package ru.cobalt42.auth.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.cobalt42.auth.exception.ExceptionMessage
import ru.cobalt42.auth.model.dictionary.Target
import ru.cobalt42.auth.repository.custom.MainRepository

@Component
class SystemMessages {

    @Autowired
    private lateinit var mainRepository: MainRepository

    fun getException(
        authToken: String, uname: String, target: Target = Target(),
        source: Target = Target(), description: String = "", section: String = ""
    ): ExceptionMessage {
        val exceptions = mainRepository.getMessages(authToken)
        return exceptions.first { it.uname == uname && it.code < 10000 }
            .also {
                it.description = description.ifBlank { "" }
                it.target.uid = target.uid.ifBlank { "" }
                it.target.uname = target.uname.ifBlank { "" }
                it.target.serviceUname = target.serviceUname.ifBlank { "" }
                it.target.label = target.label.ifBlank { "" }
                it.source.uid = source.uid.ifBlank { "" }
                it.source.uname = source.uname.ifBlank { "" }
                it.source.serviceUname = source.serviceUname.ifBlank { "" }
                it.source.label = source.label.ifBlank { "" }
                it.section = section.ifBlank { "" }
            }
    }

    fun getWarning(
        authToken: String, uname: String, target: Target = Target(),
        source: Target = Target(), description: String = "", section: String = ""
    ): ExceptionMessage {
        val exceptions = mainRepository.getMessages(authToken)
        return exceptions.first { it.uname == uname && it.code >= 10000 }
            .also {
                it.description = description.ifBlank { "" }
                it.target.uid = target.uid.ifBlank { "" }
                it.target.uname = target.uname.ifBlank { "" }
                it.target.serviceUname = target.serviceUname.ifBlank { "" }
                it.target.label = target.label.ifBlank { "" }
                it.source.uid = source.uid.ifBlank { "" }
                it.source.uname = source.uname.ifBlank { "" }
                it.source.serviceUname = source.serviceUname.ifBlank { "" }
                it.source.label = source.label.ifBlank { "" }
                it.section = section.ifBlank { "" }
            }
    }
}