package ru.pmlite.api.security.repositories

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.security.domain.UserState
import ru.pmlite.api.security.dto.RegistrationCommand
import ru.pmlite.api.security.dto.UserAuthenticatedDetails
import ru.pmlite.api.values.UserId

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
            from users where email = :email
        """.trimIndent(), MapSqlParameterSource("email", email), mapUserDetails)
        }.getOrNull()

    }

    fun existsByEmail(email: String): Boolean {
        return jdbcTemplate.queryForObject("""
            select  count(id) > 0 from users where email = :email
        """.trimIndent(), MapSqlParameterSource("email", email), Boolean::class.java) ?: false

    }

    fun createUser(command: RegistrationCommand): UserId {
        val keyHolder = GeneratedKeyHolder()
        return jdbcTemplate.update("""
            insert into users (name, email) VALUES (:name, :email) returning id
        """.trimIndent(),
            MapSqlParameterSource("name", command.name)
                .addValue("email", command.email),
            keyHolder
        )
            .let { keyHolder.keys?.get("id") as Long }
            .let(::UserId)
    }

    fun confirmUserByEmail(userId: UserId) {
        jdbcTemplate.update("""
            update users set state = :state::user_state 
            where id = :id and state = 'PENDING'
        """.trimIndent(),
            MapSqlParameterSource("id", userId.id)
                .addValue("state", UserState.CONFIRMED.name)
            )
    }

    private val mapUserDetails = RowMapper<UserAuthenticatedDetails> { rs, _ ->
        UserAuthenticatedDetails(
            rs.getString("email"),
            rs.getString("password"),
            rs.getLong("id")
        )
    }
}
