package ru.pmlite.api.security.filters

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import ru.pmlite.api.security.providers.JwtTokenProvider

const val AUTH_COOKIE_NAME = "auth"
@Service
class JWTAuthorizationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = resolveToken(request)
        val cookie = Cookie(AUTH_COOKIE_NAME, "")
        cookie.path = "/"
        cookie.maxAge = 0
        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token))
                    SecurityContextHolder.getContext().authentication = jwtTokenProvider.getAuthentication(token)
                else {
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.addCookie(cookie)
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Доступ был изменён!")
                    return
                }
            } catch (e: ExpiredJwtException) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.addCookie(cookie)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
                return
            } catch (e: UnsupportedJwtException) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.addCookie(cookie)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
                return
            } catch (e: MalformedJwtException) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.addCookie(cookie)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
                return
            }
        }
        chain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.cookies?.find { it.name.equals(AUTH_COOKIE_NAME) }?.value
        return if (!bearerToken.isNullOrBlank()) bearerToken
        else null
    }
}
