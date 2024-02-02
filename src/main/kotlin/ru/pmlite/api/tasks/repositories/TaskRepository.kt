package ru.pmlite.api.tasks.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.tasks.domain.TaskDetails
import ru.pmlite.api.tasks.domain.TaskUser
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.tasks.dto.CreateTaskCommand
import ru.pmlite.api.tasks.dto.TaskSummary
import ru.pmlite.api.tasks.dto.TaskUserRoleDto
import ru.pmlite.api.tasks.exceptions.TaskNotFoundExceptions
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@Repository
class TaskRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun createTask(command: CreateTaskCommand, agreementId: AgreementId): TaskId {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update("""
            INSERT INTO tasks (name, short_description,  agreement_id, path)
            VALUES (
                :name, :shortDescription, :agreementId,
                (
                    CASE
                        WHEN cast(:parentId as text) is not null 
                        THEN (select text2ltree(concat_ws('.', t.path, cast(:parentId as text))) from tasks t where t.id = :parentId)
                        ELSE :parentId
                    END
                )
            ) returning id
        """.trimIndent(),
            MapSqlParameterSource("name", command.name)
                .addValue("parentId", command.parentId)
                .addValue("agreementId", agreementId.id)
                .addValue("shortDescription", command.shortDescription),
            keyHolder
        )
        return (keyHolder.keys?.get("id") as Long).let(::TaskId)
    }

    fun addUserRole(role: TaskUserRoleDto) {
        jdbcTemplate.update("""
            INSERT INTO task_users (task_id, user_id, role, agreement_id)
            VALUES (:taskId, :userId, :role::task_user_role, :agreementId)
        """.trimIndent(),
            MapSqlParameterSource("taskId", role.taskId.id)
                .addValue("userId", role.userId.id)
                .addValue("role", role.role.name)
                .addValue("agreementId", role.agreementId.id)
        )
    }

    fun getAllTasks(pageable: Pageable): List<TaskSummary> {
        return jdbcTemplate.query("""
            select * from tasks t
             join agreements a on t.agreement_id = a.id
             where a.state = 'APPROVED'
            order by t.created_at desc offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("limit", pageable.pageSize)
                .addValue("offset", pageable.offset),
            mapTaskSummary
        )
    }

    fun getMyTasks(userId: UserId, pageable: Pageable): List<TaskSummary> {
        return jdbcTemplate.query("""
            with cte as (
                select tt.task_id from team_users t
                    join task_teams tt on tt.team_id = t.team_id
                where t.user_id = :userId
                union distinct 
                select t.task_id from task_users t where t.user_id = :userId
            )
            select * from tasks t join cte on cte.task_id = t.id
            order by created_at desc offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("limit", pageable.pageSize)
                .addValue("offset", pageable.offset)
                .addValue("userId", userId.id)
            ,
            mapTaskSummary
        )
    }

    fun getTask(id: TaskId): TaskDetails {
        val taskSummary = getTaskSummary(id)
        val userRelations = getUserRelation(id)
        return TaskDetails(
            taskSummary, userRelations
        )
    }

    private fun getUserRelation(taskId: TaskId): List<TaskUser> {
        return jdbcTemplate.query("""
            select u.id as userId, u.name, tu.created_at, tu.role from task_users tu 
            join agreements a on tu.agreement_id = a.id
            join users u on tu.user_id = u.id
            where task_id = :id and a.state = 'APPROVED'
        """.trimIndent(), mapOf("id" to taskId.id)) { rs, _ ->
            TaskUser(
                UserSummary(rs.getLong("userId"), rs.getString("name")),
                UserTaskRole.valueOf(rs.getString("role")),
                rs.getTimestamp("created_at").toInstant()
            )
        }
    }

    private fun getTaskSummary(taskId: TaskId): TaskSummary {
        return runCatching {
            jdbcTemplate.queryForObject("""
            select * from tasks where id = :id
        """.trimIndent(),
                MapSqlParameterSource("id", taskId.id),
                mapTaskSummary
            )
        }.getOrNull() ?: throw TaskNotFoundExceptions()
    }

    private val mapTaskSummary = RowMapper<TaskSummary> { rs, _ ->
        TaskSummary(
            rs.getLong("id").let(::TaskId),
            rs.getString("name"),
            rs.getString("short_description"),
            rs.getTimestamp("created_at").toInstant()
        )
    }
}
