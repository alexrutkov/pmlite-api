package ru.pmlite.api.tasks.repositories

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.tasks.dto.CreateTaskCommand
import ru.pmlite.api.tasks.dto.TaskUserRoleDto
import ru.pmlite.api.values.TaskId

@Repository
class TaskRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun createTask(command: CreateTaskCommand): TaskId {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update("""
            INSERT INTO tasks (name, short_description, parent_id)
            VALUES (:name, :shortDescription, :parentId) returning id
        """.trimIndent(),
            MapSqlParameterSource("name", command.name)
                .addValue("parentId", command.parentId)
                .addValue("shortDescription", command.shortDescription),
            keyHolder
        )
        return (keyHolder.keys?.get("id") as Long).let(::TaskId)
    }

    fun addUserRole(taskUserRole: TaskUserRoleDto) {
        jdbcTemplate.update("""
            INSERT INTO task_users (task_id, user_id, role)
            VALUES (:taskId, :userId, :role)
        """.trimIndent(),
            MapSqlParameterSource("taskId", taskUserRole.taskId.id)
                .addValue("userId", taskUserRole.userId.id)
                .addValue("role", taskUserRole.role.name)
        )
    }
}
