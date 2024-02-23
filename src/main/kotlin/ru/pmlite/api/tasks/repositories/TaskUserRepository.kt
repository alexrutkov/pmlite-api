package ru.pmlite.api.tasks.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pmlite.api.tasks.domain.TaskUser
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@Repository
class TaskUserRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    fun getUserRelation(taskId: TaskId, pageable: Pageable): List<TaskUser> {
        return jdbcTemplate.query("""
            select 
                tu.user_id, u.name, tu.created_at,
                tu.role, tu.task_id,
                tu.agreement_id 
            from task_users tu 
            join agreements a on tu.agreement_id = a.id
            join users u on tu.user_id = u.id
            where task_id = :id and a.state = 'APPROVED'
            offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("id", taskId.id)
                .addValue("offset", pageable.offset)
                .addValue("limit", pageable.pageSize),
            mapTaskSummary
        )
    }
    fun getTaskUser(taskId: TaskId, userId: UserId): TaskUser {
        return jdbcTemplate.queryForObject("""
            select 
                tu.user_id, u.name, tu.created_at,
                tu.role, tu.task_id,
                tu.agreement_id
            from task_users tu 
            join agreements a on tu.agreement_id = a.id
            join users u on tu.user_id = u.id
            where tu.task_id = :taskId and tu.user_id = :userId
        """.trimIndent(),
            MapSqlParameterSource("taskId", taskId.id)
                .addValue("userId", userId.id),
            mapTaskSummary
        )!!
    }

    private val mapTaskSummary = RowMapper<TaskUser> { rs, _ ->
        TaskUser(
            AgreementId(rs.getLong("agreement_id")),
            TaskId(rs.getLong("task_id")),
            UserSummary(rs.getLong("user_id"), rs.getString("name")),
            UserTaskRole.valueOf(rs.getString("role")),
            rs.getTimestamp("created_at").toInstant()
        )
    }
}
