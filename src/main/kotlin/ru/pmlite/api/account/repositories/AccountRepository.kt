package ru.pmlite.api.account.repositories

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pmlite.api.account.dto.AccountDetails
import ru.pmlite.api.account.dto.AccountTaskRole
import ru.pmlite.api.account.dto.ProfileDetails
import ru.pmlite.api.account.dto.SaveProfileCommand
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.users.exceptions.UserNotFoundException
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
            } ?: throw UserNotFoundException()
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
            select 
                task_id, role 
            from task_users t join agreements a on t.agreement_id = a.id
            where t.user_id = :id and a.state != 'CANCELLED'
        """.trimIndent(), MapSqlParameterSource("id", userId.id)) { rs, _ ->
            AccountTaskRole(
                TaskId(rs.getLong("task_id")),
                UserTaskRole.valueOf(rs.getString("role"))
            )
        }
    }

    fun getProfileDetails(userId: UserId): ProfileDetails {
        return runCatching {
            jdbcTemplate.queryForObject("""
                select 
                    name, description
                from users where id = :id
            """.trimIndent(), MapSqlParameterSource("id", userId.id)) {rs, _ ->
                ProfileDetails(
                    rs.getString("name"),
                    rs.getString("description"),
                    emptyList()
                )
            } ?: throw UserNotFoundException()
        }.getOrThrow()
    }

    fun saveProfile(userId: UserId, command: SaveProfileCommand) {
        jdbcTemplate.update("""
            update users set name = :name, description = :description
            where id = :userId
        """.trimIndent(),
            MapSqlParameterSource("userId", userId.id)
                .addValue("name", command.name)
                .addValue("description", command.description)
            )
    }
}
