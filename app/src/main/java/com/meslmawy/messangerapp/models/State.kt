package com.meslmawy.messangerapp.models

class State(
    val status: Status,
    val error: Throwable? = null
) {
    enum class Status {
        LOADING, SUCCESS, ERROR
    }
}
