package ru.pmlite.api.likes.domain

interface LikeEntity {

  val likeAmount: Long
  val isLiked: Boolean
}
