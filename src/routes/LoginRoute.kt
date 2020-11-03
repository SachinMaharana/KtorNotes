package com.sachinmaharana.com.routes

import com.sachinmaharana.com.data.checkIfUserExists
import com.sachinmaharana.com.data.checkPasswordForEmail
import com.sachinmaharana.com.data.collections.User
import com.sachinmaharana.com.data.registerUser
import com.sachinmaharana.com.data.requests.AccountRequest
import com.sachinmaharana.com.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute() {
    route("/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val isPasswordCorrect = checkPasswordForEmail(request.email, request.password)
            if (isPasswordCorrect) {
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Logged In"))
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "Email or Password is Incorrect"))
            }
        }
    }
}