package com.joshrojas.photosearch.view.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date
import java.util.regex.Pattern

class FormatterTest {

    @Test
    fun dateFormatter_FormatsDateCorrectly() {
        // given
        val expectedDate = "Jan 04 2024"
        val date = Date(1704382512829) // Date of 01/04/2024

        // when
        val formattedDate = date.format()

        // then
        assertEquals(expectedDate, formattedDate)
    }

    @Test
    fun dateFormatter_FormatsDateWithCorrectFormat() {
        // given
        val expectedDateRegex =
            "^(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) [01][0-9] \\d{4}\$"
        val date = Date(1704382512829) // Date of 01/04/2024

        // when
        val formattedDate = date.format()

        // then
        assert(Pattern.matches(expectedDateRegex, formattedDate))
    }
}