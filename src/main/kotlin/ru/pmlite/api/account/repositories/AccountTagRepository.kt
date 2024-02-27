package ru.pmlite.api.account.repositories

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.account.domains.AccountTag
import ru.pmlite.api.account.domains.ActivityState
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.tags.domains.TagDetails
import ru.pmlite.api.tags.dto.TagDto
import ru.pmlite.api.values.AccountTagId
import ru.pmlite.api.values.TagId
import ru.pmlite.api.values.UserId

@Repository
class AccountTagRepository(
    private val jdbcClient: JdbcClient,
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun getAccountTagsByUser(userId: UserId): List<AccountTag> {
        return jdbcClient.sql("""
            select 
                at.id, at.state,
                t.tag, t.id as tagId,
                at.created_at
            from account_tags at 
                join tags t on at.tag_id = t.id
                join agreements a on t.agreement_id = a.id
            where at.user_id = :userId and a.state = 'APPROVED'
        """.trimIndent())
            .param("userId", userId.id)
            .query { rs, _ ->
                AccountTag(
                    AccountTagId(rs.getLong("id")),
                    TagDetails(
                        TagId(rs.getLong("tagId")),
                        rs.getString("tag"),
                        AgreementState.APPROVED
                    ),
                    ActivityState.valueOf(rs.getString("state")),
                    rs.getTimestamp("created_at").toInstant()
                )
            }.list()
    }

    fun addAccountTags(userId: UserId, tags: List<TagDto>) {
        jdbcTemplate.batchUpdate("""
            insert into account_tags (user_id, tag_id) 
            values (:userId, :tagId)
            on conflict (user_id, tag_id) do update set state = 'ACTIVE'
        """.trimIndent(),
            tags.map {
                MapSqlParameterSource("userId", userId.id)
                    .addValue("tagId", it.tagId.id)
            }.toTypedArray()
            )
    }
}
