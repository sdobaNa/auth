package ru.cobalt42.auth.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.cobalt42.auth.dto.Authorization
import ru.cobalt42.auth.dto.RefreshData
import ru.cobalt42.auth.service.AuthorizationService

@CrossOrigin
@RestController
@RequestMapping("api/auth")
class AuthorizationController(
    private val authorizationService: AuthorizationService
) {

    @PostMapping("/generate")
    fun generate(@RequestBody authorization: Authorization) =
        ResponseEntity.ok(authorizationService.generate(authorization))

    @PostMapping("/refresh")
    fun refresh(@RequestBody refreshData: RefreshData) = ResponseEntity.ok(authorizationService.refresh(refreshData))
}