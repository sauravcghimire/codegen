package com.nando.codegen.models

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class AddUserRequestModel(
  fullName: String?,
  email: String?,
  contact: List<ContactNumber>,
  age: Int?,
  address: Address,
  isSuperUser: Boolean?,
  excellencyLevel: ExcellencyLevel,
  interests: List<String>,
)
