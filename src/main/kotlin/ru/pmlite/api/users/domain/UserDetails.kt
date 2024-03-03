package ru.pmlite.api.users.domain

import ru.pmlite.api.tags.domains.TagDetails

data class UserDetails(
  val details: UserShortDetails,
  val tags: List<TagDetails>
)
