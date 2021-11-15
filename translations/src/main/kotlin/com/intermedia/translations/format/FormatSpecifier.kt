package com.intermedia.translations.format

data class FormatSpecifier(
    private val token: String,
    private val index: Int = UNKNOWN
) : Comparable<FormatSpecifier> {

    constructor(
        token: String,
        index: String,
    ) : this(
        token,
        if (index.isEmpty()) {
            UNKNOWN
        } else {
            Integer.valueOf(index)
        }
    )

    override operator fun compareTo(other: FormatSpecifier): Int {
        return index.compareTo(other.index)
    }

    override fun toString(): String {
        return "FormatSpecifier(token='$token', index=$index)"
    }

    companion object {
        private const val UNKNOWN = -1
    }
}
