package com.sachinmaharana.com.routes

import com.sachinmaharana.com.data.checkIfUserExists
import com.sachinmaharana.com.data.collections.User
import com.sachinmaharana.com.data.registerUser
import com.sachinmaharana.com.data.requests.AccountRequest
import com.sachinmaharana.com.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute() {
    route("/register") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExist = checkIfUserExists(request.email)
            if (!userExist) {
                if (registerUser(User(request.email, request.password))) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Successfully created account"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, SimpleResponse(false, "An unknown error occured"))
                }
            } else {
                call.respond(HttpStatusCode.OK, "A user already exists!")
            }
        }
    }
}