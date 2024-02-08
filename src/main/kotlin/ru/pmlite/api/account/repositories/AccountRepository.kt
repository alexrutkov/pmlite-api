package ru.pmlite.api.account.repositories

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pmlite.api.account.dto.AccountDetails
import ru.pmlite.api.account.dto.AccountTaskRole
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@Repository
class AccountRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun getAccountDetails(userId: UserId): AccountDetails {
        return runCatching {
            jdbcTemplate.queryForObject("""
                select 
                    name
                from users where id = :id
            """.trimIndent(), MapSqlParameterSource("id", userId.id)) {rs, _ ->
                AccountDetails(
                    userId.id, rs.getString("name")
                )
            }!!
        }.getOrThrow()
    }

    fun getAccountRoles(userId: UserId): List<UserRole> {
        return jdbcTemplate.query("""
            select role from user_roles where user_id = :id
        """.trimIndent(), MapSqlParameterSource("id", userId.id)) {rs, _ ->
            rs.getString("role").let(UserRole::valueOf)
        }
    }

    fun getAccountTaskRoles(userId: UserId): List<AccountTaskRole> {
        return jdbcTemplate.query("""
            select task_id, role from task_users where user_id = :id
        """.trimIndent(), MapSqlParameterSource("id", userId.id)) { rs, _ ->
            AccountTaskRole(
                TaskId(rs.getLong("task_id")),
                UserTaskRole.valueOf(rs.getString("role"))
            )
        }
    }
}
