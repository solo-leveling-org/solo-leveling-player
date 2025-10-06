package com.sleepkqq.sololeveling.player.service.config

import io.grpc.*
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.annotation.Order
import org.springframework.grpc.server.GlobalServerInterceptor
import org.springframework.stereotype.Component
import java.util.Locale

@Component
@Order(100)
@GlobalServerInterceptor
class LocaleGrpcInterceptor : ServerInterceptor {

	private companion object {
		const val LOCALE_METADATA_KEY = "x-locale"
		val SUPPORTED_LOCALES = setOf("ru", "en")
	}

	override fun <ReqT, RespT> interceptCall(
		call: ServerCall<ReqT, RespT>,
		headers: Metadata,
		next: ServerCallHandler<ReqT, RespT>
	): ServerCall.Listener<ReqT> {
		val locale = extractLocaleFromMetadata(headers)
		LocaleContextHolder.setLocale(locale)

		return object : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
			next.startCall(call, headers)
		) {
			override fun onComplete() {
				try {
					super.onComplete()
				} finally {
					LocaleContextHolder.resetLocaleContext()
				}
			}

			override fun onCancel() {
				try {
					super.onCancel()
				} finally {
					LocaleContextHolder.resetLocaleContext()
				}
			}
		}
	}

	private fun extractLocaleFromMetadata(metadata: Metadata): Locale {
		val customLocale = metadata.get(
			Metadata.Key.of(LOCALE_METADATA_KEY, Metadata.ASCII_STRING_MARSHALLER)
		)
		if (!customLocale.isNullOrBlank() && SUPPORTED_LOCALES.contains(customLocale)) {
			return Locale.forLanguageTag(customLocale)
		}

		return Locale.ENGLISH
	}
}
