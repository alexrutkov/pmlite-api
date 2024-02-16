package ru.pmlite.api.users.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.users.services.UsersService

@RestController
@RequestMapping("/api/users")
class UsersController(
    private val service: UsersService
) {

    @GetMapping("all")
    fun getAllUsers(pageable: Pageable) = service.getAllUsers(pageable)

    @GetMapping("my")
    fun getMyUsers(pageable: Pageable) = service.getMyUsers(pageable)

    @GetMapping("{id}")
    fun getUserDetails(@PathVariable id: Long) = service.getUserDetails(id)

    @GetMapping("{id}/tasks")
    fun getUserTasks(
        @PathVariable id: Long,
        pageable: Pageable
    ) = service.getUserTasks(id, pageable)

}
