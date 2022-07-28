package ru.cobalt42.auth.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class GlobalExceptionHandlerController {
    @ExceptionHandler(RequestException::class)
    fun handleCustomException(res: HttpServletResponse, ex: RequestException) =
        ResponseEntity(ex.message(), ex.getHttpStatus())

    @ExceptionHandler(ValidateException::class)
    fun handleValidateException(res: HttpServletResponse, ex: ValidateException) =
        ResponseEntity(ex, BAD_REQUEST)

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(res: HttpServletResponse, ex: BadRequestException) =
        ResponseEntity(ex, BAD_REQUEST)

    @ExceptionHandler(PayloadTooLargeException::class)
    fun handlePayloadTooLargeException(res: HttpServletResponse, ex: PayloadTooLargeException) =
        ResponseEntity(ex, PAYLOAD_TOO_LARGE)
}