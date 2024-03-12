package ru.pmlite.api.security.providers


import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Component
import ru.pmlite.api.security.config.JwtProperties
import ru.pmlite.api.security.filters.AUTH_COOKIE_NAME
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository
import ru.pmlite.api.values.UserId
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

const val AUTHORITIES_KEY = "roles"

private val logger = KotlinLogging.logger {}
@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
    private val userAuthenticatedRepository: UserAuthenticatedRepository
) {


    private val secretKey: SecretKey = Base64.getEncoder().encodeToString(jwtProperties.secretKey.toByteArray())
        .let{ Keys.hmacShaKeyFor(it.toByteArray()) }
    private val jwtParser = Jwts.parser().verifyWith(secretKey).build()



    fun createTokenByAuthentication(authentication: Authentication): String {
        val authorities: Collection<GrantedAuthority> = authentication.authorities
        val claims: Claims =  Jwts.claims().subject(authentication.name)
            .add(AUTHORITIES_KEY, authorities.joinToString(", ") { it.authority.toString() })
            .build()
        logger.info { "Authorities: ${authentication.name}, ${authorities}" }
        val now = Date()
        val validity = Date(now.time + jwtProperties.validityInMs.toMillis())
        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey, Jwts.SIG.HS512)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = jwtParser.parseSignedClaims(token).payload
        val authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims[AUTHORITIES_KEY].toString())
        val principal = UserId(claims.subject.toLong())
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            val jws = jwtParser.parseSignedClaims(token)
            val claims: Claims = jws.payload
            val updatedUserAt = UserId(claims.subject.toLong())
                .let(userAuthenticatedRepository::getUserDetailsById).updatedAt.truncatedTo(ChronoUnit.SECONDS)
            if (claims.issuedAt.toInstant() < updatedUserAt)
                throw ExpiredJwtException(jws.header, claims, "Данные пользователя были изменены!")
            return !claims.expiration.before(Date())
        } catch (e: JwtException) {
            logger.info("Invalid JWT token. ${e.localizedMessage}, ${e.message}")
        } catch (e: IllegalArgumentException) {
            logger.info("Invalid JWT token. ${e.localizedMessage}")
        }
        return false
    }

    fun getAuthenticationCookieByToken(token: String) = Cookie(AUTH_COOKIE_NAME, token).apply {
        isHttpOnly = true
        maxAge = jwtProperties.validityInMs.toSeconds().toInt()
        path = "/"
    }
}
