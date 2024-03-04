package ru.pmlite.api.teams.dto

import ru.pmlite.api.tags.domains.TagDetails

data class TeamDetails(
  val summary: TeamSummary,
  val tags: List<TagDetails>
)
