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
import liquibase.analytics.AnalyticsFactory
import liquibase.analytics.configuration.AnalyticsConfigurationFactory
import liquibase.change.ChangeFactory
import liquibase.changelog.ChangeLogHistoryServiceFactory
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.ChangeSet
import liquibase.changelog.DatabaseChangeLog
import liquibase.changelog.FastCheckService
import liquibase.changelog.filter.ShouldRunChangeSetFilter
import liquibase.changelog.visitor.ChangeExecListener
import liquibase.changelog.visitor.UpdateVisitor
import liquibase.changelog.visitor.ValidatingVisitorGeneratorFactory
import liquibase.changeset.ChangeSetServiceFactory
import liquibase.command.CommandFactory
import liquibase.command.copy.ProjectCopierFactory
import liquibase.configuration.ConfiguredValueModifierFactory
import liquibase.configuration.LiquibaseConfiguration
import liquibase.database.DatabaseFactory
import liquibase.database.LiquibaseTableNamesFactory
import liquibase.database.core.PostgresDatabase
import liquibase.database.jvm.JdbcConnection
import liquibase.executor.ExecutorService
import liquibase.io.OutputFileHandlerFactory
import liquibase.license.LicenseServiceFactory
import liquibase.license.LicenseTrackingFactory
import liquibase.lockservice.LockServiceFactory
import liquibase.logging.LogFactory
import liquibase.logging.core.LogServiceFactory
import liquibase.logging.mdc.MdcManagerFactory
import liquibase.parser.ChangeLogParserFactory
import liquibase.parser.SqlParserFactory
import liquibase.parser.core.xml.LiquibaseEntityResolver
import liquibase.parser.core.yaml.YamlChangeLogParser
import liquibase.report.ShowSummaryGeneratorFactory
import liquibase.resource.PathHandlerFactory
import liquibase.sqlgenerator.SqlGeneratorFactory
import liquibase.statement.SqlStatement
import liquibase.structure.core.Column
import liquibase.structure.core.Table
import liquibase.ui.LoggerUIService
import liquibase.ui.UIServiceFactory
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.babyfish.jimmer.sql.dialect.PostgresDialect
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

		// Jimmer
		PostgresDialect::class,

		// Liquibase
		LoggerUIService::class,
		LiquibaseTableNamesFactory::class,
		ChangeLogParserFactory::class,
		DatabaseFactory::class,
		ChangeExecListener::class,
		PostgresDatabase::class,
		JdbcConnection::class,
		LiquibaseEntityResolver::class,
		YamlChangeLogParser::class,
		ChangeSet::class,
		DatabaseChangeLog::class,
		ChangeLogParameters::class,
		ExecutorService::class,
		LockServiceFactory::class,
		UpdateVisitor::class,
		ShouldRunChangeSetFilter::class,
		SqlGeneratorFactory::class,
		SqlStatement::class,
		liquibase.structure.core.Schema::class,
		Table::class,
		Column::class,
		ValidatingVisitorGeneratorFactory::class,
		FastCheckService::class,
		ShowSummaryGeneratorFactory::class,
		AnalyticsConfigurationFactory::class,
		AnalyticsFactory::class,
		ChangeFactory::class,
		ChangeLogHistoryServiceFactory::class,
		ChangeSetServiceFactory::class,
		CommandFactory::class,
		ConfiguredValueModifierFactory::class,
		FastCheckService::class,
		LicenseServiceFactory::class,
		LicenseTrackingFactory::class,
		LiquibaseConfiguration::class,
		LiquibaseTableNamesFactory::class,
		LogFactory::class,
		LogServiceFactory::class,
		MdcManagerFactory::class,
		OutputFileHandlerFactory::class,
		PathHandlerFactory::class,
		ProjectCopierFactory::class,
		SqlParserFactory::class,
		UIServiceFactory::class
	]
)
@Configuration
class NativeBuildConfig
