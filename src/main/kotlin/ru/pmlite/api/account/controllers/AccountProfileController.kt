package ru.pmlite.api.account.controllers

import org.springframework.web.bind.annotation.*
import ru.pmlite.api.account.dto.SaveProfileCommand
import ru.pmlite.api.account.services.AccountService
import ru.pmlite.api.values.TagId
@RequestMapping("/api/account/profile")
@RestController
class AccountProfileController(
    private val accountService: AccountService,
) {

    @GetMapping
    fun getProfileDetails() = accountService.profileDetails()

    @PostMapping
    fun saveProfile(
        @RequestBody command: SaveProfileCommand
    ) = accountService.saveProfile(command)

    @DeleteMapping("tags/{tagId}")
    fun deleteTag(
        @PathVariable tagId: Long
    ) = accountService.deleteTag(TagId(tagId))
}
