package com.hasan.eventapp.data.managers

import android.content.Context
import com.hasan.eventapp.domain.models.UserDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val keyUserId = "user_id"
    private val keyUserName = "user_name"
    private val keyUserEmail = "user_email"

    private val _currentUser = MutableStateFlow<UserDomain?>(null)
    val currentUser: StateFlow<UserDomain?> = _currentUser.asStateFlow()

    init {
        loadUserFromPreferences()
    }

    private fun loadUserFromPreferences() {
        val userId = prefs.getString(keyUserId, null)
        val userName = prefs.getString(keyUserName, null)
        val userEmail = prefs.getString(keyUserEmail, null)

        if (userId != null && userName != null && userEmail != null) {
            _currentUser.value = UserDomain(
                id = userId,
                name = userName,
                email = userEmail
            )
        }
    }

    fun setCurrentUser(user: UserDomain) {
        prefs.edit()
            .putString(keyUserId, user.id)
            .putString(keyUserName, user.name)
            .putString(keyUserEmail, user.email)
            .apply()

        _currentUser.value = user
    }

    fun getCurrentUserId(): String {
        return _currentUser.value?.id ?: prefs.getString(keyUserId, "") ?: ""
    }

    fun clearCurrentUser() {
        prefs.edit().clear().apply()
        _currentUser.value = null
    }
}