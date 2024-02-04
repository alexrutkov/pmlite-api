package ru.pmlite.api.tags.repositories

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.tags.domains.Tag
import ru.pmlite.api.tags.dto.CreateTagCommand
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TagId

@Repository
class TagsRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun createTag(command: CreateTagCommand, agreementId: AgreementId): TagId {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update("""
            insert into tags (tag, agreement_id) 
            values (lower(:tag), :agreementId)
            on conflict (tag) do nothing returning id
            """,
            MapSqlParameterSource("tag", command.tag)
                .addValue("agreementId", agreementId.id),
            keyHolder
        )
        return (keyHolder.keys?.get("id") as Long).let(::TagId)
    }

    fun searchTags(tag: String): List<Tag> {
        return jdbcTemplate.query("""
            select * from tags where tag like :tag
        """.trimIndent(),
            MapSqlParameterSource("tag", tag.lowercase().plus("%")),
        mapToTag)
    }

    private val mapToTag = RowMapper<Tag> { rs, _ ->
        Tag(
            TagId(rs.getLong("id")),
            rs.getString("tag")
        )
    }
}
