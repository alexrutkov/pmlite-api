package ru.pmlite.api.likes.domain

interface LikeEntity {

  val likeAmount: Long
  val starAmount: Long

  val isLiked: Boolean
  val isStared: Boolean
}
