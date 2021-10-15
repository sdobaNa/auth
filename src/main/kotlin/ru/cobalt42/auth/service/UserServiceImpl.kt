package ru.cobalt42.auth.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.cobalt42.auth.model.User
import ru.cobalt42.auth.repository.auth.UserRepository
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun createOne(user: User, authToken: String): User {
        user.uid = UUID.randomUUID().toString()
        return userRepository.save(user)
    }

    override fun getAll(paging: Pageable, search: String) {
        TODO("Not yet implemented")
    }

    override fun getOne(uid: String): User {
        TODO("Not yet implemented")
    }

    override fun updateOne(uid: String, user: User, authToken: String): User {
        TODO("Not yet implemented")
    }

    override fun deleteOne(uid: String) {
        TODO("Not yet implemented")
    }
}