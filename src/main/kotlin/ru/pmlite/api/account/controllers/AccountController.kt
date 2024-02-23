package ru.pmlite.api.account.controllers

import org.springframework.web.bind.annotation.*
import ru.pmlite.api.account.dto.AccountDetails
import ru.pmlite.api.account.dto.SaveProfileCommand
import ru.pmlite.api.account.services.AccountService
import ru.pmlite.api.values.TagId

@RequestMapping("/api/account")
@RestController
class AccountController(
    private val accountService: AccountService
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


}
