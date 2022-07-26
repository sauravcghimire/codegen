package com.nando.codegen.data

class ApiContract {
    object EndPoints {
        //User Management
        object UserManagement {
            const val USER = "user"
            const val USER_LOGIN = "user_login"
        }
    }

    object Keys {
        const val CONTENT_TYPE = "Content-Type"
        const val AUTHORIZATION = "Authorization"
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val PAGE = "page"
        const val QUERY = "q"
        const val MESSAGE = "message"
    }

    object Values {
        const val APPLICATION_JSON = "application/json"
    }

    object NetworkCodes {
        const val NO_INTERNET_CONNECTION = 600
        const val NO_CONTENT_FOUND = 601
        const val FORBIDDEN_CODE = 602
        const val SUCCESS = "200"
        const val LEGAL_AGREEMENT = 451
        const val UNAUTHENTICATED = "UNAUTHENTICATED"
        const val FORBIDDEN = "FORBIDDEN"
        const val LEGAL_AGREEMENT_CODE_MESSAGE =
            "android_${LEGAL_AGREEMENT}_message" //unique internal message
    }
}