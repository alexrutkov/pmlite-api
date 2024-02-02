package ru.pmlite.api.tasks.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class TaskNotFoundExceptions : RuntimeException("Задача не найдена!") {
}
