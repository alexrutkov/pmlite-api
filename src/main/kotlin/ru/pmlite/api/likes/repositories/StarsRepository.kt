package ru.pmlite.api.likes.repositories

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.likes.dto.EntityDto
import ru.pmlite.api.values.EntityId
import ru.pmlite.api.values.UserId

@Repository
class StarsRepository(
  private val jdbcClient: JdbcClient
) {
  fun addStar(userId: UserId, like: EntityDto) {
    jdbcClient.sql("""
      insert into stars (user_id, entity_id, type) 
      values (:userId, :entityId, :type::entity_type)
      on conflict (user_id, entity_id, type) do update set state = 'ACTIVE', updated_at = now()
    """.trimIndent())
      .param("userId", userId.id)
      .param("entityId", like.entityId.id)
      .param("type", like.type.name)
      .update()
  }

  fun findStarsBy(userId: UserId, entities: List<EntityId>, type: EntityType): List<EntityId> {
    return jdbcClient.sql("""
      select entity_id from stars 
      where user_id = :userId 
          and type = :type::entity_type 
          and entity_id in (:entities)
          and state = 'ACTIVE'
    """.trimIndent())
      .param("userId", userId.id)
      .param("entities", entities.map(EntityId::id))
      .param("type", type.name)
      .query(Long::class.java)
      .list()
      .map(::EntityId)
  }

  fun disStar(userId: UserId, like: EntityDto) {
    jdbcClient.sql("""
      update stars set state = 'CANCELLED', updated_at = now()
      where user_id = :userId 
          and type = :type::entity_type 
          and entity_id = :entityId
    """.trimIndent())
      .param("userId", userId.id)
      .param("entityId", like.entityId.id)
      .param("type", like.type.name)
      .update()
  }
}
