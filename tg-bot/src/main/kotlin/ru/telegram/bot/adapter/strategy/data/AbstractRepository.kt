package ru.telegram.bot.adapter.strategy.data

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

@Repository
abstract class AbstractRepository<T: DataModel> {

    protected lateinit var dslContext: DSLContext

    abstract fun getData(chatId: Long): T

    fun isAvailableForCurrentStep(stepCode: StepCode): Boolean {
        return this.currentStepCode("Repository") == stepCode
    }
}