package com.sleepkqq.sololeveling.player.service.mapper

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule

object EnumOrdinalMapper {

	fun <E : Enum<E>> createMapper(enumType: Class<E>): ObjectMapper {
		val mapper = ObjectMapper()
		val module = SimpleModule()

		module.addSerializer(
			enumType,
			object : JsonSerializer<Enum<E>>() {
				override fun serialize(
					value: Enum<E>,
					gen: JsonGenerator,
					provider: SerializerProvider
				) = gen.writeNumber(value.ordinal)
			}
		)

		module.addDeserializer(
			enumType,
			object : JsonDeserializer<E>() {
				override fun deserialize(p: JsonParser, ctx: DeserializationContext): E =
					enumType.enumConstants[p.intValue]
			}
		)

		mapper.registerModule(module)
		return mapper
	}
}
