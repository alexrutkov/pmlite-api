package ru.pmlite.api.security.repositories

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pmlite.api.security.dto.UserAuthenticatedDetails

@Repository
class UserAuthenticatedRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun findByEmail(email: String): UserAuthenticatedDetails? {
        return runCatching {
            jdbcTemplate.queryForObject("""
            select 
                email,
                password,
                id
            from pmlite.public.users where email = :email
        """.trimIndent(), MapSqlParameterSource("email", email), mapUserDetails)
        }.getOrNull()

    }

    private val mapUserDetails = RowMapper<UserAuthenticatedDetails> { rs, _ ->
        UserAuthenticatedDetails(
            rs.getString("email"),
            rs.getString("password"),
            rs.getLong("id")
        )
    }
}
