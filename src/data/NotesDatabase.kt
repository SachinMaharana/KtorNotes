package com.sachinmaharana.com.data

import com.sachinmaharana.com.data.collections.Note
import com.sachinmaharana.com.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("NotesDatabase")
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean {
    return users.findOne(User::email eq email) != null
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return actualPassword == passwordToCheck
}

suspend fun getNotesForUser(email: String): List<Note> {
    return notes.find(Note::owners contains email).toList()
}

suspend fun saveNote(note: Note): Boolean {
    val noteExists = notes.findOneById(note.id) != null
    return if (noteExists) {
        notes.updateOneById(note.id, note).wasAcknowledged()
    } else {
        notes.insertOne(note).wasAcknowledged()
    }
}

suspend fun deleteNoteForUser(email: String, noteId: String): Boolean {
    val note = notes.findOne(Note::id eq noteId, Note::owners contains email)
    note?.let {
        if (it.owners.size > 1) {
            val newOwners = it.owners - email
            val updateResult = notes.updateOne(Note::id eq it.id, setValue(Note::owners, newOwners))
            return updateResult.wasAcknowledged()
        }
        return notes.deleteOneById(note.id).wasAcknowledged()
    } ?: return false
}

suspend fun isOwnerOfNote(noteId: String, owner: String): Boolean {
    val note = notes.findOneById(noteId) ?: return false
    return owner in note.owners
}

suspend fun addOwnerToNote(noteID: String, owner: String): Boolean {
    val owners = notes.findOneById(noteID)?.owners ?: return false
    return notes.updateOneById(noteID, setValue(Note::owners, owners + owner)).wasAcknowledged()
}