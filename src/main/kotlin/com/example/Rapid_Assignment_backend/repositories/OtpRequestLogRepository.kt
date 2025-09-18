package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.OtpRequestLog
import com.example.Rapid_Assignment_backend.utils.EnumsRole
import com.example.Rapid_Assignment_backend.utils.EnumsType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface OtpRequestLogRepository : MongoRepository<OtpRequestLog, String> {
    fun countByTypeAndRoleAndEmailAndRequestedAtAfter(
        type: EnumsType, role: EnumsRole, email: String, requestedAt: Instant
    ): Int
}