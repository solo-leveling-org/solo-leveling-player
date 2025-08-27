package com.sleepkqq.sololeveling.player.model.entity.player;

import com.sleepkqq.sololeveling.player.model.entity.Model;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionCause;
import com.sleepkqq.sololeveling.player.model.entity.player.enums.PlayerBalanceTransactionType;
import java.math.BigDecimal;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.KeyUniqueConstraint;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.Table;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
@Table(name = "player_balance_transactions")
@KeyUniqueConstraint
public interface PlayerBalanceTransaction extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  BigDecimal amount();

  PlayerBalanceTransactionType type();

  PlayerBalanceTransactionCause cause();

  @ManyToOne
  @JoinColumn(name = "balance_id")
  PlayerBalance balance();
}
