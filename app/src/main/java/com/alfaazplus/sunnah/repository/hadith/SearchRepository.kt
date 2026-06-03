package com.alfaazplus.sunnah.repository.hadith

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.api.JsonHelper
import com.alfaazplus.sunnah.db.dao.SearchIndexDao
import com.alfaazplus.sunnah.db.databases.SearchIndexDatabase
import com.alfaazplus.sunnah.db.entities.search.SearchContentEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithBlock
import com.alfaazplus.sunnah.db.entities.v2.HadithBlockType
import com.alfaazplus.sunnah.ui.search.HadithSearchResult
import com.alfaazplus.sunnah.ui.search.HadithSearchRow
import com.alfaazplus.sunnah.ui.search.SearchIndexMatchRow
import com.alfaazplus.sunnah.ui.search.SearchMatchingStrategy
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.reader.ReaderItemsBuilder
import com.alfaazplus.sunnah.ui.utils.text.stripHtml
import com.alfaazplus.sunnah.ui.utils.text.textStyleForLang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

class SearchRepository(
    private val database: SearchIndexDatabase,
    private val hadithRepo: HadithRepository,
) {
    private val dao get() = database.searchIndexDao

    suspend fun buildIndexForLangIfNeeded(langCode: String, rebuildFts: Boolean = true): Boolean {
        val fingerprint = hadithRepo.getSearchIndexFingerprint(langCode)
        if (dao.getFingerprint(SearchIndexDao.metaKey(langCode)) == fingerprint) {
            return false
        }

        buildIndexForLang(langCode, fingerprint)

        if (rebuildFts) {
            dao.rebuildFtsIndex()
        }

        return true
    }

    suspend fun buildAllIndexes() {
        var anyUpdated = false
        hadithRepo
            .getSearchIndexLangCodes()
            .forEach { langCode ->
                if (buildIndexForLangIfNeeded(langCode, rebuildFts = false)) {
                    anyUpdated = true
                }
            }

        if (anyUpdated) {
            dao.rebuildFtsIndex()
        }
    }

    suspend fun removeLang(langCode: String) {
        dao.removeLang(langCode)
    }

    fun searchHadiths(
        query: String,
        collectionIds: Set<String>?,
        matchingStrategy: SearchMatchingStrategy,
        color: Color,
        displayLangCode: String,
    ): Flow<PagingData<HadithSearchResult>> {
        Logger.d("Searching FTS hadiths with query: $query", "CollectionIds: $collectionIds")

        val matchQuery = query.toFtsMatchQuery(matchingStrategy)
        if (matchQuery == null) {
            return kotlinx.coroutines.flow.flow { emit(PagingData.empty()) }
        }

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = true,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = {
                HadithFtsPagingSource(
                    searchRepo = this,
                    matchQuery = matchQuery,
                    collectionIds = collectionIds
                        .orEmpty()
                        .toList(),
                    displayLangCode = displayLangCode,
                )
            },
        ).flow.map { pagingData ->
            pagingData.map { row ->
                row.toSearchResult(query, matchingStrategy, color, displayLangCode)
            }
        }
    }

    fun searchBooks(
        query: String,
        collectionIds: Set<String>?,
        displayLangCode: String,
    ) = hadithRepo.searchBooks(query, collectionIds, displayLangCode)

    suspend fun getHadithMatches(
        matchQuery: String,
        collectionIds: List<String>,
        displayLangCode: String,
        limit: Int,
        offset: Int,
    ): List<SearchIndexMatchRow> {
        return dao.searchHadithMatches(
            matchQuery = matchQuery,
            collectionIds = collectionIds,
            collectionCount = collectionIds.size,
            displayLangCode = displayLangCode,
            limit = limit,
            offset = offset,
        )
    }

    suspend fun countHadithMatches(
        matchQuery: String,
        collectionIds: List<String>,
    ): Int {
        return dao.countHadithMatches(
            matchQuery = matchQuery,
            collectionIds = collectionIds,
            collectionCount = collectionIds.size,
        )
    }

    suspend fun getHadithSearchRows(
        matches: List<SearchIndexMatchRow>,
        displayLangCode: String,
    ): List<HadithSearchRow> {
        if (matches.isEmpty()) return emptyList()

        val rowsById = matches
            .groupBy { it.matchedLang }
            .flatMap { (matchedLang, langMatches) ->
                hadithRepo.getHadithSearchRowsByIds(
                    hadithIds = langMatches.map { it.hadithId },
                    matchedLangCode = matchedLang,
                    displayLangCode = displayLangCode,
                )
            }
            .associateBy { it.hadithId }

        return matches.mapNotNull { match ->
            rowsById[match.hadithId]?.copy(
                blocksJson = match.matchedText,
                matchedLang = match.matchedLang,
            )
        }
    }

    private suspend fun buildIndexForLang(langCode: String, fingerprint: String) {
        val rows = hadithRepo.getSearchIndexSourceRows(langCode)
        val maxConcurrency = Runtime
            .getRuntime()
            .availableProcessors()
            .coerceIn(1, 4)
        val semaphore = Semaphore(maxConcurrency)

        val content = withContext(Dispatchers.Default) {
            rows
                .chunked(500)
                .map { chunk ->
                    async {
                        semaphore.withPermit {
                            chunk.mapNotNull { row ->
                                val text = row.blocksJson.toPlainSearchText()
                                if (text.isBlank()) null
                                else SearchContentEntity(
                                    hadithId = row.hadithId,
                                    collectionId = row.collectionId,
                                    langCode = row.langCode,
                                    text = text,
                                )
                            }
                        }
                    }
                }
                .awaitAll()
                .flatten()
        }

        dao.replaceLang(langCode, content, fingerprint)
    }

    private fun HadithSearchRow.toSearchResult(
        query: String,
        matchingStrategy: SearchMatchingStrategy,
        highlightColor: Color,
        displayLangCode: String,
    ): HadithSearchResult {
        return HadithSearchResult(
            hadithId = hadithId,
            bookId = bookId,
            collectionId = collectionId,
            numbering = ReaderItemsBuilder.buildNumbering(
                collectionName,
                hadithNumber,
                displayLangCode,
            ),
            matchedLang = matchedLang,
            snippetText = blocksJson.toSearchSnippet(query, matchingStrategy, highlightColor, matchedLang),
        )
    }
}

private class HadithFtsPagingSource(
    private val searchRepo: SearchRepository,
    private val matchQuery: String,
    private val collectionIds: List<String>,
    private val displayLangCode: String,
) : PagingSource<Int, HadithSearchRow>() {
    override fun getRefreshKey(state: PagingState<Int, HadithSearchRow>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(page.data.size) ?: page.nextKey?.minus(page.data.size)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HadithSearchRow> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize
            val matches = searchRepo.getHadithMatches(
                matchQuery = matchQuery,
                collectionIds = collectionIds,
                displayLangCode = displayLangCode,
                limit = limit,
                offset = offset,
            )
            val rows = searchRepo.getHadithSearchRows(matches, displayLangCode)

            LoadResult.Page(
                data = rows,
                prevKey = if (offset == 0) null else (offset - limit).coerceAtLeast(0),
                nextKey = if (rows.size < limit) null else offset + rows.size,
                itemsBefore = offset,
                itemsAfter = searchRepo
                    .countHadithMatches(matchQuery, collectionIds)
                    .minus(offset + rows.size)
                    .coerceAtLeast(0),
            )
        } catch (e: Exception) {
            Logger.e(e, "Hadith FTS search failed", "matchQuery=$matchQuery")
            LoadResult.Error(e)
        }
    }
}

internal fun String.toFtsMatchQuery(strategy: SearchMatchingStrategy): String? {
    if (strategy == SearchMatchingStrategy.EXACT_PHRASE) {
        return normalizeSearchPhrase()
            .takeIf { it.isNotBlank() }
            ?.let { phrase -> "\"${phrase.replace("\"", "\"\"")}\"" }
    }

    val tokenRegex = Regex("""("[^"]+"|\bNEAR(?:[/:]\d+)?\b|[\p{L}\p{N}\p{M}_]+)""", RegexOption.IGNORE_CASE)
    val matches = tokenRegex
        .findAll(this)
        .toList()

    val tokens = matches.mapNotNull { match ->
        val text = match.value

        if (text.startsWith("\"") && text.endsWith("\"") && text.length > 2) {
            val inner = text.substring(1, text.length - 1)
            val normalized = inner.normalizeSearchPhrase()
            if (normalized.isNotBlank()) {
                QueryToken.Term("\"${normalized.replace("\"", "\"\"")}\"")
            } else {
                null
            }
        } else if (text.equals("NEAR", ignoreCase = true) || text.startsWith("NEAR/", ignoreCase = true) || text.startsWith(
                "NEAR:", ignoreCase = true
            )
        ) {
            val distance = text
                .substring(4)
                .removePrefix("/")
                .removePrefix(":")
                .toIntOrNull()
            QueryToken.Near(distance)
        } else {
            val normalized = text.lowercase()
            if (normalized.isNotBlank()) {
                QueryToken.Term("$normalized*")
            } else {
                null
            }
        }
    }

    if (tokens.isEmpty()) return null

    val result = mutableListOf<String>()
    val currentNearGroup = mutableListOf<String>()
    var currentNearDistance: Int? = null

    var i = 0
    while (i < tokens.size) {
        val token = tokens[i]

        if (token is QueryToken.Near) {
            val hasPrev = currentNearGroup.isNotEmpty() || result.isNotEmpty()
            val nextToken = tokens.getOrNull(i + 1)
            val hasNext = nextToken is QueryToken.Term

            if (hasPrev && hasNext) {
                if (currentNearGroup.isEmpty() && result.isNotEmpty()) {
                    val prevTerm = result.removeAt(result.size - 1)
                    currentNearGroup.add(prevTerm)
                }
                if (token.distance != null) {
                    currentNearDistance = if (currentNearDistance == null) token.distance else minOf(currentNearDistance, token.distance)
                }
                val nextTerm = tokens[i + 1] as QueryToken.Term
                currentNearGroup.add(nextTerm.text)
                i += 2
            } else {
                if (currentNearGroup.isNotEmpty()) {
                    result.add(formatNearGroup(currentNearGroup, currentNearDistance))
                    currentNearGroup.clear()
                    currentNearDistance = null
                }
                result.add("near*")
                i++
            }
        } else if (token is QueryToken.Term) {
            if (currentNearGroup.isNotEmpty()) {
                result.add(formatNearGroup(currentNearGroup, currentNearDistance))
                currentNearGroup.clear()
                currentNearDistance = null
            }

            result.add(token.text)

            i++
        }
    }

    if (currentNearGroup.isNotEmpty()) {
        result.add(formatNearGroup(currentNearGroup, currentNearDistance))
    }

    val operator = when (strategy) {
        SearchMatchingStrategy.ANY_WORD -> " OR "
        SearchMatchingStrategy.ALL_WORDS -> " "
    }

    return result
        .takeIf { it.isNotEmpty() }
        ?.joinToString(operator)
}

private sealed interface QueryToken {
    data class Term(val text: String) : QueryToken
    data class Near(val distance: Int?) : QueryToken
}

private fun formatNearGroup(terms: List<String>, distance: Int?): String {
    val joined = terms.joinToString(" ")
    return if (distance != null) "NEAR($joined, $distance)" else "NEAR($joined)"
}

private fun String.normalizeSearchPhrase(): String {
    return Regex("[\\p{L}\\p{N}\\p{M}_]+")
        .findAll(this)
        .joinToString(" ") { it.value.lowercase() }
}

private fun String.toPlainSearchText(): String = try {
    JsonHelper.json
        .decodeFromString<List<HadithBlock>>(this)
        .asSequence()
        .filter {
            it.type == HadithBlockType.MATN || it.type == HadithBlockType.TRANSLATION || it.type == HadithBlockType.NARRATOR || it.type == HadithBlockType.SANAD
        }
        .mapNotNull { block ->
            block.text
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?.stripHtml()
                ?.takeIf { it.isNotEmpty() }
        }
        .joinToString(" ")
} catch (_: Exception) {
    ""
}


private fun String.toSearchSnippet(
    query: String,
    strategy: SearchMatchingStrategy,
    highlightColor: Color,
    langCode: String,
): AnnotatedString {
    val textStyle = textStyleForLang(langCode)
    val highlightStyle = SpanStyle(
        color = highlightColor,
        fontWeight = FontWeight.Bold,
        background = highlightColor.alpha(0.15f),
    )

    if (isBlank() || query.isBlank()) return buildAnnotatedString { }

    val source = this
    val patterns = getHighlightPatterns(query, strategy)

    var earliestMatchStart = -1
    var earliestMatchLength = 0

    for (pattern in patterns) {
        val matchResult = pattern.find(source)
        if (matchResult != null) {
            val start = matchResult.range.first
            if (earliestMatchStart == -1 || start < earliestMatchStart) {
                earliestMatchStart = start
                earliestMatchLength = matchResult.value.length
            }
        }
    }

    val snippetBody = when {
        source.length <= 400 -> source
        earliestMatchStart == -1 -> source.take(400)
        else -> {
            val highlightEnd = (earliestMatchStart + earliestMatchLength).coerceAtMost(source.length)
            val padding = 200
            val preContextStart = (earliestMatchStart - padding).coerceAtLeast(0)
            val postContextEnd = (highlightEnd + padding).coerceAtMost(source.length)
            buildString {
                if (preContextStart > 0) append("...")
                append(source, preContextStart, postContextEnd)
                if (postContextEnd < source.length) append("...")
            }
        }
    }

    return buildAnnotatedString {
        withStyle(textStyle.toParagraphStyle()) {
            withStyle(textStyle.toSpanStyle()) {
                append(snippetBody)
            }
        }
        for (pattern in patterns) {
            pattern
                .findAll(snippetBody)
                .forEach { matchResult ->
                    val start = matchResult.range.first
                    val end = matchResult.range.last + 1
                    addStyle(highlightStyle, start, end)
                }
        }
    }
}

internal fun getHighlightPatterns(query: String, strategy: SearchMatchingStrategy): List<Regex> {
    if (query.isBlank()) return emptyList()

    return when (strategy) {
        SearchMatchingStrategy.EXACT_PHRASE -> {
            val words = Regex("[\\p{L}\\p{N}\\p{M}_]+")
                .findAll(query)
                .map { it.value }
                .toList()
            if (words.isEmpty()) return emptyList()
            val pattern = "(?i)\\b" + words.joinToString(separator = "\\W+") { Regex.escape(it) } + "\\b"
            listOf(Regex(pattern))
        }

        SearchMatchingStrategy.ANY_WORD, SearchMatchingStrategy.ALL_WORDS -> {
            val quotedPhrases = Regex("\"([^\"]+)\"")
                .findAll(query)
                .mapNotNull { match ->
                    val phrase = match.groupValues.getOrNull(1)
                    if (phrase.isNullOrBlank()) null else phrase
                }
                .toList()

            val unquoted = query.replace(Regex("\"([^\"]+)\""), " ")
            val cleanedUnquoted = unquoted.replace(Regex("(?i)\\bNEAR(?:[/:]\\d+)?\\b"), " ")
            val terms = Regex("[\\p{L}\\p{N}\\p{M}_]+")
                .findAll(cleanedUnquoted)
                .map { it.value }
                .filter { it.isNotBlank() }
                .distinct()
                .toList()

            val patterns = mutableListOf<Regex>()

            quotedPhrases.forEach { phrase ->
                val words = Regex("[\\p{L}\\p{N}\\p{M}_]+")
                    .findAll(phrase)
                    .map { it.value }
                    .toList()
                if (words.isNotEmpty()) {
                    val pattern = "(?i)\\b" + words.joinToString(separator = "\\W+") { Regex.escape(it) } + "\\b"
                    patterns.add(Regex(pattern))
                }
            }

            terms.forEach { term ->
                val pattern = "(?i)\\b" + Regex.escape(term)
                patterns.add(Regex(pattern))
            }

            patterns
        }
    }
}
