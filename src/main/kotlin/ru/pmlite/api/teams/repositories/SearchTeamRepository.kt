package ru.pmlite.api.teams.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.teams.dto.TeamSummary
import ru.pmlite.api.values.UserId


@Repository
class SearchTeamRepository(
  private val jdbcClient: JdbcClient
) {
  fun searchAllTeams(search: String, pageable: Pageable): List<TeamSummary> {
    return jdbcClient.sql("""
            select distinct on (t.id) 
                 t.id, t.name, t.description, t.created_at,
                 (select count(*) from likes l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as starAmount  
            from teams t
             join agreements a on t.agreement_id = a.id
             where  a.state = 'APPROVED'  and lower(t.name) like :search
            order by t.id offset :offset limit :limit
        """.trimIndent())
      .param("limit", pageable.pageSize)
      .param("search", "%${search.lowercase()}%")
      .param("offset", pageable.offset)
      .query(mapTeamSummary)
      .list()
  }

  fun searchMyTeams(search: String, userId: UserId, pageable: Pageable): List<TeamSummary> {
    return jdbcClient.sql("""
             with cte as (
                select distinct tt.team_id from team_users t
                    join task_teams tt on tt.team_id = t.team_id
                where t.user_id = :userId
                union distinct 
                select t.team_id from team_users t where t.user_id = :userId
                union distinct 
                select s.entity_id as team_id from stars s where s.user_id = :userId and state = 'ACTIVE' and type = 'TEAM'
            )
            select distinct on (t.id) 
                 t.id, t.name, t.description, t.created_at,
                 (select count(*) from likes l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = t.id and l.type = 'TEAM' and l.state = 'ACTIVE') as starAmount 
            from teams t join cte on cte.team_id = t.id
            where lower(t.name) like :search
            order by t.id desc offset :offset limit :limit
        """.trimIndent())
      .param("limit", pageable.pageSize)
      .param("search", "%${search.lowercase()}%")
      .param("offset", pageable.offset)
      .param("userId", userId.id)
      .query(mapTeamSummary)
      .list()
  }
}
