package ru.pmlite.api.tasks.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.tasks.services.TaskUserService
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@RequestMapping("/api/tasks")
@RestController
class TaskUsersController(
    private val service: TaskUserService
) {

    @GetMapping("{id}/users")
    fun getUsers(@PathVariable id: Long, pageable: Pageable) = service.getUsers(TaskId(id), pageable)

    @DeleteMapping("{id}/users/{userId}")
    fun cancelUser(
        @PathVariable id: Long,
        @PathVariable userId: Long
    ) = service.cancelUser(TaskId(id), UserId(userId))
    @DeleteMapping("{id}")
    fun cancelTask(@PathVariable id: TaskId) = service.cancelTask(id)
}
