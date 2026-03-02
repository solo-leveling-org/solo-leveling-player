package com.soloist.player.model.entity.balance;

import com.soloist.player.model.entity.Model;
import com.soloist.player.model.entity.player.Player;
import com.soloist.player.model.entity.player.enums.CurrencyCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OneToOne;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
public interface Balance extends Model {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  BigDecimal amount();

  CurrencyCode currencyCode();

  @OneToOne
  @JoinColumn(name = "player_id")
  Player player();

  @OneToMany(mappedBy = "balance")
  List<BalanceTransaction> transactions();
}
