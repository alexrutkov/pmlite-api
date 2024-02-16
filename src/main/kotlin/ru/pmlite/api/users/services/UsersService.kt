package ru.pmlite.api.users.services

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.pmlite.api.users.domain.UserShortDetails
import ru.pmlite.api.users.domain.UserTask
import ru.pmlite.api.users.repositories.UsersRepository

@Service
class UsersService(
    private val repository: UsersRepository
) {
    fun getAllUsers(pageable: Pageable): List<UserShortDetails> {
        return repository.getAllUsers(pageable)
    }

    fun getMyUsers(pageable: Pageable): List<UserShortDetails> {
        return repository.getMyUsers(pageable)
    }

    fun getUserDetails(id: Long): UserShortDetails {
        return repository.getUserDetails(id)
    }

    fun getUserTasks(id: Long, pageable: Pageable): List<UserTask> {
        return repository.getUserTasks(id, pageable)
    }
}
