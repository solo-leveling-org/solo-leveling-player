package com.sleepkqq.sololeveling.player.service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.Locale

@Suppress("unused")
@Configuration
class LocaleConfig {

	@Bean
	fun localeResolver(): LocaleResolver = AcceptHeaderLocaleResolver().apply {
		supportedLocales = listOf(
			Locale.ENGLISH,
			Locale.forLanguageTag("ru")
		)
		setDefaultLocale(Locale.ENGLISH)
	}
}
