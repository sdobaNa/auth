package ru.cobalt42.auth.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.cobalt42.auth.dto.Authorization
import ru.cobalt42.auth.service.AuthorizationService

@CrossOrigin
@RestController
@RequestMapping("api/auth")
class AuthorizationController(
    private val service: AuthorizationService,
) {

    @PostMapping("/generate")
    fun generate(
        @RequestBody authorization: Authorization,
        @RequestParam(defaultValue = "false", required = false) isAdminPanel: Boolean,
    ) = ResponseEntity.ok(service.generate(authorization, isAdminPanel))

    @GetMapping("/refresh")
    fun refresh(
        @RequestHeader("Authorization") authToken: String,
    ) = ResponseEntity.ok(service.refresh(authToken))

    @GetMapping("/refresh/{uid}")
    fun changeProject(
        @RequestHeader("Authorization") authToken: String,
        @PathVariable("uid", required = false) projectUid: String
    ) = ResponseEntity.ok(service.changeProject(projectUid, authToken))

}