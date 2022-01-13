package ru.cobalt42.auth.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class GlobalExceptionHandlerController {
    @ExceptionHandler(RequestException::class)
    fun handleCustomException(res: HttpServletResponse, ex: RequestException) {
        res.sendError(ex.getHttpStatus().value(), ex.message)
    }

    @ExceptionHandler(ValidateException::class)
    fun handleValidateException(res: HttpServletResponse, ex: ValidateException) =
        ResponseEntity(ex, HttpStatus.BAD_REQUEST)
}