package com.intermedia.translations.format

class FormatSpecifiers(
    private val text: String
) {

    private val specifiers: List<FormatSpecifier> by lazy {
        formatSpecifiersOf(text).sorted()
    }

    override fun hashCode(): Int {
        return specifiers.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is FormatSpecifiers) {
            other.specifiers == specifiers
        } else {
            false
        }
    }

    override fun toString(): String {
        return specifiers.toString()
    }

    private fun formatSpecifiersOf(text: String): List<FormatSpecifier> {
        val formatTokens = mutableListOf<FormatSpecifier>()
        for (matchResult in FORMAT_SPECIFIER_PATTERN.toRegex().findAll(text)) {
            val token = matchResult.value
            val index = matchResult.groupValues[2] // 2 - index of argument group
            formatTokens.add(FormatSpecifier(token, index))
        }
        return formatTokens
    }

    companion object {
        /**
         * Idea from: https://android.googlesource.com/platform/tools/base/+/refs/heads/android12-d1-release/lint/libs/lint-checks/src/main/java/com/android/tools/lint/checks/StringFormatDetector.java#800
         *
         * Generic format: %[argument_index$][flags][width][.precision]conversion
         */
        private val FORMAT_SPECIFIER_PATTERN = "%" +
                // Argument Index
                "((\\d+)\\$)?" +
                // Flags
                "([-+#, 0(<]*)?" +
                // Width
                "(\\d+)?" +
                // Precision
                "(\\.\\d+)?" +
                // Conversion. These are all a single character, except date/time
                // conversions
                // which take a prefix of t/T:
                "([tT])?" +
                // The current set of conversion characters are
                // b,h,s,c,d,o,x,e,f,g,a,t (as well as all those as upper-case
                // characters), plus
                // n for newlines and % as a literal %. And then there are all the
                // time/date
                // characters: HIKLm etc. Just match on all characters here since there
                // should
                // be at least one.
                "([a-zA-Z%])".toRegex()
    }
}
