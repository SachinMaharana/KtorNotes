package com.sachinmaharana.com.routes

import com.sachinmaharana.com.data.*
import com.sachinmaharana.com.data.collections.Note
import com.sachinmaharana.com.data.requests.AddOwnerRequest
import com.sachinmaharana.com.data.requests.DeleteNoteRequest
import com.sachinmaharana.com.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val notes = getNotesForUser(email)
                call.respond(HttpStatusCode.OK, notes)
            }
        }
    }

    route("/addOwnerToNote") {
        authenticate {
            post {
                val request = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (!checkIfUserExists(request.owner)) {
                    call.respond(
                        HttpStatusCode.OK, SimpleResponse(false, "No User with this email exist")
                    )
                    return@post
                }
                if (isOwnerOfNote(request.noteId, request.owner)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(false, "the user is already owner"))
                    return@post
                }

                if (addOwnerToNote(request.noteId, request.owner)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "${request.owner} can see this note."))
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }

    }

    route("/deleteNote") {
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (deleteNoteForUser(email, request.id)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }

    route("/addNote") {
        authenticate {
            post {
                val note = try {
                    call.receive<Note>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (saveNote((note))) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }
}