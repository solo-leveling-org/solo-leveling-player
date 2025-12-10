package com.sleepkqq.sololeveling.player.config

import com.sleepkqq.sololeveling.player.model.entity.*
import com.sleepkqq.sololeveling.player.model.entity.player.*
import com.sleepkqq.sololeveling.player.model.entity.player.dto.*
import com.sleepkqq.sololeveling.player.model.entity.task.*
import com.sleepkqq.sololeveling.player.model.entity.task.dto.*
import com.sleepkqq.sololeveling.player.model.entity.user.*
import com.sleepkqq.sololeveling.player.model.entity.user.dto.*
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString
import io.confluent.kafka.schemaregistry.client.rest.entities.Mode
import io.confluent.kafka.schemaregistry.client.rest.entities.SubjectVersion
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.*
import io.confluent.kafka.serializers.*
import io.confluent.kafka.serializers.context.NullContextNameStrategy
import io.confluent.kafka.serializers.context.strategy.ContextNameStrategy
import io.confluent.kafka.serializers.subject.*
import liquibase.analytics.AnalyticsFactory
import liquibase.analytics.configuration.AnalyticsConfigurationFactory
import liquibase.change.ChangeFactory
import liquibase.changelog.*
import liquibase.changelog.filter.ShouldRunChangeSetFilter
import liquibase.changelog.visitor.*
import liquibase.changeset.ChangeSetServiceFactory
import liquibase.command.CommandFactory
import liquibase.command.copy.ProjectCopierFactory
import liquibase.configuration.*
import liquibase.database.*
import liquibase.database.core.PostgresDatabase
import liquibase.database.jvm.JdbcConnection
import liquibase.executor.ExecutorService
import liquibase.io.OutputFileHandlerFactory
import liquibase.license.*
import liquibase.lockservice.LockServiceFactory
import liquibase.logging.LogFactory
import liquibase.logging.core.LogServiceFactory
import liquibase.logging.mdc.MdcManagerFactory
import liquibase.parser.*
import liquibase.parser.core.xml.LiquibaseEntityResolver
import liquibase.parser.core.yaml.YamlChangeLogParser
import liquibase.report.ShowSummaryGeneratorFactory
import liquibase.resource.PathHandlerFactory
import liquibase.sqlgenerator.SqlGeneratorFactory
import liquibase.statement.SqlStatement
import liquibase.structure.core.*
import liquibase.ui.*
import org.apache.kafka.common.serialization.*
import org.babyfish.jimmer.sql.dialect.PostgresDialect
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

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
		// Jimmer generated
		PlayerTaskTopicInput::class,
		PlayerTaskTopicView::class,
		PlayerTaskView::class,
		PlayerView::class,
		LevelDraft::class,
		LevelFetcher::class,
		LevelProps::class,
		LevelTable::class,
		LevelTableEx::class,
		PlayerBalanceDraft::class,
		PlayerBalanceFetcher::class,
		PlayerBalanceProps::class,
		PlayerBalanceTable::class,
		PlayerBalanceTableEx::class,
		PlayerBalanceTransactionDraft::class,
		PlayerBalanceTransactionFetcher::class,
		PlayerBalanceTransactionProps::class,
		PlayerBalanceTransactionTable::class,
		PlayerBalanceTransactionTableEx::class,
		PlayerDraft::class,
		PlayerFetcher::class,
		PlayerProps::class,
		PlayerTable::class,
		PlayerTableEx::class,
		PlayerTaskDraft::class,
		PlayerTaskFetcher::class,
		PlayerTaskProps::class,
		PlayerTaskTable::class,
		PlayerTaskTableEx::class,
		PlayerTaskTopicDraft::class,
		PlayerTaskTopicFetcher::class,
		PlayerTaskTopicProps::class,
		PlayerTaskTopicTable::class,
		PlayerTaskTopicTableEx::class,
		TaskInput::class,
		TaskDraft::class,
		TaskFetcher::class,
		TaskProps::class,
		TaskTable::class,
		TaskTableEx::class,
		UserInput::class,
		UserView::class,
		UserDraft::class,
		UserFetcher::class,
		UserProps::class,
		UserTable::class,
		UserTableEx::class,
		Fetchers::class,
		Immutables::class,
		ModelDraft::class,
		ModelProps::class,
		TableExes::class,
		Tables::class,

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
