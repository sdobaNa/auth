package ru.cobalt42.auth.service

import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.exception.ExceptionMessage
import ru.cobalt42.auth.exception.ValidateException
import ru.cobalt42.auth.model.auth.role.Role
import ru.cobalt42.auth.repository.auth.RoleRepository
import ru.cobalt42.auth.util.SystemMessages
import java.util.*

@Service
class RoleServiceImpl(
    private val repository: RoleRepository,
    private val systemMessages: SystemMessages
) : RoleService {

    override fun createOne(role: Role, authToken: String): DefaultResponse {
        val messages = validator(role, authToken)
        if (messages.isEmpty()) {
            role.uid = UUID.randomUUID().toString()
            repository.save(role)
        } else
            throw ValidateException(role, messages)
        return DefaultResponse(role, messages)
    }

    override fun getAll(paging: Pageable, search: String): PaginatedResponse {
        var total: Long
        return if (search.isBlank())
            PaginatedResponse(
                result =
                repository.findAll(paging).also { total = it.totalElements }
                    .toList(),
                total = total
            )
        else
            PaginatedResponse(
                result =
                repository.getByNameContainingIgnoreCase(search, paging)
                    .also { total = it.totalElements }.toList(),
                total = total
            )
    }

    override fun getOne(uid: String): DefaultResponse = DefaultResponse(repository.getByUid(uid))

    override fun updateOne(uid: String, role: Role, authToken: String): DefaultResponse {
        role.uid = uid
        val messages = validator(role, authToken)
        if (messages.any { (it.code in 1..9999) })
            throw ValidateException(role, messages)
        val old = try {
            repository.getByUid(uid)
        } catch (e: DataAccessException) {
            messages.add(
                systemMessages.getWarning(
                    authToken = authToken,
                    uname = "updatedDocumentNotFound"
                )
            )
            Role()
        }
        role._id = old._id
        repository.save(role)
        return DefaultResponse(role, messages)
    }

    override fun deleteOne(uid: String) = repository.deleteByUid(uid)

    private fun validator(role: Role, authToken: String): MutableList<ExceptionMessage> {
        val message = mutableListOf<ExceptionMessage>()
        if (role.name.isBlank())
            message.add(
                systemMessages.getException(
                    authToken = authToken,
                    uname = "requiredFieldsEmpty",
                    description = "Наименование"
                )
            )
        return message
    }
}