package com.sleepkqq.sololeveling.player.event

import com.sleepkqq.sololeveling.avro.notification.NotificationPriority
import com.sleepkqq.sololeveling.avro.notification.NotificationSource
import com.sleepkqq.sololeveling.avro.notification.NotificationType
import com.sleepkqq.sololeveling.avro.notification.SendNotificationEvent
import com.sleepkqq.sololeveling.player.event.model.LocaleUpdatedEvent
import com.sleepkqq.sololeveling.player.event.model.TasksSavedEvent
import com.sleepkqq.sololeveling.player.event.model.TasksSilentUpdatedEvent
import com.sleepkqq.sololeveling.player.kafka.producer.SendNotificationProducer
import com.sleepkqq.sololeveling.player.lozalization.LocalizationCode
import com.sleepkqq.sololeveling.player.service.i18n.I18nService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.util.concurrent.CompletableFuture

class NotificationListenerTest {

	@Mock
	private lateinit var i18nService: I18nService

	@Mock
	private lateinit var sendNotificationProducer: SendNotificationProducer

	private lateinit var notificationListener: NotificationListener

	@BeforeEach
	fun setUp() {
		MockitoAnnotations.openMocks(this)
		notificationListener = NotificationListener(i18nService, sendNotificationProducer)
	}

	@Test
	fun `should send notification for tasks saved event`() {
		// Arrange
		val userId = 1L
		val txId = "tx-123"
		val event = TasksSavedEvent(userId, txId)
		val expectedMessage = "Tasks generated successfully"

		whenever(i18nService.getMessage(LocalizationCode.TASKS_GENERATION_SUCCESS))
			.thenReturn(expectedMessage)
		whenever(sendNotificationProducer.send(any()))
			.thenReturn(CompletableFuture.completedFuture(null))

		// Act
		notificationListener.listen(event)

		// Assert
		val captor = argumentCaptor<SendNotificationEvent>()
		verify(sendNotificationProducer, times(1)).send(captor.capture())
		
		val sentEvent = captor.firstValue
		assert(sentEvent.userId == userId)
		assert(sentEvent.txId == txId)
		assert(sentEvent.notification.message == expectedMessage)
		assert(sentEvent.notification.type == NotificationType.INFO)
		assert(sentEvent.notification.source == NotificationSource.TASKS)
	}

	@Test
	fun `should handle notification with null localization code`() {
		// Arrange
		val userId = 2L
		val event = TasksSilentUpdatedEvent(userId)

		whenever(sendNotificationProducer.send(any()))
			.thenReturn(CompletableFuture.completedFuture(null))

		// Act
		notificationListener.listen(event)

		// Assert
		val captor = argumentCaptor<SendNotificationEvent>()
		verify(sendNotificationProducer, times(1)).send(captor.capture())
		
		val sentEvent = captor.firstValue
		assert(sentEvent.userId == userId)
		assert(sentEvent.notification.message == null)
		assert(sentEvent.notification.source == NotificationSource.TASKS)
	}

	@Test
	fun `should set correct notification priority from context`() {
		// Arrange
		val userId = 3L
		val txId = "tx-456"
		val event = TasksSavedEvent(userId, txId)
		val expectedMessage = "Tasks generated"

		whenever(i18nService.getMessage(LocalizationCode.TASKS_GENERATION_SUCCESS))
			.thenReturn(expectedMessage)
		whenever(sendNotificationProducer.send(any()))
			.thenReturn(CompletableFuture.completedFuture(null))

		// Act
		notificationListener.listen(event)

		// Assert
		val captor = argumentCaptor<SendNotificationEvent>()
		verify(sendNotificationProducer, times(1)).send(captor.capture())
		
		val sentEvent = captor.firstValue
		assert(sentEvent.priority == NotificationPriority.LOW)
	}

	@Test
	fun `should handle locale updated event correctly`() {
		// Arrange
		val userId = 4L
		val event = LocaleUpdatedEvent(userId)

		whenever(sendNotificationProducer.send(any()))
			.thenReturn(CompletableFuture.completedFuture(null))

		// Act
		notificationListener.listen(event)

		// Assert
		val captor = argumentCaptor<SendNotificationEvent>()
		verify(sendNotificationProducer, times(1)).send(captor.capture())
		
		val sentEvent = captor.firstValue
		assert(sentEvent.userId == userId)
		assert(sentEvent.notification.source == NotificationSource.LOCALE)
		assert(sentEvent.notification.message == null)
	}

	@Test
	fun `should log error when notification sending fails`() {
		// Arrange
		val userId = 5L
		val event = TasksSavedEvent(userId)
		val expectedMessage = "Tasks generated"

		whenever(i18nService.getMessage(LocalizationCode.TASKS_GENERATION_SUCCESS))
			.thenReturn(expectedMessage)
		whenever(sendNotificationProducer.send(any()))
			.thenThrow(RuntimeException("Kafka connection failed"))

		// Act
		notificationListener.listen(event)

		// Assert
		// Verify that send was attempted
		verify(sendNotificationProducer, times(1)).send(any())
		// No exception should be thrown - it should be caught and logged
	}

	@Test
	fun `should handle i18n service failure gracefully`() {
		// Arrange
		val userId = 6L
		val event = TasksSavedEvent(userId)

		whenever(i18nService.getMessage(LocalizationCode.TASKS_GENERATION_SUCCESS))
			.thenThrow(RuntimeException("I18n service error"))

		// Act
		notificationListener.listen(event)

		// Assert
		// No exception should be thrown - it should be caught and logged
		verify(sendNotificationProducer, never()).send(any())
	}

	@Test
	fun `should preserve transaction id from event`() {
		// Arrange
		val userId = 7L
		val txId = "tx-789-custom"
		val event = TasksSavedEvent(userId, txId)
		val expectedMessage = "Tasks generated"

		whenever(i18nService.getMessage(LocalizationCode.TASKS_GENERATION_SUCCESS))
			.thenReturn(expectedMessage)
		whenever(sendNotificationProducer.send(any()))
			.thenReturn(CompletableFuture.completedFuture(null))

		// Act
		notificationListener.listen(event)

		// Assert
		val captor = argumentCaptor<SendNotificationEvent>()
		verify(sendNotificationProducer, times(1)).send(captor.capture())
		
		val sentEvent = captor.firstValue
		assert(sentEvent.txId == txId)
	}

	@Test
	fun `should handle multiple sequential events`() {
		// Arrange
		val userId1 = 8L
		val userId2 = 9L
		val event1 = TasksSavedEvent(userId1)
		val event2 = LocaleUpdatedEvent(userId2)
		val expectedMessage = "Tasks generated"

		whenever(i18nService.getMessage(LocalizationCode.TASKS_GENERATION_SUCCESS))
			.thenReturn(expectedMessage)
		whenever(sendNotificationProducer.send(any()))
			.thenReturn(CompletableFuture.completedFuture(null))

		// Act
		notificationListener.listen(event1)
		notificationListener.listen(event2)

		// Assert
		verify(sendNotificationProducer, times(2)).send(any())
	}

	@Test
	fun `should set visible flag correctly from context`() {
		// Arrange
		val userId = 10L
		val event = TasksSavedEvent(userId)
		val expectedMessage = "Tasks generated"

		whenever(i18nService.getMessage(LocalizationCode.TASKS_GENERATION_SUCCESS))
			.thenReturn(expectedMessage)
		whenever(sendNotificationProducer.send(any()))
			.thenReturn(CompletableFuture.completedFuture(null))

		// Act
		notificationListener.listen(event)

		// Assert
		val captor = argumentCaptor<SendNotificationEvent>()
		verify(sendNotificationProducer, times(1)).send(captor.capture())
		
		val sentEvent = captor.firstValue
		assert(sentEvent.notification.visible == true)
	}

	@Test
	fun `should handle notification with visible flag set to false`() {
		// Arrange
		val userId = 11L
		val event = TasksSilentUpdatedEvent(userId)

		whenever(sendNotificationProducer.send(any()))
			.thenReturn(CompletableFuture.completedFuture(null))

		// Act
		notificationListener.listen(event)

		// Assert
		val captor = argumentCaptor<SendNotificationEvent>()
		verify(sendNotificationProducer, times(1)).send(captor.capture())
		
		val sentEvent = captor.firstValue
		assert(sentEvent.notification.visible == false)
	}
}
