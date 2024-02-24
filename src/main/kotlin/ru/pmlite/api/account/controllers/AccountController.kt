package ru.pmlite.api.account.controllers

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.pmlite.api.account.dto.AccountDetails
import ru.pmlite.api.account.dto.SaveProfileCommand
import ru.pmlite.api.account.services.AccountService
import ru.pmlite.api.users.services.UserAvatarService
import ru.pmlite.api.values.TagId
import ru.pmlite.api.values.UserId

@RequestMapping("/api/account")
@RestController
class AccountController(
    private val accountService: AccountService,
    private val userAvatarService: UserAvatarService,
) {

    @GetMapping("details")
    fun getAccountDetails(): AccountDetails = accountService.getAccountDetails()

    @GetMapping("profile")
    fun getProfileDetails() = accountService.profileDetails()

    @PostMapping("profile")
    fun saveProfile(
        @RequestBody command: SaveProfileCommand
    ) = accountService.saveProfile(command)

    @DeleteMapping("profile/tags/{tagId}")
    fun deleteTag(
        @PathVariable tagId: Long
    ) = accountService.deleteTag(TagId(tagId))

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
