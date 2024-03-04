package ru.pmlite.api.tasks.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.tasks.domain.TaskTeam
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

@Repository
class TaskTeamRepository(
  private val jdbcClient: JdbcClient
) {

  fun getTeams(taskId: TaskId, pageable: Pageable): List<TaskTeam> {
    return jdbcClient.sql(
      """
            select 
                 a.id as agreement_id,
                 tu.team_id,
                 tu.task_id,
                 t.name,
                 tu.created_at
            from task_teams tu
                join teams t on tu.team_id = t.id
                join agreements a on tu.agreement_id = a.id
            where tu.task_id = :taskId and a.state = 'APPROVED'
            order by tu.created_at desc offset :offset limit :limit
        """.trimIndent()
    )
      .param("taskId", taskId.id)
      .param("offset", pageable.offset)
      .param("limit", pageable.pageSize)
      .query(mapTaskTeam)
      .list()
  }

  fun getTaskTeam(taskId: TaskId, teamId: TeamId): TaskTeam {
    return jdbcClient.sql(
      """
            select 
                 a.id as agreement_id,
                 tu.team_id,
                 tu.task_id,
                 t.name,
                 tu.created_at
            from task_teams tu
                join teams t on tu.team_id = t.id
                join agreements a on tu.agreement_id = a.id
            where tu.task_id = :taskId and tu.team_id = :teamId
        """.trimIndent()
    )
      .param("taskId", taskId.id)
      .param("teamId", teamId.id)
      .query(mapTaskTeam)
      .single()
  }

  private val mapTaskTeam = RowMapper<TaskTeam> { rs, _ ->
    TaskTeam(
      AgreementId(rs.getLong("agreement_id")),
      TeamId(rs.getLong("team_id")),
      TaskId(rs.getLong("task_id")),
      rs.getString("name"),
      rs.getTimestamp("created_at").toInstant()
    )
  }
}
