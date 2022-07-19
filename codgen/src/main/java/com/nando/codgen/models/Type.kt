package com.nando.codgen.models

data class Type(
    val formatId: Int,
    val name: String,
    val nullable: Boolean,
    val of: Type,
    val typeId: Int
)