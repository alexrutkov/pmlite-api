package ru.pmlite.api.users.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.users.domain.UserTask
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@Repository
class UserTaskRepository(
  private val jdbcClient: JdbcClient
) {

  fun getTaskRelation(userId: UserId, pageable: Pageable): List<UserTask> {
    return jdbcClient.sql(
      """
            select 
                 a.id as agreement_id,
                 tu.task_id,
                 tu.user_id,
                 t.name,
                 tu.created_at,
                 tu.role
            from task_users tu
                join tasks t on tu.task_id = t.id
                join agreements a on tu.agreement_id = a.id
            where tu.user_id = :userId and a.state = 'APPROVED'
            order by tu.created_at desc offset :offset limit :limit
        """.trimIndent()
    )
      .param("userId", userId.id)
      .param("offset", pageable.offset)
      .param("limit", pageable.pageSize)
      .query(mapUserTask)
      .list()
  }

  fun getUserTask(userId: UserId, taskId: TaskId): UserTask {
    return jdbcClient.sql(
      """
            select 
                 a.id as agreement_id,
                 tu.task_id,
                 tu.user_id,
                 t.name,
                 tu.created_at,
                 tu.role
            from task_users tu
                join tasks t on tu.task_id = t.id
                join agreements a on tu.agreement_id = a.id
            where tu.user_id = :userId and tu.task_id = :taskId
        """.trimIndent()
    )
      .param("userId", userId.id)
      .param("taskId", taskId.id)
      .query(mapUserTask)
      .single()
  }

  private val mapUserTask = RowMapper<UserTask> { rs, _ ->
    UserTask(
      AgreementId(rs.getLong("agreement_id")),
      TaskId(rs.getLong("task_id")),
      rs.getLong("user_id"),
      rs.getString("name"),
      UserTaskRole.valueOf(rs.getString("role")),
      rs.getTimestamp("created_at").toInstant()
    )
  }
}
