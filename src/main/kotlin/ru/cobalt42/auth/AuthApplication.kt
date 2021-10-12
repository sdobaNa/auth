package ru.cobalt42.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@ComponentScan
@SpringBootApplication
class AuthApplication

fun main(args: Array<String>) {
	runApplication<AuthApplication>(*args)
}
