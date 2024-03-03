package ru.pmlite.api.tasks.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import ru.pmlite.api.tasks.dto.TaskSummary
import ru.pmlite.api.values.UserId

@Repository
class SearchTaskRepository(
  private val jdbcClient: JdbcClient
) {
  fun searchAllTasks(search: String, pageable: Pageable): List<TaskSummary> {
    return jdbcClient.sql("""
            select distinct on (t.id) 
                 t.id, t.name, t.short_description, t.created_at,
                 (select count(*) from likes l where l.entity_id = t.id and l.type = 'TASK' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = t.id and l.type = 'TASK' and l.state = 'ACTIVE') as starAmount  
            from tasks t
             join agreements a on t.agreement_id = a.id
             where  a.state = 'APPROVED'  and lower(t.name) like :search
            order by t.id offset :offset limit :limit
        """.trimIndent())
      .param("limit", pageable.pageSize)
      .param("search", "%${search.lowercase()}%")
      .param("offset", pageable.offset)
      .query(mapTaskSummary)
      .list()
  }

  fun searchMyTasks(search: String, userId: UserId, pageable: Pageable): List<TaskSummary> {
    return jdbcClient.sql("""
             with cte as (
                select distinct tt.task_id from team_users t
                    join task_teams tt on tt.team_id = t.team_id
                where t.user_id = :userId
                union distinct 
                select t.task_id from task_users t where t.user_id = :userId
                union distinct 
                select s.entity_id as task_id from stars s where s.user_id = :userId and state = 'ACTIVE' and type = 'TASK'
            )
            select distinct on (t.id) 
                 t.id, t.name, t.short_description, t.created_at,
                 (select count(*) from likes l where l.entity_id = t.id and l.type = 'TASK' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = t.id and l.type = 'TASK' and l.state = 'ACTIVE') as starAmount 
            from tasks t join cte on cte.task_id = t.id
            where lower(t.name) like :search
            order by t.id desc offset :offset limit :limit
        """.trimIndent())
      .param("limit", pageable.pageSize)
      .param("search", "%${search.lowercase()}%")
      .param("offset", pageable.offset)
      .param("userId", userId.id)
      .query(mapTaskSummary)
      .list()
  }
}
