package ru.pmlite.api.account.controllers

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.account.dto.AccountDetails
import ru.pmlite.api.account.dto.PasswordDto
import ru.pmlite.api.account.dto.SavePasswordCommand
import ru.pmlite.api.account.services.AccountService

@RequestMapping("/api/account")
@RestController
class AccountController(
    private val accountService: AccountService,
) {

    @GetMapping("details")
    fun getAccountDetails(): AccountDetails = accountService.getAccountDetails()



    @PostMapping("validate/password")
    fun validatePassword(
        @Valid @RequestBody dto: PasswordDto
    ) = accountService.validatePassword(dto.password)

    @PostMapping("password")
    fun savePassword(
        @Valid @RequestBody command: SavePasswordCommand
    ) = accountService.savePassword(command)

}
