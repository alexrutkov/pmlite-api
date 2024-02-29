package ru.pmlite.api.agreements.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.domains.Decision
import ru.pmlite.api.agreements.domains.DecisionDetails
import ru.pmlite.api.agreements.domains.DecisionMode
import ru.pmlite.api.agreements.dto.DecisionCommand
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.DecisionId
import ru.pmlite.api.values.UserId

@Repository
class DecisionRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun saveDecision(command: DecisionCommand, userId: UserId) {
        jdbcTemplate.update("""
            insert into decisions(agreement_id, mode, decision, user_id, comment) 
            values (:agreementId, :mode::decision_mode, :decision::decision, :userId, :comment)
        """.trimIndent(),
            MapSqlParameterSource("agreementId", command.agreementId.id)
                .addValue("decision", command.decision.name)
                .addValue("mode", command.mode.name)
                .addValue("userId", userId.id)
                .addValue("comment", command.comment)
            )
    }

    fun getDecisions(type: AgreementType?, pageable: Pageable): List<DecisionDetails> {
        return  jdbcTemplate.query("""
            select 
             d.id,
             d.mode,
             d.created_at,
             d.comment,
             d.decision,
             d.agreement_id,
             u.id as user_id,
             u.name
            from decisions d 
                join users u on d.user_id = u.id
                join agreements a on d.agreement_id = a.id
            where ((:type::agreement_type is null) or a.type = :type)
            order by d.created_at desc
            offset :offset limit :limit
        """.trimIndent(),
          MapSqlParameterSource("offset", pageable.offset)
            .addValue("limit", pageable.pageSize)
            .addValue("type", type?.name),
          mapToDecision
        )
    }

  fun getMyDecisions(userId: UserId, type: AgreementType?, pageable: Pageable): List<DecisionDetails>  {
    return  jdbcTemplate.query("""
            select 
             d.id,
             d.mode,
             d.created_at,
             d.comment,
             d.decision,
             d.agreement_id,
             u.id as user_id,
             u.name
            from decisions d 
                join users u on d.user_id = u.id
                join agreements a on d.agreement_id = a.id
            where d.user_id = :userId 
                and ((:type::agreement_type is null) or a.type = :type)
            order by d.created_at desc offset :offset limit :limit
        """.trimIndent(),
      MapSqlParameterSource("offset", pageable.offset)
        .addValue("limit", pageable.pageSize)
        .addValue("userId", userId.id)
        .addValue("type", type?.name),
      mapToDecision
    )
  }

  private val mapToDecision = RowMapper<DecisionDetails> { rs, _ ->
    DecisionDetails(
      DecisionId(rs.getLong("id")),
      UserSummary(rs.getLong("user_id"), rs.getString("name")),
      rs.getString("comment"),
      Decision.valueOf(rs.getString("decision")),
      DecisionMode.valueOf(rs.getString("mode")),
      rs.getTimestamp("created_at").toInstant(),
      AgreementId(rs.getLong("agreement_id"))
    )
  }


}
