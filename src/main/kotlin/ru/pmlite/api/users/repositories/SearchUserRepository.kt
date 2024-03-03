package ru.pmlite.api.users.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.users.domain.UserShortDetails

@Repository
class SearchUserRepository(
  private val jdbcClient: JdbcClient
) {
  fun searchAllUsers(search: String, pageable: Pageable): List<UserShortDetails> {
    return jdbcClient.sql("""
            select distinct on (u.id) 
                 u.id, u.name, u.description, u.created_at,
                 (select count(*) from likes l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as starAmount 
            from users u
            where u.state != 'BLOCKED' and lower(u.name) like :search
            order by u.created_at desc offset :offset limit :limit
        """.trimIndent())
      .param("limit", pageable.pageSize)
      .param("offset", pageable.offset)
      .param("search", "%${search.lowercase()}%")
      .query(mapUserDetails)
      .list()
  }

  fun searchMyUsers(search: String, pageable: Pageable): List<UserShortDetails> {
    return jdbcClient.sql("""
            select 
                distinct on (u.id) 
                 u.id, u.name, u.description, u.created_at,
                 (select count(*) from likes l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as starAmount  
            from users u
            where u.state != 'BLOCKED' and lower(u.name) like :search
            order by u.created_at desc offset :offset limit :limit
        """.trimIndent())
      .param("limit", pageable.pageSize)
      .param("offset", pageable.offset)
      .param("search", "%${search.lowercase()}%")
      .query(mapUserDetails)
      .list()
  }
}
