package ru.pmlite.api.users.controllers

import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.users.services.UserAvatarService
import ru.pmlite.api.users.services.UsersService
import ru.pmlite.api.values.UserId

@RestController
@RequestMapping("/api/users")
class UsersController(
    private val service: UsersService,
    private val userAvatarService: UserAvatarService,
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

    @GetMapping("{id}/avatar.jpg",
        produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getAvatar(@PathVariable id: Long) = userAvatarService.getAvatar(UserId(id))
    @RequestMapping(
        "{id}/avatar.jpg",
        method = [RequestMethod.HEAD]
    )
    fun avatarIsExist(@PathVariable id: Long): ResponseEntity<Any> {
        val entity = if (userAvatarService.isExists(UserId(id))) ResponseEntity.ok() else ResponseEntity.notFound()
        return entity.build()
    }
}
