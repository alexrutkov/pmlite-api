package ru.pmlite.api.users.services

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.likes.services.LikesService
import ru.pmlite.api.likes.services.StarsService
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.users.domain.UserShortDetails
import ru.pmlite.api.users.domain.UserTask
import ru.pmlite.api.users.repositories.SearchUserRepository
import ru.pmlite.api.users.repositories.UsersRepository

@Service
class UsersService(
    private val repository: UsersRepository,
    private val searchRepository: SearchUserRepository,
    private val securityService: SecurityService,
    private val likesService: LikesService,
    private val starService: StarsService
) {
    fun getAllUsers(pageable: Pageable): List<UserShortDetails> {
        return repository.getAllUsers(securityService.userId, pageable)
            .let(::addDetails)
    }

    fun getMyUsers(pageable: Pageable): List<UserShortDetails> {
        return repository.getMyUsers(pageable)
            .let(::addDetails)
    }

    fun getUserDetails(id: Long): UserShortDetails {
        return repository.getUserDetails(id)
            .let { addDetails(listOf(it)) }.first()
    }

    fun getUserTasks(id: Long, pageable: Pageable): List<UserTask> {
        return repository.getUserTasks(id, pageable)
    }

    fun searchMyUsers(search: String, pageable: Pageable): List<UserShortDetails> {
        return searchRepository.searchMyUsers(search, pageable).let(::addDetails)
    }

    fun searchAllUsers(search: String, pageable: Pageable): List<UserShortDetails> {
        return searchRepository.searchAllUsers(search, pageable).let(::addDetails)
    }

    private fun addDetails(users: List<UserShortDetails>): List<UserShortDetails> {
        val myLikes = likesService.getMyLikes(users.map(UserShortDetails::entityId), EntityType.USER)
        val myStars = starService.getMyStars(users.map(UserShortDetails::entityId), EntityType.USER)
        return users.map {
            it.copy(
                isLiked = myLikes.contains(it.entityId),
                isStared = myStars.contains(it.entityId)
            )
        }
    }
}
