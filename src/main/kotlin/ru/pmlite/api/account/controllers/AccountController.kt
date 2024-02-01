package ru.pmlite.api.account.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.account.dto.AccountDetails
import ru.pmlite.api.account.services.AccountService
import ru.pmlite.api.values.UserId

@RequestMapping("/api/account")
@RestController
class AccountController(
    private val accountService: AccountService
) {

    @GetMapping("details")
    fun getAccountDetails(@AuthenticationPrincipal userId: UserId): AccountDetails = accountService.getAccountDetails(userId)


}
