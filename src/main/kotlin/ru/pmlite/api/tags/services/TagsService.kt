package ru.pmlite.api.tags.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.services.AgreementService
import ru.pmlite.api.tags.domains.TagDetails
import ru.pmlite.api.tags.dto.CreateTagCommand
import ru.pmlite.api.tags.dto.SearchTagCommand
import ru.pmlite.api.tags.repositories.TagsRepository

@Service
class TagsService(
    private val repository: TagsRepository,
    private val agreementService: AgreementService
) {
    @Transactional
    fun createTag(command: CreateTagCommand): TagDetails {
        val agreementId = agreementService.createAgreement(AgreementType.TAG)
        val tagId = repository.createTag(command, agreementId)
        return TagDetails(tagId, command.tag, AgreementState.PENDING)
    }

    fun searchTags(command: SearchTagCommand): List<TagDetails> {
        return repository.searchTags(command.tag)
    }
}
