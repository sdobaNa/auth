package ru.cobalt42.auth.service

import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.exception.ExceptionMessage
import ru.cobalt42.auth.exception.ValidateException
import ru.cobalt42.auth.model.user.User
import ru.cobalt42.auth.repository.auth.UserRepository
import ru.cobalt42.auth.util.SystemMessages
import java.util.*

@Service
class UserServiceImpl(
    private val repository: UserRepository,
    private val systemMessages: SystemMessages
) : UserService {

    override fun createOne(user: User, authToken: String): User {
        val messages = validator(user, authToken)
        if (messages.isEmpty()) {
            user.uid = UUID.randomUUID().toString()
            user.password = BCryptPasswordEncoder().encode(user.password)
            repository.save(user)
        } else
            throw ValidateException(messages, user)
        return user
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
                repository.findByNameContainingIgnoreCase(search, paging)
                    .also { total = it.totalElements }.toList(),
                total = total
            )
    }

    override fun getOne(uid: String): User = repository.findByUid(uid)

    override fun updateOne(uid: String, user: User, authToken: String): User {
        user.uid = uid
        val messages = validator(user, authToken)
        if (messages.any { (it.code in 1..9999) })
            throw ValidateException(messages, user)
        val old = try {
            repository.findByUid(uid)
        } catch (e: DataAccessException) {
            messages.add(
                systemMessages.getWarning(
                    authToken = authToken,
                    uname = "updatedDocumentNotFound"
                )
            )
            User()
        }
        user._id = old._id
        user.password = BCryptPasswordEncoder().encode(user.password)
        repository.save(user)
        return user
    }

    override fun deleteOne(uid: String) = repository.deleteByUid(uid)

    private fun validator(user: User, authToken: String): MutableList<ExceptionMessage> {
        val message = mutableListOf<ExceptionMessage>()
        if (user.login.isBlank())
            message.add(
                systemMessages.getException(
                    authToken = authToken,
                    description = "проекта",
                    uname = "requiredFieldsEmpty"
                )
            )
        return message
    }
}