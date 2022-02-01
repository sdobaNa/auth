package ru.cobalt42.auth.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.exception.ExceptionMessage
import ru.cobalt42.auth.exception.RequestException
import ru.cobalt42.auth.exception.ValidateException
import ru.cobalt42.auth.model.Refresh
import ru.cobalt42.auth.model.user.User
import ru.cobalt42.auth.repository.auth.RefreshRepository
import ru.cobalt42.auth.repository.auth.UserRepository
import ru.cobalt42.auth.util.SystemMessages
import ru.cobalt42.auth.util.UserSearcher
import java.text.SimpleDateFormat
import java.util.*

@Service
class UserServiceImpl(
    private val repository: UserRepository,
    private val refreshRepository: RefreshRepository,
    private val systemMessages: SystemMessages,
    private val userSearcher: UserSearcher
) : UserService {

    @Value("\${token.refresh.time}")
    private lateinit var refreshTime: String

    override fun createOne(user: User, authToken: String): DefaultResponse {
        val messages = validator(user, authToken)
        if (user.password.isBlank())
            messages.add(
                systemMessages.getWarning(
                    authToken = authToken,
                    uname = "requiredFieldsEmpty",
                    description = "Пароль"
                )
            )
        if (user.login.isNotBlank())
            try {
                repository.findByLogin(user.login)
                messages.add(
                    systemMessages.getException(
                        authToken = authToken,
                        uname = "loginIsUse"
                    )
                )
                throw ValidateException(user, messages)
            } catch (e: EmptyResultDataAccessException) {
                user.uid = UUID.randomUUID().toString()
                user.password = BCryptPasswordEncoder().encode(user.password)
                repository.save(user)
                refreshRepository.save(
                    Refresh(
                        refresh = UUID.randomUUID().toString(),
                        exp = try {
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date(System.currentTimeMillis() + refreshTime.toInt()))
                        } catch (e: Throwable) {
                            throw RequestException("Expiration date exception", HttpStatus.BAD_REQUEST)
                        },
                        user = user.uid
                    )
                )
            }
        return DefaultResponse(user, messages)
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
                repository.findByLoginContainingIgnoreCase(search, paging)
                    .also { total = it.totalElements }.toList(),
                total = total
            )
    }

    override fun getOne(uid: String, authToken: String): DefaultResponse {
        if (userSearcher.isAdmin(authToken) || userSearcher.isOriginalUser(authToken, uid))
            return DefaultResponse(repository.findByUid(uid).also { it.password = "" })
        else
            throw RequestException("Attempt to bypass access", HttpStatus.FORBIDDEN)
    }

    override fun updateOne(uid: String, user: User, authToken: String): DefaultResponse {
        if (userSearcher.isAdmin(authToken) || userSearcher.isOriginalUser(authToken, uid)) {
            user.uid = uid
            val messages = validator(user, authToken)
            if (messages.any { (it.code in 1..9999) })
                throw ValidateException(user, messages)
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
            if (user.password.isNotBlank())
                user.password = BCryptPasswordEncoder().encode(user.password)
            else user.password = old.password
            repository.save(user)
            return DefaultResponse(user, messages)
        } else throw RequestException("Attempt to bypass access", HttpStatus.FORBIDDEN)
    }

    override fun deleteOne(uid: String) = repository.deleteByUid(uid)

    private fun validator(user: User, authToken: String): MutableList<ExceptionMessage> {
        val messages = mutableListOf<ExceptionMessage>()
        if (user.login.isBlank())
            messages.add(
                systemMessages.getWarning(
                    authToken = authToken,
                    uname = "requiredFieldsEmpty",
                    description = "Логин"
                )
            )
//        if (user.firstName.isBlank())
//            messages.add(
//                systemMessages.getWarning(
//                    authToken = authToken,
//                    uname = "requiredFieldsEmpty",
//                    description = "Имя"
//                )
//            )
//        if (user.secondName.isBlank())
//            messages.add(
//                systemMessages.getWarning(
//                    authToken = authToken,
//                    uname = "requiredFieldsEmpty",
//                    description = "Отчество"
//                )
//            )
//        if (user.lastName.isBlank())
//            messages.add(
//                systemMessages.getWarning(
//                    authToken = authToken,
//                    uname = "requiredFieldsEmpty",
//                    description = "Фамилия"
//                )
//            )
//        if (user.name.isBlank())
//            messages.add(
//                systemMessages.getWarning(
//                    authToken = authToken,
//                    uname = "requiredFieldsEmpty",
//                    description = "Фамилия и инициалы"
//                )
//            )
//        if (user.organization.isBlank())
//            messages.add(
//                systemMessages.getWarning(
//                    authToken = authToken,
//                    uname = "requiredFieldsEmpty",
//                    description = "Наименование организации"
//                )
//            )
//        if (user.position.isBlank())
//            messages.add(
//                systemMessages.getWarning(
//                    authToken = authToken,
//                    uname = "requiredFieldsEmpty",
//                    description = "Наименование должности"
//                )
//            )
//        if (user.roles.isEmpty())
//            messages.add(
//                systemMessages.getWarning(
//                    authToken = authToken,
//                    uname = "requiredFieldsEmpty",
//                    description = "Роли"
//                )
//            )
        return messages
    }
}