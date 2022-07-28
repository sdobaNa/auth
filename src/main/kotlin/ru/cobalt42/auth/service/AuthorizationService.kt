package ru.cobalt42.auth.service

import ru.cobalt42.auth.dto.Authorization
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.RefreshData

interface AuthorizationService {
    fun generate(authorization: Authorization, isAdminPanel: Boolean): DefaultResponse<RefreshData>
    fun refresh(authToken: String): DefaultResponse<RefreshData>
    fun changeProject(projectUid: String, authToken: String): DefaultResponse<RefreshData>
}