package ru.wimmly.logic.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.wimmly.logic.model.entity.UserEntity

@Repository
interface UserRepository: JpaRepository<UserEntity, String> {
}