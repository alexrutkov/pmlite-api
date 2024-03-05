package ru.pmlite.api.account.repositories

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.account.dto.*
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.teams.domain.UserTeamRole
import ru.pmlite.api.users.exceptions.UserNotFoundException
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId
import ru.pmlite.api.values.UserId

@Repository
class AccountRepository(
  private val jdbcTemplate: NamedParameterJdbcTemplate,
  private val jdbcClient: JdbcClient
) {
  fun getAccountDetails(userId: UserId): AccountDetails {
    return jdbcClient.sql( """ 
      select  name from users where id = :id
      """.trimIndent())
      .param("id", userId.id)
      .query { rs, _ ->
        AccountDetails(
          userId.id, rs.getString("name")
        )
      }
      .optional()
      .orElseThrow { UserNotFoundException() }
  }

  fun getAccountRoles(userId: UserId): List<UserRole> {
    return jdbcTemplate.query(
      """
            select role from user_roles where user_id = :id
        """.trimIndent(), MapSqlParameterSource("id", userId.id)
    ) { rs, _ ->
      rs.getString("role").let(UserRole::valueOf)
    }
  }

  fun getAccountTaskRoles(userId: UserId): List<AccountTaskRole> {
    return jdbcTemplate.query(
      """
            select 
                task_id, role 
            from task_users t join agreements a on t.agreement_id = a.id
            where t.user_id = :id and a.state != 'CANCELLED'
        """.trimIndent(), MapSqlParameterSource("id", userId.id)
    ) { rs, _ ->
      AccountTaskRole(
        TaskId(rs.getLong("task_id")),
        UserTaskRole.valueOf(rs.getString("role"))
      )
    }
  }

  fun getAccountTeamRoles(userId: UserId): List<AccountTeamRole> {
    return jdbcClient.sql("""
      select 
          t.team_id, t.role
      from team_users t join agreements a on t.agreement_id = a.id
      where t.user_id = :userId and a.state != 'CANCELLED'
    """.trimIndent())
      .param("userId", userId.id)
      .query {rs, _ ->
        AccountTeamRole(
          TeamId(rs.getLong("team_id")),
          UserTeamRole.valueOf(rs.getString("role"))
        )
      }.list()
  }

  fun getAccountTaskTeams(userId: UserId): List<AccountTaskTeamRole> {
    return jdbcClient.sql("""
      with myTeams as (
          select  t.team_id from team_users t join agreements a on t.agreement_id = a.id
          where t.user_id = :userId and a.state = 'APPROVED')
      select * from task_teams tt 
          join myTeams mt on mt.team_id = tt.team_id
          join agreements a on tt.agreement_id = a.id
      where a.state != 'CANCELLED'
    """.trimIndent())
      .param("userId", userId.id)
      .query {rs, _ ->
        AccountTaskTeamRole(
          TaskId(rs.getLong("task_id")),
          TeamId(rs.getLong("team_id"))
        )
      }.list()
  }

  fun getProfileDetails(userId: UserId): ProfileDetails {
    return runCatching {
      jdbcTemplate.queryForObject(
        """
                select 
                    name, description
                from users where id = :id
            """.trimIndent(), MapSqlParameterSource("id", userId.id)
      ) { rs, _ ->
        ProfileDetails(
          rs.getString("name"),
          rs.getString("description"),
          emptyList()
        )
      } ?: throw UserNotFoundException()
    }.getOrThrow()
  }

  fun saveProfile(userId: UserId, command: SaveProfileCommand) {
    jdbcTemplate.update(
      """
            update users set name = :name, description = :description
            where id = :userId
        """.trimIndent(),
      MapSqlParameterSource("userId", userId.id)
        .addValue("name", command.name)
        .addValue("description", command.description)
    )
  }
}
