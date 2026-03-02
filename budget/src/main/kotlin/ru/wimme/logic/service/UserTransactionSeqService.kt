package ru.wimme.logic.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.wimme.logic.model.entity.UserDisplayIdSeqEntity
import ru.wimme.logic.repository.UserDisplayIdSeqRepository

@Service
class UserTransactionSeqService(
    private val userTransactionSeqRepository: UserDisplayIdSeqRepository
) {

    @Transactional
    fun getNextSeq(userId: String): Long {
        val seq: UserDisplayIdSeqEntity = userTransactionSeqRepository.findByIdOrNull(userId)
            ?: UserDisplayIdSeqEntity(userId = userId, currentSeq = 0)

        seq.currentSeq += 1
        userTransactionSeqRepository.save(seq)

        return userTransactionSeqRepository.save(seq).currentSeq
    }
}