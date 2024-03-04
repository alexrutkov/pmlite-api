package ru.pmlite.api.users.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.users.services.UserTaskService
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@RequestMapping("/api/users")
@RestController
class UserTasksController(
    private val service: UserTaskService
) {

    @GetMapping("{id}/tasks")
    fun getUserTasks(@PathVariable id: Long, pageable: Pageable) = service.getTasks(UserId(id), pageable)

    @DeleteMapping("tasks/{taskId}")
    fun cancelTask(@PathVariable taskId: TaskId) = service.cancelTask(taskId)
}
