package ru.wimme.logic.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.wimme.logic.model.entity.UserDisplayIdSeqEntity

@Repository
interface UserDisplayIdSeqRepository : JpaRepository<UserDisplayIdSeqEntity, String>