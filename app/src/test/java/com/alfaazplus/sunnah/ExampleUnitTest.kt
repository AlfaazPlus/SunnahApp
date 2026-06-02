package com.alfaazplus.sunnah

import com.alfaazplus.sunnah.repository.hadith.getHighlightPatterns
import com.alfaazplus.sunnah.ui.search.SearchMatchingStrategy
import org.junit.Assert.*
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
}

