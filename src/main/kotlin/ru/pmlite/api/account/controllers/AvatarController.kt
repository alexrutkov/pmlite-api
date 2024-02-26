package ru.pmlite.api.account.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.pmlite.api.users.services.UserAvatarService
import ru.pmlite.api.values.UserId

@RequestMapping("/api/account")
@RestController
class AvatarController(
    private val userAvatarService: UserAvatarService,
) {
    @PostMapping("avatar")
    fun saveAvatar(
        @AuthenticationPrincipal userId: UserId,
        @RequestParam file: MultipartFile
    ) = userAvatarService.saveAvatar(userId, file)

    @GetMapping("avatar.jpg",
        produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getAvatar(@AuthenticationPrincipal userId: UserId) = userAvatarService.getAvatar(userId)

    @RequestMapping(
        "avatar.jpg",
        method = [RequestMethod.HEAD]
    )
    fun avatarIsExist(@AuthenticationPrincipal userId: UserId): ResponseEntity<Any> {
        val entity = if (userAvatarService.isExists(userId)) ResponseEntity.ok() else ResponseEntity.notFound()
        return entity.build()
    }
}
