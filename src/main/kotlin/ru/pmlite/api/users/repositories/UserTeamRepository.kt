package ru.pmlite.api.users.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.teams.domain.UserTeamRole
import ru.pmlite.api.users.domain.UserTeam
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TeamId
import ru.pmlite.api.values.UserId

@Repository
class UserTeamRepository(
  private val jdbcClient: JdbcClient
) {

  fun getTeams(userId: UserId, pageable: Pageable): List<UserTeam> {
    return jdbcClient.sql(
      """
            select 
                 a.id as agreement_id,
                 tu.team_id,
                 tu.user_id,
                 t.name,
                 tu.created_at,
                 tu.role
            from team_users tu
                join teams t on tu.team_id = t.id
                join agreements a on tu.agreement_id = a.id
            where tu.user_id = :userId and a.state = 'APPROVED'
            order by tu.created_at desc offset :offset limit :limit
        """.trimIndent()
    )
      .param("userId", userId.id)
      .param("offset", pageable.offset)
      .param("limit", pageable.pageSize)
      .query(mapUserTeam)
      .list()
  }

  fun getUserTeam(userId: UserId, teamId: TeamId): UserTeam {
    return jdbcClient.sql(
      """
            select 
                 a.id as agreement_id,
                 tu.team_id,
                 tu.user_id,
                 t.name,
                 tu.created_at,
                 tu.role
            from team_users tu
                join teams t on tu.team_id = t.id
                join agreements a on tu.agreement_id = a.id
            where tu.user_id = :userId and tu.team_id = :teamId
        """.trimIndent()
    )
      .param("userId", userId.id)
      .param("teamId", teamId.id)
      .query(mapUserTeam)
      .single()
  }

  private val mapUserTeam = RowMapper<UserTeam> { rs, _ ->
    UserTeam(
      AgreementId(rs.getLong("agreement_id")),
      TeamId(rs.getLong("team_id")),
      rs.getLong("user_id"),
      rs.getString("name"),
      UserTeamRole.valueOf(rs.getString("role")),
      rs.getTimestamp("created_at").toInstant()
    )
  }
}
