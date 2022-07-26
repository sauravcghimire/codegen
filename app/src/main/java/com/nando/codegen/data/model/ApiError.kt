package com.nando.codegen.data.model

data class ApiError(
    val data: Any,
    val errors: List<Error>,
)

data class Error(
    val extensions: Extensions,
    val message: String,
)

data class Extensions(
    val code: String,
    val response: Response?,
)

data class Response(
    val message: String,
    val statusCode: Int,
)

