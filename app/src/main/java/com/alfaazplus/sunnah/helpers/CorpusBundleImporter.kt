package com.alfaazplus.sunnah.helpers

import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.db.entities.v2.BookEntity
import com.alfaazplus.sunnah.db.entities.v2.BookTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.ChapterEntity
import com.alfaazplus.sunnah.db.entities.v2.ChapterTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionType
import com.alfaazplus.sunnah.db.entities.v2.HadithContentEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeType
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithNarratorEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceType
import com.alfaazplus.sunnah.db.entities.v2.HadithRelatedEntity
import com.alfaazplus.sunnah.deliverable.v1.CorpusBundle

private const val EXPECTED_CORPUS_SCHEMA_VERSION = 1

private const val BATCH_WIDE = 400
private const val BATCH_HADITH = 150
private const val BATCH_CONTENT = 50

private fun String?.nullIfBlank(): String? = this?.takeIf { it.isNotBlank() }

/**
 * Pre-mapped Room rows for one [CorpusBundle]. Built **outside** a DB transaction so the write
 * transaction only runs inserts.
 */
data class CorpusImportPayload(
    val collections: List<CollectionEntity>,
    val books: List<BookEntity>,
    val chapters: List<ChapterEntity>,
    val hadiths: List<HadithEntity>,
    val collectionTranslations: List<CollectionTranslationEntity>,
    val bookTranslations: List<BookTranslationEntity>,
    val chapterTranslations: List<ChapterTranslationEntity>,
    val hadithContents: List<HadithContentEntity>,
    val hadithReferences: List<HadithReferenceEntity>,
    val hadithRelated: List<HadithRelatedEntity>,
    val hadithGrades: List<HadithGradeEntity>,
    val hadithNarrators: List<HadithNarratorEntity>,
)

/**
 * Maps protobuf -> Room entities. Returns `null` if the bundle is skipped (wrong schema).
 *
 * hadith_related rows whose endpoints are not both in this bundle’s hadith ids are dropped,
 * because Room enforces both FKs on [HadithRelatedEntity].
 */
fun CorpusBundle.toImportPayloadOrNull(): CorpusImportPayload? {
    if (schemaVersion != EXPECTED_CORPUS_SCHEMA_VERSION) {
        Logger.d(
            "CorpusBundleImporter: skipping bundle corpus_id=$corpusId (schema_version=$schemaVersion, expected=$EXPECTED_CORPUS_SCHEMA_VERSION)"
        )
        return null
    }

    val hadithIds = hadithsList
        .map { it.id }
        .toSet()

    val collections = ArrayList<CollectionEntity>(collectionsList.size)

    for (row in collectionsList) {
        collections.add(
            CollectionEntity(
                id = row.id,
                type = CollectionType.fromType(row.type),
                sortOrder = row.sortOrder,
                hasVolumes = row.hasVolumes,
                hasBooks = row.hasBooks,
                hasChapters = row.hasChapters,
                numberingSource = row.numberingSource.nullIfBlank(),
            )
        )
    }

    val books = ArrayList<BookEntity>(booksList.size)
    for (row in booksList) {
        books.add(
            BookEntity(
                id = row.id,
                collectionId = row.collectionId,
                number = row.number.nullIfBlank(),
            )
        )
    }

    val chapters = ArrayList<ChapterEntity>(chaptersList.size)
    for (row in chaptersList) {
        chapters.add(
            ChapterEntity(
                id = row.id,
                collectionId = row.collectionId,
                bookId = row.bookId,
                number = row.number.nullIfBlank(),
            )
        )
    }

    val hadiths = ArrayList<HadithEntity>(hadithsList.size)
    for (row in hadithsList) {
        val urn = when {
            row.hasUrn() -> row.urn
            else -> null
        }

        hadiths.add(
            HadithEntity(
                id = row.id,
                urn = urn,
                collectionId = row.collectionId,
                bookId = row.bookId,
                chapterId = if (row.hasChapterId()) row.chapterId.nullIfBlank() else null,
                number = row.number.nullIfBlank(),
            )
        )
    }

    val collectionTranslations = ArrayList<CollectionTranslationEntity>(collectionTranslationsList.size)
    for (row in collectionTranslationsList) {
        collectionTranslations.add(
            CollectionTranslationEntity(
                collectionId = row.collectionId,
                lang = row.lang,
                title = row.title.nullIfBlank(),
                intro = row.intro.nullIfBlank(),
                description = row.description.nullIfBlank(),
            )
        )
    }

    val bookTranslations = ArrayList<BookTranslationEntity>(bookTranslationsList.size)
    for (row in bookTranslationsList) {
        bookTranslations.add(
            BookTranslationEntity(
                bookId = row.bookId,
                lang = row.lang,
                title = row.title.nullIfBlank(),
                intro = row.intro.nullIfBlank(),
                preamble = row.preamble.nullIfBlank(),
                notes = row.notes.nullIfBlank(),
            )
        )
    }

    val chapterTranslations = ArrayList<ChapterTranslationEntity>(chapterTranslationsList.size)
    for (row in chapterTranslationsList) {
        chapterTranslations.add(
            ChapterTranslationEntity(
                chapterId = row.chapterId,
                lang = row.lang,
                title = row.title.ifBlank { " " },
                intro = row.intro.nullIfBlank(),
            )
        )
    }

    val hadithContents = ArrayList<HadithContentEntity>(hadithContentsList.size)
    for (row in hadithContentsList) {
        hadithContents.add(
            HadithContentEntity(
                hadithId = row.hadithId,
                lang = row.lang,
                blocksJson = row.blocksJson,
            )
        )
    }

    val hadithReferences = ArrayList<HadithReferenceEntity>(hadithReferencesList.size)
    for (row in hadithReferencesList) {
        try {
            hadithReferences.add(
                HadithReferenceEntity(
                    hadithId = row.hadithId,
                    type = HadithReferenceType.fromValue(row.type),
                    value = row.value,
                )
            )
        } catch (e: Exception) {
            Logger.d("CorpusBundleImporter: skip hadith_reference ${row.hadithId} type=${row.type}: ${e.message}")
        }
    }

    val hadithRelated = ArrayList<HadithRelatedEntity>(hadithRelatedList.size)
    for (row in hadithRelatedList) {
        if (row.hadithId in hadithIds && row.relatedHadithId in hadithIds) {
            hadithRelated.add(
                HadithRelatedEntity(
                    hadithId = row.hadithId,
                    relatedHadithId = row.relatedHadithId,
                )
            )
        }
    }

    val hadithGrades = ArrayList<HadithGradeEntity>(hadithGradesList.size)
    for (row in hadithGradesList) {
        hadithGrades.add(
            HadithGradeEntity(
                hadithId = row.hadithId,
                gradeType = HadithGradeType.fromValue(row.gradeId),
                label = row.label,
                lang = row.lang,
            )
        )
    }

    val hadithNarrators = ArrayList<HadithNarratorEntity>(hadithNarratorsList.size)
    for (row in hadithNarratorsList) {
        hadithNarrators.add(
            HadithNarratorEntity(
                hadithId = row.hadithId,
                source = row.source,
                narratorId = row.narratorId,
                position = row.position,
            )
        )
    }

    return CorpusImportPayload(
        collections = collections,
        books = books,
        chapters = chapters,
        hadiths = hadiths,
        collectionTranslations = collectionTranslations,
        bookTranslations = bookTranslations,
        chapterTranslations = chapterTranslations,
        hadithContents = hadithContents,
        hadithReferences = hadithReferences,
        hadithRelated = hadithRelated,
        hadithGrades = hadithGrades,
        hadithNarrators = hadithNarrators,
    )
}

/**
 * Inserts one corpus in FK-safe order. Call inside withWriteTransaction so one corpus
 * commits in a single transaction.
 */
suspend fun HadithDatabase.insertCorpusImportPayload(payload: CorpusImportPayload) {
    val dao = importDao

    for (chunk in payload.collections.chunked(BATCH_WIDE)) {
        dao.insertCollections(chunk)
    }

    for (chunk in payload.books.chunked(BATCH_WIDE)) {
        dao.insertBooks(chunk)
    }

    for (chunk in payload.chapters.chunked(BATCH_WIDE)) {
        dao.insertChapters(chunk)
    }

    for (chunk in payload.hadiths.chunked(BATCH_HADITH)) {
        dao.insertHadiths(chunk)
    }

    for (chunk in payload.collectionTranslations.chunked(BATCH_WIDE)) {
        dao.insertCollectionTranslations(chunk)
    }

    for (chunk in payload.bookTranslations.chunked(BATCH_WIDE)) {
        dao.insertBookTranslations(chunk)
    }

    for (chunk in payload.chapterTranslations.chunked(BATCH_WIDE)) {
        dao.insertChapterTranslations(chunk)
    }

    for (chunk in payload.hadithContents.chunked(BATCH_CONTENT)) {
        dao.insertHadithContents(chunk)
    }

    for (chunk in payload.hadithReferences.chunked(BATCH_WIDE)) {
        dao.insertHadithReferences(chunk)
    }

    for (chunk in payload.hadithRelated.chunked(BATCH_WIDE)) {
        dao.insertHadithRelated(chunk)
    }

    for (chunk in payload.hadithGrades.chunked(BATCH_WIDE)) {
        dao.insertHadithGrades(chunk)
    }

    for (chunk in payload.hadithNarrators.chunked(BATCH_WIDE)) {
        dao.insertHadithNarrators(chunk)
    }
}
