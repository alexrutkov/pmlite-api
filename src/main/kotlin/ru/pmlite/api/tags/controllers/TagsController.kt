package ru.pmlite.api.tags.controllers

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.tags.dto.CreateTagCommand
import ru.pmlite.api.tags.dto.SearchTagCommand
import ru.pmlite.api.tags.services.TagsService

@RestController
@RequestMapping("/api/tags")
class TagsController(
    private val service: TagsService
) {

    @PostMapping
    fun createTag(
        @Valid @RequestBody command: CreateTagCommand
    ) = service.createTag(command)

    @PostMapping("search")
    fun searchTags(
        @Valid @RequestBody command: SearchTagCommand
    ) = service.searchTags(command)
}
