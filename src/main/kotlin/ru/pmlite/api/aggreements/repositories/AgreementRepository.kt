package ru.pmlite.api.aggreements.repositories

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.aggreements.domains.AgreementState
import ru.pmlite.api.aggreements.domains.AgreementType
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
}
