package ru.pmlite.api.users.repositories

import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.users.domain.UserShortDetails
import ru.pmlite.api.users.domain.UserTask
import ru.pmlite.api.users.exceptions.UserNotFoundException
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

val mapUserDetails = RowMapper<UserShortDetails> { rs, _ ->
  UserShortDetails(
    rs.getLong("id"),
    rs.getString("name"),
    rs.getString("description"),
    rs.getTimestamp("created_at").toInstant(),
    rs.getLong("likeAmount"),
    rs.getLong("starAmount"),
  )
}

@Repository
class UsersRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun getAllUsers(userId: UserId, pageable: Pageable): List<UserShortDetails> {
        return jdbcTemplate.query("""
          with usedTags as (
                select tag_id from account_tags where user_id = :userId and state = 'ACTIVE'
            )
          select distinct on (u.id) 
                 u.id, u.name, u.description, u.created_at,
                 (select count(*) from likes l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as starAmount  
          from users u
            join user_tags ut on u.id = ut.user_id
            where u.state != 'BLOCKED' 
            and (
                (select count(*) = 0 from usedTags) 
                or (ut.tag_id in (select tag_id from usedTags))
            )
            order by u.id desc offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("limit", pageable.pageSize)
                .addValue("offset", pageable.offset)
              .addValue("userId", userId.id),
            mapUserDetails
        )
    }

    fun getMyUsers(pageable: Pageable): List<UserShortDetails> {
        return jdbcTemplate.query("""
            select distinct on (u.id) 
                 u.id, u.name, u.description, u.created_at,
                 (select count(*) from likes l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as starAmount 
            from users u join stars s on u.id = s.user_id
            where s.state = 'ACTIVE' and s.type = 'USER' and u.id = s.entity_id 
            order by u.id desc offset :offset limit :limit
        """.trimIndent(),
            MapSqlParameterSource("limit", pageable.pageSize)
                .addValue("offset", pageable.offset),
            mapUserDetails
        )
    }

    fun getUserDetails(userId: UserId): UserShortDetails {
        return runCatching {
            jdbcTemplate.queryForObject("""
            select
                 u.id, u.name, u.description, u.created_at,
                 (select count(*) from likes l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as likeAmount,
                 (select count(*) from stars l where l.entity_id = u.id and l.type = 'USER' and l.state = 'ACTIVE') as starAmount 
            from users u where u.id = :id
        """.trimIndent(), MapSqlParameterSource("id", userId.id), mapUserDetails)
        }.getOrNull() ?: throw UserNotFoundException()
    }

    fun getUserTasks(id: Long, pageable: Pageable): List<UserTask> {
        return jdbcTemplate.query("""
            select 
                 t.id,
                 tu.user_id,
                 t.name,
                 tu.created_at,
                 tu.role
            from task_users tu
                join tasks t on tu.task_id = t.id
                join agreements a on tu.agreement_id = a.id
            where tu.user_id = :id and a.state = 'APPROVED'
            order by tu.created_at desc offset :offset limit :limit
        """.trimIndent(), MapSqlParameterSource("id", id)
            .addValue("limit", pageable.pageSize)
            .addValue("offset", pageable.offset)
        ) { rs, _ ->
            UserTask(
                TaskId(rs.getLong("id")),
                rs.getLong("user_id"),
                rs.getString("name"),
                UserTaskRole.valueOf(rs.getString("role")),
                rs.getTimestamp("created_at").toInstant()
            )
        }
    }

}
