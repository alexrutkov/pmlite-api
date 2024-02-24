package ru.pmlite.api.users.services

import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.pmlite.api.users.exceptions.AvatarNotFoundException
import ru.pmlite.api.values.UserId
import java.nio.file.Path
import kotlin.io.path.exists

const val AVATAR_DIRECTORY = "avatars"
@Service
class UserAvatarService(
    globalPath: Path
) {
    private val defaultPath = globalPath.resolve(AVATAR_DIRECTORY)
        .apply { toFile().mkdirs() }
    fun saveAvatar(userId: UserId, file: MultipartFile) {
        val avatarPath = defaultPath.resolve(userId.id.toString())
        file.transferTo(avatarPath)
    }

    fun getAvatar(userId: UserId): FileSystemResource {
        val avatarPath = defaultPath.resolve(userId.id.toString())
        return if (avatarPath.exists()) {
            FileSystemResource(avatarPath)
        } else throw AvatarNotFoundException()
    }

    fun isExists(userId: UserId): Boolean {
        val avatarPath = defaultPath.resolve(userId.id.toString())
        return avatarPath.exists()
    }
}
