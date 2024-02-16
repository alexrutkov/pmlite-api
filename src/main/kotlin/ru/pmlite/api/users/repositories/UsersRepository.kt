package ru.pmlite.api.users.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.users.domain.UserShortDetails
import ru.pmlite.api.users.domain.UserTask
import ru.pmlite.api.users.exceptions.UserNotFoundException
import ru.pmlite.api.values.TaskId

@Repository
class UsersRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun getAllUsers(pageable: Pageable): List<UserShortDetails> {
        return jdbcTemplate.query("""
            select * from users u
            where u.state != 'BLOCKED' 
            order by u.created_at desc offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("limit", pageable.pageSize)
                .addValue("offset", pageable.offset),
            mapTaskSummary
        )
    }

    fun getMyUsers(pageable: Pageable): List<UserShortDetails> {
        return jdbcTemplate.query("""
            select * from users u
            where u.state != 'BLOCKED' 
            order by u.created_at desc offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("limit", pageable.pageSize)
                .addValue("offset", pageable.offset),
            mapTaskSummary
        )
    }

    fun getUserDetails(id: Long): UserShortDetails {
        return runCatching {
            jdbcTemplate.queryForObject("""
            select * from users u where u.id = :id
        """.trimIndent(), MapSqlParameterSource("id", id), mapTaskSummary)
        }.getOrNull() ?: throw UserNotFoundException()
    }

    fun getUserTasks(id: Long, pageable: Pageable): List<UserTask> {
        return jdbcTemplate.query("""
            select 
                 t.id,
                 tu.user_id,
                 t.name,
                 tu.created_at,
                 tu.role
            from task_users tu
                join tasks t on tu.task_id = t.id
                join agreements a on tu.agreement_id = a.id
            where tu.user_id = :id and a.state = 'APPROVED'
            order by tu.created_at desc offset :offset limit :limit
        """.trimIndent(), MapSqlParameterSource("id", id)
            .addValue("limit", pageable.pageSize)
            .addValue("offset", pageable.offset)
        ) { rs, _ ->
            UserTask(
                TaskId(rs.getLong("id")),
                rs.getLong("user_id"),
                rs.getString("name"),
                UserTaskRole.valueOf(rs.getString("role")),
                rs.getTimestamp("created_at").toInstant()
            )
        }
    }

    private val mapTaskSummary = RowMapper<UserShortDetails> { rs, _ ->
        UserShortDetails(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getTimestamp("created_at").toInstant()
        )
    }
}
