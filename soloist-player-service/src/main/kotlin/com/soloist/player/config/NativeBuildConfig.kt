package com.soloist.player.config

import com.soloist.player.model.entity.*
import com.soloist.player.model.entity.balance.*
import com.soloist.player.model.entity.player.*
import com.soloist.player.model.entity.task.*
import com.soloist.player.model.entity.user.*
import com.soloist.player.model.entity.user.dto.UserInput
import com.soloist.player.model.entity.user.dto.UserView
import liquibase.analytics.AnalyticsFactory
import liquibase.analytics.configuration.AnalyticsConfigurationFactory
import liquibase.change.ChangeFactory
import liquibase.changelog.*
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
import org.babyfish.jimmer.sql.dialect.PostgresDialect
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

@RegisterReflectionForBinding(
	classes = [
		// Jimmer
		PostgresDialect::class,
		// Jimmer generated — player
		PlayerDraft::class,
		PlayerFetcher::class,
		PlayerProps::class,
		PlayerTable::class,
		PlayerTableEx::class,
		StaminaDraft::class,
		StaminaFetcher::class,
		StaminaProps::class,
		StaminaTable::class,
		StaminaTableEx::class,
		DayStreakDraft::class,
		DayStreakFetcher::class,
		DayStreakProps::class,
		DayStreakTable::class,
		DayStreakTableEx::class,
		DayActivityDraft::class,
		DayActivityFetcher::class,
		DayActivityProps::class,
		DayActivityTable::class,
		DayActivityTableEx::class,
		// Jimmer generated — task
		TaskDraft::class,
		TaskFetcher::class,
		TaskProps::class,
		TaskTable::class,
		TaskTableEx::class,
		// Jimmer generated — balance
		BalanceDraft::class,
		BalanceFetcher::class,
		BalanceProps::class,
		BalanceTable::class,
		BalanceTableEx::class,
		BalanceTransactionDraft::class,
		BalanceTransactionFetcher::class,
		BalanceTransactionProps::class,
		BalanceTransactionTable::class,
		BalanceTransactionTableEx::class,
		// Jimmer generated — user
		UserInput::class,
		UserView::class,
		UserDraft::class,
		UserFetcher::class,
		UserProps::class,
		UserTable::class,
		UserTableEx::class,
		// Jimmer root
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
