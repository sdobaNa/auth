package ru.cobalt42.auth.service

import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.exception.ExceptionMessage
import ru.cobalt42.auth.exception.ValidateException
import ru.cobalt42.auth.model.auth.group.Group
import ru.cobalt42.auth.repository.auth.GroupRepository
import ru.cobalt42.auth.repository.auth.UserRepository
import ru.cobalt42.auth.util.SystemMessages
import java.util.*

@Service
class GroupServiceImpl(
    private val repository: GroupRepository,
    private val systemMessages: SystemMessages,
    private val userRepository: UserRepository,
) : GroupService {
    override fun createOne(group: Group, authToken: String): DefaultResponse<Group> {
        val messages = validator(group, authToken)
        if (messages.isEmpty()) {
            group.uid = UUID.randomUUID().toString()
            repository.save(group)
        } else
            throw ValidateException(group, messages)
        return DefaultResponse(group, messages)
    }

    override fun getAll(paging: Pageable, search: String): PaginatedResponse<Group> {
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

    override fun getOne(uid: String): DefaultResponse<Group> = DefaultResponse(repository.getByUid(uid))

    override fun updateOne(uid: String, group: Group, authToken: String): DefaultResponse<Group> {
        group.uid = uid
        val messages = validator(group, authToken)
        if (messages.any { (it.code in 1..9999) })
            throw ValidateException(group, messages)
        val old = try {
            repository.getByUid(uid)
        } catch (e: DataAccessException) {
            messages.add(
                systemMessages.getWarning(
                    authToken = authToken,
                    uname = "updatedDocumentNotFound"
                )
            )
            Group()
        }
        group._id = old._id
        repository.save(group)
        return DefaultResponse(group, messages)
    }

    override fun deleteOne(uid: String) {
        userRepository.getAllByGroupUid(uid).forEach {
            userRepository.save(it.also { user -> user.groupUid = "'" })
        }
        repository.deleteByUid(uid)
    }

    private fun validator(group: Group, authToken: String): MutableList<ExceptionMessage> {
        val message = mutableListOf<ExceptionMessage>()
        if (group.name.isBlank())
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