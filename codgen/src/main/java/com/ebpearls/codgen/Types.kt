package com.ebpearls.codgen

object MetaType{
    const val MODEL = 0
    const val ENUM = 1
    const val LIST = 2
    const val STRING = 3
    const val BOOLEAN  = 4
    const val NUMBER = 5
}

object MetaNumberFormat{
    const val INTEGER = 0
    const val FLOAT = 1
    const val LONG = 2
    const val DOUBLE = 3
}

object MetaHttpMethod{
    const val POST = 0
    const val GET = 1
    const val PUT = 2
    const val DELETE = 3
    const val HEAD = 4
    const val OPTION = 5
}

object MetaParameterSource{
    const val BODY = 0
    const val URI = 1
    const val QUERY = 2
}