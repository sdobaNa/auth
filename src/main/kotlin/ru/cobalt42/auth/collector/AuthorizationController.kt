package ru.cobalt42.auth.collector

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
        try {
            ResponseEntity.ok(authorizationService.generate(authorization))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).build()
        } catch (e: ArrayIndexOutOfBoundsException) {
            ResponseEntity.status(403).build()
        } catch (e: Throwable) {
            ResponseEntity.status(400).build()
        }

    @PostMapping("/refresh")
    fun refresh(@RequestBody refreshData: RefreshData) =
        try {
            ResponseEntity.ok(authorizationService.refresh(refreshData))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).build()
        } catch (e: ArrayIndexOutOfBoundsException) {
            ResponseEntity.status(403).build()
        } catch (e: Throwable) {
            ResponseEntity.status(400).build()
        }
}