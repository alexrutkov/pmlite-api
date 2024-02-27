package ru.pmlite.api.account.controllers

import org.springframework.web.bind.annotation.*
import ru.pmlite.api.account.services.AccountTagService
import ru.pmlite.api.tags.dto.TagDto

@RequestMapping("/api/account/tags")
@RestController
class AccountTagsController(
    private val service: AccountTagService
) {

    @GetMapping
    fun getAccountTags() = service.getAccountTags()

    @PostMapping
    fun addAccountTags(@RequestBody tags: List<TagDto>) = service.addAccountTags(tags)
}
