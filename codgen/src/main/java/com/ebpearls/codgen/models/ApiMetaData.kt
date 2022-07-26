package com.ebpearls.codgen.models

data class ApiMetaData(
    val baseUrl: String,
    val enums: List<Enum>,
    val models: List<Model>,
    val repos: List<Repo>
)