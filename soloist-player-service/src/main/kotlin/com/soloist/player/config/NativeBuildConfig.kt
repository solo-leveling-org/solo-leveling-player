package com.soloist.player.config

import com.soloist.player.model.entity.Fetchers
import com.soloist.player.model.entity.Immutables
import com.soloist.player.model.entity.ModelDraft
import com.soloist.player.model.entity.ModelProps
import com.soloist.player.model.entity.TableExes
import com.soloist.player.model.entity.Tables
import com.soloist.player.model.entity.player.LevelDraft
import com.soloist.player.model.entity.player.LevelFetcher
import com.soloist.player.model.entity.player.LevelProps
import com.soloist.player.model.entity.player.LevelTable
import com.soloist.player.model.entity.player.LevelTableEx
import com.soloist.player.model.entity.player.PlayerBalanceDraft
import com.soloist.player.model.entity.player.PlayerBalanceFetcher
import com.soloist.player.model.entity.player.PlayerBalanceProps
import com.soloist.player.model.entity.player.PlayerBalanceTable
import com.soloist.player.model.entity.player.PlayerBalanceTableEx
import com.soloist.player.model.entity.player.PlayerBalanceTransactionDraft
import com.soloist.player.model.entity.player.PlayerBalanceTransactionFetcher
import com.soloist.player.model.entity.player.PlayerBalanceTransactionProps
import com.soloist.player.model.entity.player.PlayerBalanceTransactionTable
import com.soloist.player.model.entity.player.PlayerBalanceTransactionTableEx
import com.soloist.player.model.entity.player.PlayerDraft
import com.soloist.player.model.entity.player.PlayerFetcher
import com.soloist.player.model.entity.player.PlayerProps
import com.soloist.player.model.entity.player.PlayerTable
import com.soloist.player.model.entity.player.PlayerTableEx
import com.soloist.player.model.entity.player.PlayerTaskDraft
import com.soloist.player.model.entity.player.PlayerTaskFetcher
import com.soloist.player.model.entity.player.PlayerTaskProps
import com.soloist.player.model.entity.player.PlayerTaskTable
import com.soloist.player.model.entity.player.PlayerTaskTableEx
import com.soloist.player.model.entity.player.PlayerTaskTopicDraft
import com.soloist.player.model.entity.player.PlayerTaskTopicFetcher
import com.soloist.player.model.entity.player.PlayerTaskTopicProps
import com.soloist.player.model.entity.player.PlayerTaskTopicTable
import com.soloist.player.model.entity.player.PlayerTaskTopicTableEx
import com.soloist.player.model.entity.player.dto.PlayerTaskTopicInput
import com.soloist.player.model.entity.player.dto.PlayerTaskTopicView
import com.soloist.player.model.entity.player.dto.PlayerTaskView
import com.soloist.player.model.entity.player.dto.PlayerView
import com.soloist.player.model.entity.task.TaskDraft
import com.soloist.player.model.entity.task.TaskFetcher
import com.soloist.player.model.entity.task.TaskProps
import com.soloist.player.model.entity.task.TaskTable
import com.soloist.player.model.entity.task.TaskTableEx
import com.soloist.player.model.entity.user.UserDraft
import com.soloist.player.model.entity.user.UserFetcher
import com.soloist.player.model.entity.user.UserProps
import com.soloist.player.model.entity.user.UserTable
import com.soloist.player.model.entity.user.UserTableEx
import com.soloist.player.model.entity.user.dto.UserInput
import com.soloist.player.model.entity.user.dto.UserView
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
