package ru.pmlite.api.teams.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.teams.domain.TeamTask
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

@Repository
class TeamTaskRepository(
  private val jdbcClient: JdbcClient
) {

  fun getTaskRelation(teamId: TeamId, pageable: Pageable): List<TeamTask> {
    return jdbcClient.sql(
      """
            select 
                tu.task_id, t.name,
                tu.created_at,
                tu.team_id,
                tu.agreement_id 
            from task_teams tu 
            join agreements a on tu.agreement_id = a.id
            join tasks t on t.id = tu.task_id
            where tu.team_id = :teamId and a.state = 'APPROVED'
            offset :offset limit :limit
        """.trimIndent()
    )
      .param("teamId", teamId.id)
      .param("offset", pageable.offset)
      .param("limit", pageable.pageSize)
      .query(mapTeamTask)
      .list()
  }

  fun getTeamTask(teamId: TeamId, taskId: TaskId): TeamTask {
    return jdbcClient.sql(
      """
            select 
                tu.task_id, t.name,
                tu.created_at,
                tu.team_id,
                tu.agreement_id 
            from task_teams tu 
            join agreements a on tu.agreement_id = a.id
            join tasks t on t.id = tu.task_id
            where tu.team_id = :teamId and tu.task_id = :taskId
        """.trimIndent()
    )
      .param("teamId", teamId.id)
      .param("taskId", taskId.id)
      .query(mapTeamTask)
      .single()
  }

  private val mapTeamTask = RowMapper<TeamTask> { rs, _ ->
    TeamTask(
      AgreementId(rs.getLong("agreement_id")),
      TeamId(rs.getLong("team_id")),
      TaskId(rs.getLong("task_id")),
      rs.getString("name"),
      rs.getTimestamp("created_at").toInstant()
    )
  }
}
