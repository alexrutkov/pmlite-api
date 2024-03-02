package ru.pmlite.api.tasks.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.tasks.services.TaskDetailsService
import ru.pmlite.api.values.TaskId

@RequestMapping("/api/tasks")
@RestController
class TaskDetailsController(
  private val service: TaskDetailsService
) {
  @GetMapping("all", params = ["search"])
  fun searchAllTasks(@RequestParam search: String, pageable: Pageable) = service.searchAllTasks(search, pageable)

  @GetMapping("my", params = ["search"])
  fun searchMyTasks(@RequestParam search: String, pageable: Pageable) = service.searchMyTasks(search, pageable)
  @GetMapping("all")
  fun getAllTasks(pageable: Pageable) = service.getAllTasks(pageable)

  @GetMapping("my")
  fun getMyTasks(pageable: Pageable) = service.getMyTasks(pageable)

  @GetMapping("{id}")
  fun getTask(@PathVariable id: TaskId) = service.getTask(id)
}
