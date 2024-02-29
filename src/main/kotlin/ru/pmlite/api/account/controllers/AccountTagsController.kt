package ru.pmlite.api.account.controllers

import org.springframework.web.bind.annotation.*
import ru.pmlite.api.account.domains.AccountTag
import ru.pmlite.api.account.dto.UpdateActiveCommand
import ru.pmlite.api.account.services.AccountTagService
import ru.pmlite.api.tags.dto.TagDto
import ru.pmlite.api.values.AccountTagId

@RequestMapping("/api/account/tags")
@RestController
class AccountTagsController(
    private val service: AccountTagService
) {

    @GetMapping
    fun getAccountTags(): List<AccountTag> = service.getAccountTags()

    @PostMapping
    fun addAccountTags(@RequestBody tags: List<TagDto>) = service.addAccountTags(tags)

    @DeleteMapping("{id}")
    fun deleteAccountTag(@PathVariable id: Long) = service.deleteAccountTag(AccountTagId(id))

    @PatchMapping("{id}")
    fun updateActivity(
        @PathVariable id: Long,
        @RequestBody command: UpdateActiveCommand
    ) = service.updateActivity(AccountTagId(id), command)
}
