package ru.pmlite.api.tags.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.aggreements.domains.AgreementType
import ru.pmlite.api.aggreements.services.AgreementService
import ru.pmlite.api.tags.domains.Tag
import ru.pmlite.api.tags.dto.CreateTagCommand
import ru.pmlite.api.tags.dto.SearchTagCommand
import ru.pmlite.api.tags.repositories.TagsRepository

@Service
class TagsService(
    private val repository: TagsRepository,
    private val agreementService: AgreementService
) {
    @Transactional
    fun createTag(command: CreateTagCommand): Tag {
        val agreementId = agreementService.createAgreement(AgreementType.TAG)
        val tagId = repository.createTag(command, agreementId)
        return Tag(tagId, command.tag)
    }

    fun searchTags(command: SearchTagCommand): List<Tag> {
        return repository.searchTags(command.tag)
    }
}
