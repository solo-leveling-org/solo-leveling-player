package com.sleepkqq.sololeveling.player.service.config

import io.confluent.kafka.schemaregistry.client.rest.entities.Mode
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString
import io.confluent.kafka.schemaregistry.client.rest.entities.SubjectVersion
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.*
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.context.NullContextNameStrategy
import io.confluent.kafka.serializers.context.strategy.ContextNameStrategy
import io.confluent.kafka.serializers.subject.RecordNameStrategy
import io.confluent.kafka.serializers.subject.TopicNameStrategy
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

@Suppress("unused")
@RegisterReflectionForBinding(
	classes = [
		// Avro
		KafkaAvroDeserializer::class,
		KafkaAvroSerializer::class,
		RecordNameStrategy::class,
		TopicNameStrategy::class,
		TopicRecordNameStrategy::class,
		NullContextNameStrategy::class,
		ContextNameStrategy::class,
		StringDeserializer::class,
		StringSerializer::class,
		ByteArrayDeserializer::class,
		Schema::class,
		SchemaString::class,
		SubjectVersion::class,
		RegisterSchemaRequest::class,
		RegisterSchemaResponse::class,
		ConfigUpdateRequest::class,
		ModeUpdateRequest::class,
		CompatibilityCheckResponse::class,
		Mode::class,
		Function1::class
	]
)
@Configuration
class NativeBuildConfig
