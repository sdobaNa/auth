package ru.cobalt42.auth.controller

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.model.auth.role.Role
import ru.cobalt42.auth.service.RoleService

@CrossOrigin
@RestController
@RequestMapping("api/auth/role")
class RoleController(
    private val service: RoleService
) {
    @PostMapping
    fun createOne(
        @RequestBody role: Role,
        @RequestHeader("Authorization") authToken: String
    ) = try {
        ResponseEntity.ok(service.createOne(role, authToken))
    } catch (e: EmptyResultDataAccessException) {
        ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "1", required = false) page: Int,
        @RequestParam(defaultValue = "50", required = false) size: Int,
        @RequestParam(defaultValue = "", required = false) search: String,
    ): ResponseEntity<PaginatedResponse<Role>> {
        val paging = PageRequest.of(page - 1, size)
        return ResponseEntity.ok(service.getAll(paging, search))
    }

    @GetMapping("/{uid}")
    fun getOne(@PathVariable uid: String) = try {
        ResponseEntity.ok(service.getOne(uid))
    } catch (e: EmptyResultDataAccessException) {
        ResponseEntity.status(HttpStatus.GONE).build()
    }

    @PostMapping("/{uid}")
    fun updateOne(
        @PathVariable("uid") uid: String,
        @RequestBody role: Role,
        @RequestHeader("Authorization") authToken: String
    ) = try {
        ResponseEntity.ok(service.updateOne(uid, role, authToken))
    } catch (e: EmptyResultDataAccessException) {
        ResponseEntity.status(HttpStatus.GONE).build()
    }

    @DeleteMapping("/{uid}")
    fun deleteOne(@PathVariable uid: String): ResponseEntity<Unit> {
        service.deleteOne(uid)
        return ResponseEntity.noContent().build()
    }
}