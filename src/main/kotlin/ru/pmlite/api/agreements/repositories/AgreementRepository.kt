package ru.pmlite.api.agreements.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.dto.AgreementSummary
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.UserId

@Repository
class AgreementRepository(
  private val jdbcTemplate: NamedParameterJdbcTemplate,
  private val jdbcClient: JdbcClient
) {
  fun createAgreement(type: AgreementType, userId: UserId): AgreementId {
    val keyHolder = GeneratedKeyHolder()
    jdbcTemplate.update(
      """
            INSERT INTO agreements (user_id, type)
            VALUES (:userId, :type::agreement_type) returning id
        """.trimIndent(),
      MapSqlParameterSource("type", type.name)
        .addValue("userId", userId.id),
      keyHolder
    )
    return (keyHolder.keys?.get("id") as Long).let(::AgreementId)
  }

  fun updateAgreement(agreementId: AgreementId, state: AgreementState) {
    jdbcTemplate.update(
      """
            update agreements set state = :state::agreement_state where id = :id
        """.trimIndent(),
      MapSqlParameterSource("id", agreementId.id)
        .addValue("state", state.name)
    )
  }

  fun findTags(agreementId: AgreementId? = null, pageable: Pageable? = null): List<AgreementId> {
    return jdbcTemplate.query(
      """
            select id from agreements a where 
             CASE WHEN :id::bigint is null THEN a.state = 'PENDING' ELSE a.id = :id END
             and a.type = 'TAG'
            offset :offset limit :limit
        """.trimIndent(),
      MapSqlParameterSource("offset", pageable?.offset)
        .addValue("id", agreementId?.id)
        .addValue("limit", pageable?.pageSize)
    )
    { rs, _ -> AgreementId(rs.getLong("id")) }

  }

  fun findTeamAgreements(agreementId: AgreementId? = null, pageable: Pageable? = null): List<AgreementId> {
    return jdbcClient.sql(
      """
      select id from agreements a 
      where 
          CASE WHEN :id::bigint is null THEN a.state = 'PENDING' ELSE a.id = :id END
          and a.type = 'TEAM'
      offset :offset limit :limit
    """.trimIndent()
    )
      .param("offset", pageable?.offset)
      .param("limit", pageable?.pageSize)
      .param("id", agreementId?.id)
      .query(mapToAgreement)
      .list()
  }

  fun findTask(userId: UserId, agreementId: AgreementId? = null, pageable: Pageable? = null): List<AgreementId> {
    return jdbcTemplate.query(
      """
            with cte as (
                select concat_ws('.', '*', task_id, '*') as path from task_users where user_id = :userId and role = 'OWNER'
            )
            select a.id from agreements a
             join tasks t on a.id = t.agreement_id
             where 
                CASE WHEN :id::bigint is null THEN a.state = 'PENDING' ELSE a.id = :id END
                and a.type = 'TASK'
                and t.path ?? (select array_agg(cte.path) from cte)::lquery[]
             offset :offset limit :limit
        """.trimIndent(),
      MapSqlParameterSource("userId", userId.id)
        .addValue("id", agreementId?.id)
        .addValue("offset", pageable?.offset)
        .addValue("limit", pageable?.pageSize)
    ) { rs, _ -> AgreementId(rs.getLong("id")) }
  }

  fun findUserTask(userId: UserId, agreementId: AgreementId? = null, pageable: Pageable? = null): List<AgreementId> {
    return jdbcTemplate.query(
      """
            select a.id from agreements a
              join task_users tu on a.id = tu.agreement_id
              join task_users tu2 on tu.task_id = tu2.task_id
            where
                CASE WHEN :id::bigint is null THEN a.state = 'PENDING' ELSE a.id = :id END
                and a.type = 'TASK_USER'
                and tu2.user_id = :userId and tu2.role = 'OWNER'
                and tu2.user_id != tu.user_id
            offset :offset limit :limit
        """.trimIndent(), MapSqlParameterSource("userId", userId.id)
        .addValue("id", agreementId?.id)
        .addValue("offset", pageable?.offset)
        .addValue("limit", pageable?.pageSize)
    )
    { rs, _ -> AgreementId(rs.getLong("id")) }
  }

  fun findTeamUsers(userId: UserId, agreementId: AgreementId? = null, pageable: Pageable? = null): List<AgreementId> {
    return jdbcClient.sql("""
      select a.id from agreements a
              join team_users tu on a.id = tu.agreement_id
              join team_users tu2 on tu.team_id = tu2.team_id
            where
                CASE WHEN :agreementId::bigint is null THEN a.state = 'PENDING' ELSE a.id = :agreementId END
                and a.type = 'TEAM_USER'
                and tu2.user_id = :userId and tu2.role = 'OWNER'
                and tu2.user_id != tu.user_id
            offset :offset limit :limit
    """.trimIndent())
      .param("userId", userId.id)
      .param("agreementId", agreementId?.id)
      .param("offset", pageable?.offset)
      .param("limit", pageable?.pageSize)
      .query(Long::class.java)
      .list()
      .map(::AgreementId)
  }

  fun findTaskTeams(userId: UserId, agreementId: AgreementId? = null, pageable: Pageable? = null): List<AgreementId> {
    return jdbcClient.sql("""
      select a.id from agreements a
              join task_teams tt on a.id = tt.agreement_id
              join task_users tu on tu.task_id = tt.task_id
            where
                CASE WHEN :agreementId::bigint is null THEN a.state = 'PENDING' ELSE a.id = :agreementId END
                and a.type = 'TASK_TEAM'
                and tu.user_id = :userId and tu.role = 'OWNER'
            offset :offset limit :limit
    """.trimIndent())
      .param("userId", userId.id)
      .param("agreementId", agreementId?.id)
      .param("offset", pageable?.offset)
      .param("limit", pageable?.pageSize)
      .query(Long::class.java)
      .list()
      .map(::AgreementId)
  }

  fun getAgreementDetailsByIds(ids: List<AgreementId>): List<AgreementSummary> {
    return if (ids.isNotEmpty()) jdbcTemplate.query(
      """
            select 
                a.id, a.created_at, a.type, u.id as userId, u.name, a.state,
                (
                    CASE
                        WHEN a.type = 'TASK' 
                            THEN (
                select jsonb_build_object('taskName', t.name, 'taskId', t.id) 
                from tasks t where t.agreement_id = a.id
                                    )
                        WHEN a.type = 'TEAM' 
                            THEN (
                select jsonb_build_object('teamName', t.name, 'teamId', t.id) 
                from teams t where t.agreement_id = a.id
                                    )
                        WHEN a.type = 'TASK_USER' 
                            THEN (
                select jsonb_build_object('taskName', tt.name, 'taskId', tt.id)
                from task_users t join tasks tt on t.task_id = tt.id where t.agreement_id = a.id
                                    )
                        WHEN a.type = 'TAG' 
                            THEN (
                select jsonb_build_object('tag', t.tag, 'tagId', t.id) 
                from tags t where t.agreement_id = a.id
                                    )
                        WHEN a.type = 'TASK_TEAM' 
                            THEN (
                select jsonb_build_object(
                        'teamName', tt.name, 'teamId', tt.id,
                        'taskName', ttt.name, 'taskId', ttt.id
                        )
                from task_teams t join teams tt on t.team_id = tt.id join tasks ttt on t.task_id = ttt.id
                where t.agreement_id = a.id
                                    )
                        WHEN a.type = 'TEAM_USER' 
                            THEN (
                            select jsonb_build_object('teamName', tt.name, 'teamId', tt.id) from team_users t 
                            join teams tt on t.team_id = tt.id where t.agreement_id = a.id
                            )
                    END
                ) as details
            from agreements a join users u on a.user_id = u.id
            where a.id in (:ids)
        """.trimIndent(), MapSqlParameterSource("ids", ids.map(AgreementId::id))
    ) { rs, _ ->
      AgreementSummary(
        AgreementId(rs.getLong("id")),
        UserSummary(rs.getLong("userId"), rs.getString("name")),
        AgreementType.valueOf(rs.getString("type")),
        rs.getTimestamp("created_at").toInstant(),
        AgreementState.valueOf(rs.getString("state")),
        rs.getString("details")
      )
    } else emptyList()
  }


  private val mapToAgreement = RowMapper<AgreementId> { rs, _ ->
    AgreementId(rs.getLong("id"))
  }
}
