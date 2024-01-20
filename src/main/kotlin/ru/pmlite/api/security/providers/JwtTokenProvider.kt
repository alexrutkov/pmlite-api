package ru.pmlite.api.security.providers


import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import ru.pmlite.api.security.config.JwtProperties
import java.util.*
import javax.crypto.SecretKey

const val AUTHORITIES_KEY = "roles"
const val PRINCIPAL_KEY = "principal"

private val logger = KotlinLogging.logger {}
@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {


    private val secretKey: SecretKey = Base64.getEncoder().encodeToString(jwtProperties.secretKey.toByteArray())
        .let{ Keys.hmacShaKeyFor(it.toByteArray()) }


    fun createToken(authentication: Authentication): String {
        val username: String = authentication.name
        val authorities: Collection<GrantedAuthority> = authentication.authorities
        val claims: Claims =  Jwts.claims().subject(username).build()

        claims[AUTHORITIES_KEY] = authorities.joinToString(", ") { it.authority.toString() }
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
        val claims: Claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
        val authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims[AUTHORITIES_KEY].toString())
        val principal = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            val claims: Claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
            return !claims.expiration.before(Date())
        } catch (e: JwtException) {
            logger.info("Invalid JWT token. ${e.localizedMessage}, ${e.message}")
        } catch (e: IllegalArgumentException) {
            logger.info("Invalid JWT token. IllegalArgumentException")
        }
        return false
    }
}
