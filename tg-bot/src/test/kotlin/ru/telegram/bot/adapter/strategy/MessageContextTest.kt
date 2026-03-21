package ru.telegram.bot.adapter.strategy

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formReplyMarkupDto
import ru.telegram.bot.adapter.strategy.data.common.AbstractRepository
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.message.AbstractSendPhoto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@ExtendWith(MockitoExtension::class)
class MessageContextTest {
    @Mock
    lateinit var sendMessages: Map<StepCode, AbstractSendMessage<DataModel>>

    @Mock
    lateinit var sendPhotos: Map<StepCode, AbstractSendPhoto<DataModel>>

    @Mock
    lateinit var sendMessage: AbstractSendMessage<DataModel>

    @Mock
    lateinit var sendPhoto: AbstractSendPhoto<DataModel>

    lateinit var repository: AbstractRepository<DataModel>

    private lateinit var messageContext: MessageContext<DataModel>

    private val chatId = CHAT_ID
    private val stepCode = StepCode.HELP
    private val messageText = "Test message"
    private val inlineButtons = listOf(MarkupDataDto(rowPos = 0, text = "Button"))
    private val replyButtons = listOf(formReplyMarkupDto())
    private val file = "photo.jpg".byteInputStream()
    private val dataModel = mock<DataModel>()

    @BeforeEach
    fun setup() {
        repository = mock() as AbstractRepository<DataModel>
        messageContext = MessageContext(sendMessages, sendPhotos, listOf(repository))
    }

    @Test
    fun `getMessage should return MessageModelDto when sendMessage exists and permitted`() {
        whenever(sendMessages[stepCode]).thenReturn(sendMessage)
        whenever(sendMessage.isPermitted(chatId)).thenReturn(true)
        whenever(sendMessage.message(any())).thenReturn(messageText)
        whenever(sendMessage.inlineButtons(eq(chatId), any())).thenReturn(inlineButtons)
        whenever(sendMessage.replyButtons(eq(chatId), any())).thenReturn(replyButtons)
        whenever(repository.isAvailableForCurrentStep(stepCode)).thenReturn(true)
        whenever(repository.getData(chatId)).thenReturn(dataModel)

        val result = messageContext.getMessage(chatId, stepCode)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(messageText, result?.message) },
            { assertEquals(inlineButtons, result?.inlineButtons) },
            { assertEquals(replyButtons, result?.replyButtons) },
            { assertNull(result?.file) },
            { verify(sendMessages)[stepCode] },
            { verify(sendMessage).isPermitted(chatId) },
            { verify(sendMessage).message(dataModel) },
            { verify(sendMessage).inlineButtons(chatId, dataModel) },
            { verify(sendMessage).replyButtons(chatId, dataModel) }
        )
    }

    @Test
    fun `getMessage should return null when sendMessage does not exist`() {
        whenever(sendMessages[stepCode]).thenReturn(null)

        val result = messageContext.getMessage(chatId, stepCode)

        assertNull(result)
        verify(sendMessages)[stepCode]
        verify(sendMessage, never()).isPermitted(any())
    }

    @Test
    fun `getMessage should return null when sendMessage is not permitted`() {
        whenever(sendMessages[stepCode]).thenReturn(sendMessage)
        whenever(sendMessage.isPermitted(chatId)).thenReturn(false)

        val result = messageContext.getMessage(chatId, stepCode)

        assertNull(result)
        verify(sendMessage).isPermitted(chatId)
        verify(sendMessage, never()).message(any())
    }

    @Test
    fun `getMessage should throw NPE when repository not found`() {
        whenever(sendMessages[stepCode]).thenReturn(sendMessage)
        whenever(sendMessage.isPermitted(chatId)).thenReturn(true)

        assertThrows<NullPointerException> { messageContext.getMessage(chatId, stepCode) }
    }

    @Test
    fun `getPhotoMessage should return MessageModelDto with file when sendPhoto exists and permitted`() {
        whenever(sendPhotos[stepCode]).thenReturn(sendPhoto)
        whenever(sendPhoto.isPermitted(chatId)).thenReturn(true)
        whenever(sendPhoto.message(any())).thenReturn(messageText)
        whenever(sendPhoto.inlineButtons(eq(chatId), any())).thenReturn(inlineButtons)
        whenever(sendPhoto.file(any())).thenReturn(file)
        whenever(repository.isAvailableForCurrentStep(stepCode)).thenReturn(true)
        whenever(repository.getData(chatId)).thenReturn(dataModel)

        val result = messageContext.getPhotoMessage(chatId, stepCode)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(messageText, result?.message) },
            { assertEquals(inlineButtons, result?.inlineButtons) },
            { assertEquals(file, result?.file) },
            { assertTrue(result?.replyButtons?.isEmpty() ?: false) },
            { verify(sendPhotos)[stepCode] },
            { verify(sendPhoto).isPermitted(chatId) },
            { verify(sendPhoto).message(dataModel) },
            { verify(sendPhoto).inlineButtons(chatId, dataModel) },
            { verify(sendPhoto).file(dataModel) }
        )
    }

    @Test
    fun `getPhotoMessage should return null when sendPhoto does not exist`() {
        whenever(sendPhotos[stepCode]).thenReturn(null)

        val result = messageContext.getPhotoMessage(chatId, stepCode)

        assertNull(result)
        verify(sendPhotos).get(stepCode)
        verify(sendPhoto, never()).isPermitted(any())
    }

    @Test
    fun `getPhotoMessage should return null when sendPhoto is not permitted`() {
        whenever(sendPhotos[stepCode]).thenReturn(sendPhoto)
        whenever(sendPhoto.isPermitted(chatId)).thenReturn(false)

        val result = messageContext.getPhotoMessage(chatId, stepCode)

        assertAll(
            { assertNull(result) },
            { verify(sendPhoto).isPermitted(chatId) },
            { verify(sendPhoto, never()).message(any()) },
        )
    }

    @Test
    fun `getPhotoMessage should return MessageModelDto when repository not found`() {
        whenever(sendPhotos[stepCode]).thenReturn(sendPhoto)
        whenever(sendPhoto.isPermitted(chatId)).thenReturn(true)

        assertThrows<NullPointerException> { messageContext.getPhotoMessage(chatId, stepCode) }
    }
}