package ru.pmlite.api.agreements.repositories

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pmlite.api.agreements.dto.DecisionCommand
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
}
