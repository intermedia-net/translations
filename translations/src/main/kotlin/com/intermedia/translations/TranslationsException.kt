package com.intermedia.translations

import java.lang.Exception

class TranslationsException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
