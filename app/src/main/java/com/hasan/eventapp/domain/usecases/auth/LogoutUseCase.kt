package com.hasan.eventapp.domain.usecases.auth

import com.hasan.eventapp.utils.SessionManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    fun logout() {
        sessionManager.clearSession()
    }
}