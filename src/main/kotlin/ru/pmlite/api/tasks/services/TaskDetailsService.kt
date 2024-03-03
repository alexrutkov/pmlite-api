package ru.pmlite.api.tasks.services

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.likes.services.LikesService
import ru.pmlite.api.likes.services.StarsService
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.tasks.domain.TaskDetails
import ru.pmlite.api.tasks.dto.TaskSummary
import ru.pmlite.api.tasks.repositories.SearchTaskRepository
import ru.pmlite.api.tasks.repositories.TaskRepository
import ru.pmlite.api.values.TaskId

@Service
class TaskDetailsService(
  private val securityService: SecurityService,
  private val repository: TaskRepository,
  private val searchRepository: SearchTaskRepository,
  private val likesService: LikesService,
  private val starsService: StarsService,
) {

  fun getAllTasks(pageable: Pageable): List<TaskSummary> {
    return repository.getAllTasks(securityService.userId, pageable)
      .let(::addDetails)
  }



  fun getMyTasks(pageable: Pageable): List<TaskSummary>  {
    return repository.getMyTasks(securityService.userId, pageable)
      .let(::addDetails)
  }

  fun getTask(id: TaskId): TaskDetails {
    return repository.getTask(id)
  }

  fun searchAllTasks(search: String, pageable: Pageable): List<TaskSummary> {
    return searchRepository.searchAllTasks(search, pageable)
      .let(::addDetails)
  }

  fun searchMyTasks(search: String, pageable: Pageable): List<TaskSummary> {
    return searchRepository.searchMyTasks(search, securityService.userId, pageable)
      .let(::addDetails)
  }

  private fun addDetails(tasks: List<TaskSummary>): List<TaskSummary> {
    val myLikes =  likesService.getMyLikes(tasks.map { it.id.entityId }, EntityType.TASK)
    val myStars =  starsService.getMyStars(tasks.map { it.id.entityId }, EntityType.TASK)
    return tasks.map {
      it.copy(
        isLiked = myLikes.contains(it.id.entityId),
        isStared = myStars.contains(it.id.entityId)
      )
    }
  }
}
