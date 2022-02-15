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
import ru.cobalt42.auth.repository.auth.UserRepository
import ru.cobalt42.auth.util.SystemMessages
import java.util.*

@Service
class RoleServiceImpl(
    private val repository: RoleRepository,
    private val systemMessages: SystemMessages,
    private val userRepository: UserRepository,
) : RoleService {

    override fun createOne(role: Role, authToken: String): DefaultResponse<Role> {
        val messages = validator(role, authToken)
        if (messages.isEmpty()) {
            role.uid = UUID.randomUUID().toString()
            repository.save(role)
        } else
            throw ValidateException(role, messages)
        return DefaultResponse(role, messages)
    }

    override fun getAll(paging: Pageable, search: String): PaginatedResponse<Role> {
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

    override fun getOne(uid: String): DefaultResponse<Role> = DefaultResponse(repository.getByUid(uid))

    override fun updateOne(uid: String, role: Role, authToken: String): DefaultResponse<Role> {
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

    override fun deleteOne(uid: String) {
        userRepository.getAllByRole(uid).forEach {
            userRepository.save(it.also { user -> user.roles = user.roles.filter { role -> role != uid } })
        }
        repository.deleteByUid(uid)
    }

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