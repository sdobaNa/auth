package ru.cobalt42.auth.controller

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.model.auth.group.Group
import ru.cobalt42.auth.service.GroupService

@CrossOrigin
@RestController
@RequestMapping("api/auth/group")
class GroupController(
    private val service: GroupService,
) {
    @PostMapping
    fun createOne(
        @RequestBody group: Group,
        @RequestHeader("Authorization") authToken: String,
    ) = try {
        ResponseEntity.ok(service.createOne(group, authToken))
    } catch (e: EmptyResultDataAccessException) {
        ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "1", required = false) page: Int,
        @RequestParam(defaultValue = "50", required = false) size: Int,
        @RequestParam(defaultValue = "", required = false) search: String,
    ): ResponseEntity<PaginatedResponse<Group>> {
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
        @RequestBody group: Group,
        @RequestHeader("Authorization") authToken: String,
    ) = try {
        ResponseEntity.ok(service.updateOne(uid, group, authToken))
    } catch (e: EmptyResultDataAccessException) {
        ResponseEntity.status(HttpStatus.GONE).build()
    }

    @DeleteMapping("/{uid}")
    fun deleteOne(@PathVariable uid: String): ResponseEntity<Unit> {
        service.deleteOne(uid)
        return ResponseEntity.noContent().build()
    }
}