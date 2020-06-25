package com.meslmawy.messangerapp.models

data class User(
    val userName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val status : String?= null,
    var search: String? = null
)