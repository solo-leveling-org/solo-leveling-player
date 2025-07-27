package com.sleepkqq.sololeveling.player.model.entity.player

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.JoinColumn
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "player_balance_transactions")
interface PlayerBalanceTransaction : Model {

	@Id
	@GeneratedValue(generatorType = UUIDIdGenerator::class)
	val id: UUID

	val amount: BigDecimal

	val type: PlayerBalanceTransactionType

	val cause: PlayerBalanceTransactionCause

	@ManyToOne
	@JoinColumn(name = "balance_id")
	val balance: PlayerBalance
}
