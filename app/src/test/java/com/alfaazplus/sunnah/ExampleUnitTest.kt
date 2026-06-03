package com.alfaazplus.sunnah

import com.alfaazplus.sunnah.repository.hadith.getHighlightPatterns
import com.alfaazplus.sunnah.repository.hadith.toFtsMatchQuery
import com.alfaazplus.sunnah.ui.search.SearchMatchingStrategy
import com.alfaazplus.sunnah.ui.utils.text.stripHtml
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGetHighlightPatterns_exactPhrase() {
        val patterns = getHighlightPatterns("sahih al-bukhari", SearchMatchingStrategy.EXACT_PHRASE)
        assertEquals(1, patterns.size)
        val regex = patterns[0]

        // Should match exact phrase case-insensitively with punctuation between words
        assertTrue(regex.containsMatchIn("This is Sahih Al-Bukhari"))
        assertTrue(regex.containsMatchIn("sahih, al-bukhari"))
        assertFalse(regex.containsMatchIn("bukhari sahih"))
        assertFalse(regex.containsMatchIn("sahihalbukhari"))
    }

    @Test
    fun testGetHighlightPatterns_anyWord() {
        val patterns = getHighlightPatterns("\"sahih bukhari\" muslim", SearchMatchingStrategy.ANY_WORD)
        // Should find 2 patterns: one for the quoted phrase, one for the unquoted word
        assertEquals(2, patterns.size)

        val phraseRegex = patterns[0]
        val wordRegex = patterns[1]

        // Phrase regex should match "sahih bukhari" as a phrase
        assertTrue(phraseRegex.containsMatchIn("This is Sahih Bukhari"))
        assertFalse(phraseRegex.containsMatchIn("sahih al bukhari"))

        // Word regex should match "muslim" as a prefix / word starting with muslim
        assertTrue(wordRegex.containsMatchIn("This is a muslim companion"))
        assertTrue(wordRegex.containsMatchIn("Muslims say..."))
        assertFalse(wordRegex.containsMatchIn("nonmuslim"))
    }

    @Test
    fun testGetHighlightPatterns_bengali() {
        // Bengali word "কিতাব" (kitab) contains vowel marks ি and া, which are Unicode marks (\p{M})
        val patterns = getHighlightPatterns("কিতাব", SearchMatchingStrategy.ANY_WORD)
        assertEquals(1, patterns.size)
        val regex = patterns[0]

        // Should match the entire Bengali word
        assertTrue(regex.containsMatchIn("এটি একটি কিতাব"))

        // A prefix query like "কিত" (kit) should match the start of "কিতাব"
        val prefixPatterns = getHighlightPatterns("কিত", SearchMatchingStrategy.ANY_WORD)
        assertEquals(1, prefixPatterns.size)
        val prefixRegex = prefixPatterns[0]
        assertTrue(prefixRegex.containsMatchIn("কিতাব"))
        assertFalse(prefixRegex.containsMatchIn("অকিতাব"))
    }

    @Test
    fun testGetHighlightPatterns_arabicAndUrdu() {
        // Arabic word "اللَّه" (Allah) contains Shadda (ّ) which is a Unicode mark (\p{M})
        val patterns = getHighlightPatterns("اللَّه", SearchMatchingStrategy.ANY_WORD)
        assertEquals(1, patterns.size)
        val regex = patterns[0]
        assertTrue(regex.containsMatchIn("ذکر اللَّه"))

        // Urdu word with diacritics / marks: "حدیثِ" (Hadith-e) has a Zer (ِ) which is a Unicode mark (\p{M})
        val urduPatterns = getHighlightPatterns("حدیثِ", SearchMatchingStrategy.ANY_WORD)
        assertEquals(1, urduPatterns.size)
        assertTrue(urduPatterns[0].containsMatchIn("یہ حدیثِ نبوی ہے"))
    }

    @Test
    fun testStripHtml() {
        // Test empty/blank strings
        assertEquals("", "".stripHtml())
        assertEquals("", "   ".stripHtml())

        // Test strings without HTML tags
        assertEquals("hello world", "hello world".stripHtml())

        // Test basic HTML tags
        assertEquals("hello world", "hello <b>world</b>".stripHtml())
        assertEquals("hello world", "<p>hello</p> <i>world</i>".stripHtml())

        // Test line breaks / malformed looking but valid tags
        assertEquals("hello world", "hello <br/>world".stripHtml())
        assertEquals("hello world", "hello <br>world".stripHtml())

        // Test HTML entities unescaping
        assertEquals("hello & world", "hello &amp; world".stripHtml())
        assertEquals("hello < world", "hello &lt; world".stripHtml())
        assertEquals("hello > world", "hello &gt; world".stripHtml())
        assertEquals("hello \"world\"", "hello &quot;world&quot;".stripHtml())
        assertEquals("hello 'world'", "hello &apos;world&apos;".stripHtml())
        assertEquals("hello world", "hello&nbsp;world".stripHtml())

        // Test numeric and hex character entities
        assertEquals("hello ' world", "hello &#39; world".stripHtml())
        assertEquals("hello A world", "hello &#65; world".stripHtml())
        assertEquals("hello A world", "hello &#x41; world".stripHtml())

        // Test mixed tags and entities
        assertEquals("hello \"world\"", "<div class=\"test\">hello &quot;world&quot;</div>".stripHtml())
    }

    @Test
    fun testToFtsMatchQuery_nearOperators() {
        // Standard NEAR
        assertEquals("NEAR(apple* banana*)", "apple NEAR banana".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))
        assertEquals("NEAR(apple* banana*)", "apple near banana".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))
        assertEquals("NEAR(apple* banana*)", "apple Near banana".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))

        // Custom distance (slash/colon)
        assertEquals("NEAR(apple* banana*, 5)", "apple NEAR/5 banana".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))
        assertEquals("NEAR(apple* banana*, 5)", "apple NEAR:5 banana".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))

        // Multiple terms with NEAR
        assertEquals("NEAR(apple* banana* cherry*, 3)", "apple NEAR/5 banana NEAR/3 cherry".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))

        // Mixed terms
        assertEquals("NEAR(apple* banana*) OR orange*", "apple NEAR banana orange".toFtsMatchQuery(SearchMatchingStrategy.ANY_WORD))
        assertEquals("NEAR(apple* banana*) orange*", "apple NEAR banana orange".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))

        // Quoted phrases
        assertEquals("NEAR(\"hello world\" banana*, 5)", "\"hello world\" NEAR/5 banana".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))

        // Edge cases (invalid position / invalid syntax)
        // NEAR at start
        assertEquals("near* apple*", "NEAR apple".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))
        // NEAR at end
        assertEquals("apple* near*", "apple NEAR".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))
        // Standalone NEAR
        assertEquals("near*", "NEAR".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))
        // Empty query
        assertEquals(null, "".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))
        // Double NEAR
        assertEquals("apple* NEAR(near* banana*)", "apple NEAR NEAR banana".toFtsMatchQuery(SearchMatchingStrategy.ALL_WORDS))
    }

    @Test
    fun testGetHighlightPatterns_nearOmission() {
        val patterns = getHighlightPatterns("apple NEAR/5 banana", SearchMatchingStrategy.ANY_WORD)
        // Should find 2 patterns (apple, banana), omitting NEAR and 5
        assertEquals(2, patterns.size)
        assertTrue(patterns[0].pattern.contains("apple"))
        assertTrue(patterns[1].pattern.contains("banana"))

        // Make sure "near" and "5" are not matched by these patterns
        assertFalse(patterns[0].containsMatchIn("near"))
        assertFalse(patterns[1].containsMatchIn("5"))
    }
}

