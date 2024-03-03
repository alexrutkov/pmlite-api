package ru.pmlite.api.tags.repositories

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.tags.domains.TagDetails
import ru.pmlite.api.tags.dto.CreateTagCommand
import ru.pmlite.api.tags.dto.TagDto
import ru.pmlite.api.values.*

@Repository
class TagsRepository(
  private val jdbcTemplate: NamedParameterJdbcTemplate
) {
  fun createTag(command: CreateTagCommand, agreementId: AgreementId): TagId {
    val keyHolder = GeneratedKeyHolder()
    jdbcTemplate.update(
      """
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

  fun searchTags(tag: String): List<TagDetails> {
    return jdbcTemplate.query(
      """
            select 
                t.id, tag, a.state 
            from tags t join agreements a on a.id = t.agreement_id
            where tag like :tag
        """.trimIndent(),
      MapSqlParameterSource("tag", tag.lowercase().plus("%")),
      mapToTag
    )
  }

  fun getTagsByUser(userId: UserId): List<TagDetails> {
    return jdbcTemplate.query(
      """
            select 
                t.id, tag, a.state 
            from user_tags ut 
                join tags t on ut.tag_id = t.id
                join agreements a on a.id = t.agreement_id
            where ut.user_id = :id
        """.trimIndent(),
      MapSqlParameterSource("id", userId.id),
      mapToTag
    )
  }

  fun addTaskTags(taskId: TaskId, tags: List<TagDto>) {
    jdbcTemplate.batchUpdate(
      """
            insert into task_tags (task_id, tag_id) 
            values (:id, :tagId)
            on conflict do nothing 
        """.trimIndent(),
      tags.map {
        MapSqlParameterSource("id", taskId.id)
          .addValue("tagId", it.tagId.id)
      }.toTypedArray()
    )
  }

  fun deleteTaskTag(taskId: TaskId, tagId: TagId) {
    jdbcTemplate.update(
      """
            delete from task_tags where task_id = :id and tag_id = :tagId
        """.trimIndent(),
      mapOf("id" to taskId.id, "tagId" to tagId.id)
    )
  }

  fun addUserTags(userId: UserId, tags: List<TagDto>) {
    jdbcTemplate.batchUpdate(
      """
            insert into user_tags (user_id, tag_id) 
            values (:userId, :tagId)
            on conflict do nothing 
        """.trimIndent(),
      tags.map {
        MapSqlParameterSource("userId", userId.id)
          .addValue("tagId", it.tagId.id)
      }.toTypedArray()
    )
  }

  fun deleteUserTag(userId: UserId, tagId: TagId) {
    jdbcTemplate.update(
      """
            delete from user_tags
            where user_id = :userId and tag_id = :tagId
        """.trimIndent(),
      MapSqlParameterSource("userId", userId.id)
        .addValue("tagId", tagId.id)

    )
  }

  fun addTeamTags(teamId: TeamId, tags: List<TagDto>) {
    jdbcTemplate.batchUpdate(
      """
            insert into team_tags (team_id, tag_id) 
            values (:id, :tagId)
            on conflict do nothing 
        """.trimIndent(),
      tags.map {
        MapSqlParameterSource("id", teamId.id)
          .addValue("tagId", it.tagId.id)
      }.toTypedArray()
    )
  }

  private val mapToTag = RowMapper<TagDetails> { rs, _ ->
    TagDetails(
      TagId(rs.getLong("id")),
      rs.getString("tag"),
      AgreementState.valueOf(rs.getString("state"))
    )
  }
}
