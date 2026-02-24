package com.soloist.player.service.i18n

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class I18nService(
	private val messageSource: MessageSource
) {

	fun getMessage(code: String, args: List<Any> = emptyList()): String =
		messageSource.getMessage(
			code,
			args.toTypedArray().takeIf { it.isNotEmpty() },
			LocaleContextHolder.getLocale()
		)
}
