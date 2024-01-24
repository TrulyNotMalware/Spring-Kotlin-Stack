package dev.notypie.configurations

import dev.notypie.jwt.dto.LoginRequestDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

@Profile("jwt")
class AuthenticationFilter(
    private val authenticationManager: AuthenticationManager
): UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {

        val builder: StringBuilder = StringBuilder()
        val inputStream: InputStream = request.inputStream

        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        while( true ){
            val line: String = bufferedReader.readLine() ?: break
            builder.append(line)
        }
        //FIXME LATER
        val list = builder.toString().split("&")
        val userIdList = list[0].split("=")
        val passwordList = list[1].split("=")

        return this.authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(userIdList[1], passwordList[1])
        )
    }
}