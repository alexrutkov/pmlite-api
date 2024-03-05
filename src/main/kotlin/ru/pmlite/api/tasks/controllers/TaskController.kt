package ru.pmlite.api.tasks.controllers

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.tasks.dto.CreateTaskCommand
import ru.pmlite.api.tasks.dto.UpdateTaskCommand
import ru.pmlite.api.tasks.services.TaskService
import ru.pmlite.api.values.TagId
import ru.pmlite.api.values.TaskId

@RequestMapping("/api/tasks")
@RestController
class TaskController(
    private val service: TaskService
) {

    @PostMapping
    fun createTask(@Valid @RequestBody command: CreateTaskCommand) = service.createTask(command)

    @PutMapping("{id}")
    fun updateTask(
        @PathVariable id: TaskId,
        @Valid @RequestBody command: UpdateTaskCommand
        ) = service.updateTask(id, command)



    @PostMapping("{id}/join")
    fun joinToTask(@PathVariable id: TaskId) = service.joinToTask(id)



    @DeleteMapping("{id}/tags/{tagId}")
    fun deleteTaskTag(
        @PathVariable id: TaskId,
        @PathVariable tagId: TagId
    ) = service.deleteTaskTag(id, tagId)
}
