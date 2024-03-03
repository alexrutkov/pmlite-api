package ru.pmlite.api.teams.repositories

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.teams.dto.CreateTeamCommand
import ru.pmlite.api.teams.dto.TeamUserRoleDto
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TeamId

@Repository
class TeamRepository(
  private val jdbcClient: JdbcClient
) {
  fun createTeam(command: CreateTeamCommand, agreementId: AgreementId): TeamId {
    val keyHolder = GeneratedKeyHolder()
    jdbcClient.sql("""
      INSERT INTO teams (name, description,  agreement_id)
            VALUES ( :name, :description, :agreementId ) 
      returning id
    """.trimIndent())
      .param("name", command.name)
      .param("agreementId", agreementId.id)
      .param("description", command.description)
      .update(keyHolder)

    return (keyHolder.keys?.get("id") as Long).let(::TeamId)
  }

  fun addUserRole(role: TeamUserRoleDto) {
    jdbcClient.sql("""
            INSERT INTO team_users (team_id, user_id, role, agreement_id)
            VALUES (:teamId, :userId, :role::user_team_role, :agreementId)
        """.trimIndent())
      .param("teamId", role.teamId.id)
      .param("userId", role.userId.id)
      .param("role", role.role.name)
      .param("agreementId", role.agreementId.id)
      .update()
  }
}
