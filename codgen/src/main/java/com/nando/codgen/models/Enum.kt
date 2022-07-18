package com.nando.codgen.models

data class Enum(
    val name: String,
    val properties: List<EnumProperty>
)