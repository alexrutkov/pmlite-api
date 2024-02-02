package ru.pmlite.api.tasks.controllers

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.tasks.dto.CreateRootTaskCommand
import ru.pmlite.api.tasks.dto.CreateTaskCommand
import ru.pmlite.api.tasks.services.TaskService


@PreAuthorize("hasRole('TASK_CREATOR')")
@RestController
@RequestMapping("/api/tasks")
class CreatorsTaskController(
    private val service: TaskService
) {

    @PostMapping("createRootTask")
    fun createRootTask(
        @Valid @RequestBody command: CreateRootTaskCommand
    ) {
        service.createRootTask(CreateTaskCommand(command.name, command.shortDescription))
    }
}
