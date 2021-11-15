package com.intermedia.translations.format

import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.Test

class FormatSpecifiersTest {
    @Test
    fun matchEmpty() {
        MatcherAssert.assertThat(
            FormatSpecifiers(""),
            IsEqual.equalTo(FormatSpecifiers(""))
        )
    }

    @Test
    fun matchStringWithoutFormatSpecifiers() {
        MatcherAssert.assertThat(
            FormatSpecifiers("Menschen"),
            IsEqual.equalTo(FormatSpecifiers("People"))
        )
    }

    @Test
    fun matchStringWithStringSpecifier() {
        MatcherAssert.assertThat(
            FormatSpecifiers("%1\$s è stato correttamente rimosso dal canale"),
            IsEqual.equalTo(FormatSpecifiers("%1\$s was successfully removed from channel"))
        )
    }

    @Test
    fun matchMultipleDifferentSpecifierWithDifferentIndexes() {
        MatcherAssert.assertThat(
            FormatSpecifiers("word %2\$d word %1\$s word"),
            IsEqual.equalTo(FormatSpecifiers("word %1\$s word %2\$d word"))
        )
    }

    @Test
    fun matchMultipleDifferentSpecifierWithoutIndexes() {
        MatcherAssert.assertThat(
            FormatSpecifiers("word %d word %s word"),
            IsEqual.equalTo(FormatSpecifiers("word %d word %s word"))
        )
    }

    @Test
    fun matchFailedMultipleDifferentSpecifierWithoutIndexes() {
        MatcherAssert.assertThat(
            FormatSpecifiers("word %d word %s word"),
            IsNot.not(IsEqual.equalTo(FormatSpecifiers("word %s word %d word")))
        )
    }

    @Test
    fun matchFailedMultipleDifferentSpecifierWithDifferentIndexes() {
        MatcherAssert.assertThat(
            FormatSpecifiers("word %2\$d word %1\$s word"),
            IsNot.not(IsEqual.equalTo(FormatSpecifiers("word %1\$d word %2\$d word")))
        )
    }

    @Test
    fun matchFailedWithStringWithoutSpecifier() {
        MatcherAssert.assertThat(
            FormatSpecifiers("empty string"),
            IsNot.not(IsEqual.equalTo(FormatSpecifiers("word %1\$d word %2\$d word")))
        )
    }
}
