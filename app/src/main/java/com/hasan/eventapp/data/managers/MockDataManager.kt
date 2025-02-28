package com.hasan.eventapp.data.managers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hasan.eventapp.R
import com.hasan.eventapp.data.models.Event
import com.hasan.eventapp.data.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader

class MockDataManager(private val context: Context) {

    private val gson = Gson()
    private val usersFileName = "users.json"
    private val eventsFileName = "events.json"

    // Get the file in internal storage
    private fun getUsersFile(): File = File(context.filesDir, usersFileName)
    private fun getEventsFile(): File = File(context.filesDir, eventsFileName)

    // Initialize data from raw resources if files don't exist
    suspend fun initializeDataIfNeeded() = withContext(Dispatchers.IO) {
        // Initialize users if needed
        if (!getUsersFile().exists()) {
            val users = readUsersFromRaw()
            saveUsersToFile(users)
        }

        // Initialize events if needed
        if (!getEventsFile().exists()) {
            val events = readEventsFromRaw()
            saveEventsToFile(events)
        }
    }

    // Read from raw resource
    private suspend fun readUsersFromRaw(): List<User> = withContext(Dispatchers.IO) {
        try {
            context.resources.openRawResource(R.raw.users).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val type = object : TypeToken<List<User>>() {}.type
                    return@withContext gson.fromJson<List<User>>(reader, type) ?: emptyList()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    private suspend fun readEventsFromRaw(): List<Event> = withContext(Dispatchers.IO) {
        try {
            context.resources.openRawResource(R.raw.events).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val type = object : TypeToken<List<Event>>() {}.type
                    return@withContext gson.fromJson<List<Event>>(reader, type) ?: emptyList()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    // Read Users from File
    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        val file = getUsersFile()
        if (!file.exists()) {
            return@withContext emptyList()
        }

        try {
            FileReader(file).use { reader ->
                val type = object : TypeToken<List<User>>() {}.type
                return@withContext gson.fromJson<List<User>>(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    // Read Events from File
    suspend fun getEvents(): List<Event> = withContext(Dispatchers.IO) {
        val file = getEventsFile()
        if (!file.exists()) {
            return@withContext emptyList()
        }

        try {
            FileReader(file).use { reader ->
                val type = object : TypeToken<List<Event>>() {}.type
                return@withContext gson.fromJson<List<Event>>(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    // Save Users to File
    private suspend fun saveUsersToFile(users: List<User>): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = getUsersFile()
            FileWriter(file).use { writer ->
                val json = gson.toJson(users)
                writer.write(json)
                writer.flush()
            }
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    // Save Events to File
    private suspend fun saveEventsToFile(events: List<Event>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val file = getEventsFile()
                FileWriter(file).use { writer ->
                    val json = gson.toJson(events)
                    writer.write(json)
                    writer.flush()
                }
                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }

    // Add New User
    suspend fun saveUser(newUser: User): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentUsers = getUsers().toMutableList()

            // Check if user with this email already exists
            if (currentUsers.any { it.email == newUser.email }) {
                return@withContext false
            }

            currentUsers.add(newUser)
            return@withContext saveUsersToFile(currentUsers)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}