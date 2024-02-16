package ru.pmlite.api.aggreements.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.aggreements.domains.AgreementState
import ru.pmlite.api.aggreements.domains.AgreementType
import ru.pmlite.api.aggreements.dto.AgreementSummary
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.UserId

@Repository
class AgreementRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun createAgreement(type: AgreementType, userId: UserId): AgreementId {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update("""
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
        jdbcTemplate.update("""
            update agreements set state = :state::agreement_state where id = :id
        """.trimIndent(),
            MapSqlParameterSource("id", agreementId.id)
                .addValue("state", state.name)
            )
    }

    fun findPendingTags(pageable: Pageable? = null): List<Long> {
        return jdbcTemplate.query("""
            select id from agreements where state = 'PENDING' and type = 'TAG'
            offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("offset", pageable?.offset)
            .addValue("limit", pageable?.pageSize)) {rs, _ -> rs.getLong("id")}

    }

    fun findPendingTask(userId: UserId, pageable: Pageable? = null): List<Long> {
        return jdbcTemplate.query("""
            with cte as (
                select concat_ws('.', '*', task_id, '*') as path from task_users where user_id = :userId and role = 'OWNER'
            )
            select a.id from agreements a
             join tasks t on a.id = t.agreement_id
             where 
                a.state = 'PENDING' and a.type = 'TASK'
                and t.path ?? (select array_agg(cte.path) from cte)::lquery[]
             offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("userId", userId.id)
                .addValue("offset", pageable?.offset)
                .addValue("limit", pageable?.pageSize)
        ) {rs, _ ->
            rs.getLong("id")
        }
    }

    fun findPendingUserTask(userId: UserId, pageable: Pageable? = null): List<Long> {
        return jdbcTemplate.query("""
            select a.id from agreements a
              join task_users tu on a.id = tu.agreement_id
              join task_users tu2 on tu.task_id = tu2.task_id
            where
                a.state = 'PENDING' and a.type = 'TASK_USER'
                and tu2.user_id = :userId and tu2.role = 'OWNER'
            offset :offset limit :limit
        """.trimIndent(), MapSqlParameterSource("userId", userId.id)
            .addValue("offset", pageable?.offset)
            .addValue("limit", pageable?.pageSize)) {rs, _ ->
            rs.getLong("id")
        }
    }

    fun getAgreementDetailsByIds(ids: List<Long>): List<AgreementSummary> {
        return jdbcTemplate.query("""
            select 
                a.id, a.created_at, a.type, u.id as userId, u.name,
                (
                    CASE
                        WHEN a.type = 'TASK' 
                            THEN (
                select jsonb_build_object('taskName', t.name, 'taskId', t.id) 
                from tasks t where t.agreement_id = a.id
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
        """.trimIndent(), MapSqlParameterSource("ids", ids)) {rs, _ ->
            AgreementSummary(
                AgreementId(rs.getLong("id")),
                UserSummary(rs.getLong("userId"), rs.getString("name")),
                AgreementType.valueOf(rs.getString("type")),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("details")
            )
        }
    }
}
