package ru.pmlite.api.values

@JvmInline
value class TeamId(val id: Long) {
  val entityId get() = EntityId(id)
}
