package com.sleepkqq.sololeveling.player.model.entity.player

import com.sleepkqq.sololeveling.player.model.entity.Model
import com.sleepkqq.sololeveling.player.model.entity.player.enums.CurrencyCode
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.JoinColumn
import org.babyfish.jimmer.sql.KeyUniqueConstraint
import org.babyfish.jimmer.sql.OneToMany
import org.babyfish.jimmer.sql.OneToOne
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "player_balances")
@KeyUniqueConstraint
interface PlayerBalance : Model {

	@Id
	@GeneratedValue(generatorType = UUIDIdGenerator::class)
	val id: UUID

	val balance: BigDecimal

	val currencyCode: CurrencyCode

	@OneToOne
	@JoinColumn(name = "player_id")
	val player: Player

	@OneToMany(mappedBy = "balance")
  val transactions: List<PlayerBalanceTransaction>
}
