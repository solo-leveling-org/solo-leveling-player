package com.sleepkqq.sololeveling.player.service.i18n

import com.sleepkqq.sololeveling.player.lozalization.LocalizationCode
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class I18nService(
	private val messageSource: MessageSource
) {

	fun getMessage(code: LocalizationCode, vararg args: Any): String = messageSource.getMessage(
		code.code,
		if (args.isEmpty()) null else args,
		LocaleContextHolder.getLocale()
	)
}
