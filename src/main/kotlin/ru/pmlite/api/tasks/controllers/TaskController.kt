package ru.pmlite.api.tasks.controllers

import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.security.validators.DtoValidator
import ru.pmlite.api.tasks.dto.CreateTaskCommand
import ru.pmlite.api.tasks.services.TaskService

private val logger = KotlinLogging.logger {}
@RequestMapping("/api/tasks")
@RestController
class TaskController(
    private val service: TaskService,
    private val dtoValidator: DtoValidator
) {

    @PostMapping
    fun createTask(
        @Valid @RequestBody command: CreateTaskCommand,
        result: BindingResult
    ) {
        dtoValidator.validate(result)
        service.createTask(command)
    }
}
