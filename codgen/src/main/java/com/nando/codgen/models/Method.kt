package com.nando.codgen.models

data class Method(
    val authorized: Boolean,
    val httpMethod: Int,
    val name: String,
    val parameter: List<Parameter>,
    val path: String,
    val returnType: Type
)