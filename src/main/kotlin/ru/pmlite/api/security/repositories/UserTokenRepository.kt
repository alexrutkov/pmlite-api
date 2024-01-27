package ru.pmlite.api.security.repositories

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.email.dto.CreateUserTokenCommand
import ru.pmlite.api.security.domain.UserToken
import ru.pmlite.api.security.domain.UserTokenState
import ru.pmlite.api.security.domain.UserTokenType
import ru.pmlite.api.values.TokenId
import ru.pmlite.api.values.UserId
import java.util.*

@Repository
class UserTokenRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun createTokenBy(command: CreateUserTokenCommand): UUID {
        val keyHolder = GeneratedKeyHolder()
        return jdbcTemplate.update("""
            insert into user_tokens (user_id, type, expired_at)
            values (:userId, :type::user_token_type, :expiredAt)
            returning id
        """.trimIndent(),
            MapSqlParameterSource("type", command.type.name)
                .addValue("userId", command.userId.id)
                .addValue("expiredAt", command.expiredAt),
            keyHolder
        ).let { keyHolder.keys?.get("id") as UUID }
    }

    fun findTokenBy(token: UUID): UserToken? {
        return runCatching {
            jdbcTemplate.queryForObject("""
                select * from user_tokens where id = :token
            """.trimIndent(), MapSqlParameterSource("token", token)) { rs, _ ->
                UserToken(
                    rs.getTimestamp("expired_at").toInstant(),
                    rs.getString("state").let(UserTokenState::valueOf),
                    rs.getString("type").let(UserTokenType::valueOf),
                    rs.getString("id").let(UUID::fromString).let(::TokenId),
                    rs.getLong("user_id").let(::UserId)
                )
            }
        }.getOrNull()
    }

    fun updateState(tokenId: TokenId, state: UserTokenState) {
        jdbcTemplate.update("""
            update user_tokens set state = :state::user_token_state where id = :id
        """.trimIndent(),
            MapSqlParameterSource("id", tokenId.id)
                .addValue("state", state.name)
        )
    }
}
