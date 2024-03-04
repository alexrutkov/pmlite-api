package ru.pmlite.api.teams.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.teams.domain.TeamUser
import ru.pmlite.api.teams.domain.UserTeamRole
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TeamId
import ru.pmlite.api.values.UserId

@Repository
class TeamUserRepository(
    private val jdbcClient: JdbcClient
) {

    fun getUserRelation(teamId: TeamId, pageable: Pageable): List<TeamUser> {
        return jdbcClient.sql("""
            select 
                tu.user_id, u.name, tu.created_at,
                tu.role, tu.team_id,
                tu.agreement_id 
            from team_users tu 
            join agreements a on tu.agreement_id = a.id
            join users u on tu.user_id = u.id
            where team_id = :teamId and a.state = 'APPROVED'
            offset :offset limit :limit
        """.trimIndent()
        )
          .param("teamId", teamId.id)
          .param("offset", pageable.offset)
          .param("limit", pageable.pageSize)
          .query(mapTeamUser)
          .list()
    }
    fun getTeamUser(teamId: TeamId, userId: UserId): TeamUser {
        return jdbcClient.sql("""
            select 
                tu.user_id, u.name, tu.created_at,
                tu.role, tu.team_id,
                tu.agreement_id
            from team_users tu 
            join agreements a on tu.agreement_id = a.id
            join users u on tu.user_id = u.id
            where tu.team_id = :taskId and tu.user_id = :userId
        """.trimIndent())
          .param("teamId", teamId.id)
          .param("userId", userId.id)
          .query(mapTeamUser)
          .single()
    }

    private val mapTeamUser = RowMapper<TeamUser> { rs, _ ->
      TeamUser(
            AgreementId(rs.getLong("agreement_id")),
            TeamId(rs.getLong("team_id")),
            UserSummary(rs.getLong("user_id"), rs.getString("name")),
            UserTeamRole.valueOf(rs.getString("role")),
            rs.getTimestamp("created_at").toInstant()
        )
    }
}
