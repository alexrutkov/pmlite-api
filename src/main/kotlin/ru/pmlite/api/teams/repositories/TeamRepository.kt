package ru.pmlite.api.teams.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.tags.domains.TagDetails
import ru.pmlite.api.teams.dto.*
import ru.pmlite.api.values.*
import kotlin.jvm.optionals.getOrNull

val mapTeamSummary = RowMapper<TeamSummary> { rs, _ ->
  TeamSummary(
    rs.getLong("id").let(::TeamId),
    rs.getString("name"),
    rs.getString("description"),
    rs.getTimestamp("created_at").toInstant(),
    rs.getLong("likeAmount"),
    rs.getLong("starAmount"),
  )
}

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

  fun addTeamTask(role: TeamTaskRoleDto) {
    jdbcClient.sql("""
            INSERT INTO task_teams (team_id, task_id, agreement_id)
            VALUES (:teamId, :taskId, :agreementId)
        """.trimIndent())
      .param("teamId", role.teamId.id)
      .param("taskId", role.taskId.id)
      .param("agreementId", role.agreementId.id)
      .update()
  }

  fun getAllTeams(userId: UserId, pageable: Pageable): List<TeamSummary> {
    return jdbcClient.sql("""
            with usedTags as (
                select tag_id from account_tags where user_id = :userId and state = 'ACTIVE'
            )
            select distinct on (t.id) 
                 t.id, t.name, t.description, t.created_at,
                 (select count(*) from likes l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as starAmount
            from teams t
             join agreements a on t.agreement_id = a.id
             left join team_tags tt on t.id = tt.team_id
             where  a.state = 'APPROVED'
             and (
                (select count(*) = 0 from usedTags) 
                or (tt.tag_id in (select tag_id from usedTags))
            )
            order by t.id offset :offset limit :limit
        """.trimIndent())
      .param("limit", pageable.pageSize)
      .param("offset", pageable.offset)
      .param("userId", userId.id)
      .query(mapTeamSummary)
      .list()
  }

  fun getMyTeams(userId: UserId, pageable: Pageable): List<TeamSummary> {
    return jdbcClient.sql("""
            with cte as (
                select distinct tt.team_id from team_users t
                    join task_teams tt on tt.team_id = t.team_id
                where t.user_id = :userId
                union distinct 
                select t.team_id from team_users t where t.user_id = :userId
                union distinct 
                select s.entity_id as team_id from stars s where s.user_id = :userId and state = 'ACTIVE' and type = 'TEAM'
            ), usedTags as (
                select tag_id from account_tags where user_id = :userId and state = 'ACTIVE'
            )
            select distinct on (t.id) 
                t.id, t.name, t.description, t.created_at,
                 (select count(*) from likes l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as starAmount 
            from teams t 
                join cte on cte.team_id = t.id
                left join team_tags tt on t.id = tt.team_id
            where (select count(*) = 0 from usedTags) or (tt.tag_id in (select tag_id from usedTags))
            order by id desc offset :offset limit :limit
        """.trimIndent())
      .param("limit", pageable.pageSize)
      .param("offset", pageable.offset)
      .param("userId", userId.id)
      .query(mapTeamSummary)
      .list()
  }

  fun getMyOwnerTeams(userId: UserId): List<TeamSummary> {
    return jdbcClient.sql("""
            with cte as (
                select t.team_id from team_users t 
                    join agreements a on t.agreement_id = a.id
                where t.user_id = :userId and t.role = 'OWNER' and a.state = 'APPROVED'
            )
            select distinct on (t.id) 
                t.id, t.name, t.description, t.created_at,
                 (select count(*) from likes l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as starAmount 
            from teams t join cte on cte.team_id = t.id
                join agreements a on t.agreement_id = a.id 
            where a.state = 'APPROVED'
        """.trimIndent())
      .param("userId", userId.id)
      .query(mapTeamSummary)
      .list()
  }

  fun getTeam(teamId: TeamId): TeamDetails {
    return TeamDetails(
      getTeamSummary(teamId),
      getTeamTags(teamId)
    )
  }

  private fun getTeamTags(teamId: TeamId): MutableList<TagDetails> {
    return jdbcClient.sql("""
      select t.tag, t.id, a.state from team_tags tt 
            join tags t on tt.tag_id = t.id
            join agreements a on t.agreement_id = a.id
            where team_id = :teamId
    """.trimIndent())
      .param("teamId", teamId.id)
      .query { rs, _ ->
        TagDetails(
          TagId(rs.getLong("id")),
          rs.getString("tag"),
          AgreementState.valueOf(rs.getString("state"))
        )
      }.list()
  }

  private fun getTeamSummary(teamId: TeamId): TeamSummary {
    return jdbcClient.sql("""
      select distinct on (t.id) 
                t.id, t.name, t.description, t.created_at,
                 (select count(*) from likes l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as starAmount 
            from teams t
       where t.id = :teamId
    """.trimIndent())
      .param("teamId", teamId.id)
      .query(mapTeamSummary)
      .single()
  }

  fun updateTeam(teamId: TeamId, command: UpdateTeamCommand) {
    jdbcClient.sql("""
      update teams t 
          set name = :name, description = :description
      where t.id = :teamId
    """.trimIndent())
      .param("teamId", teamId.id)
      .param("name", command.name)
      .param("description", command.description)
      .update()
  }



  fun findAgreementByTeamUser(teamId: TeamId, userId: UserId): AgreementId? {
    return jdbcClient.sql("""
      select agreement_id from team_users where team_id = :teamId and user_id = :userId
    """.trimIndent())
      .param("teamId", teamId.id)
      .param("userId", userId.id)
      .query(Long::class.java)
      .optional()
      .map(::AgreementId)
      .getOrNull()
  }

  fun findAgreementByTeamTask(teamId: TeamId, taskId: TaskId): AgreementId? {
    return jdbcClient.sql("""
      select agreement_id from task_teams where team_id = :teamId and task_id = :taskId
    """.trimIndent())
      .param("teamId", teamId.id)
      .param("taskId", taskId.id)
      .query(Long::class.java)
      .optional()
      .map(::AgreementId)
      .getOrNull()

  }


}
