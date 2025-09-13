package com.example.Rapid_Assignment_backend.configuration

import com.example.Rapid_Assignment_backend.domain.model.Session

object SessionContext {
    private val currentSession = ThreadLocal<Session?>()

    fun setSession(session: Session?) {
        currentSession.set(session)
    }

    fun getSession(): Session {
        return currentSession.get() ?: throw IllegalArgumentException("Unauthorized")
    }

    fun clearSession() {
        currentSession.remove()
    }
}