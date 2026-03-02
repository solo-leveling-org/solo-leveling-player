package com.soloist.player.model.entity.balance;

import com.soloist.player.model.entity.Auditable;
import com.soloist.player.model.entity.player.enums.CurrencyCode;
import com.soloist.player.model.entity.balance.enums.BalanceTransactionCause;
import com.soloist.player.model.entity.balance.enums.BalanceTransactionType;
import java.math.BigDecimal;
import java.util.UUID;
import org.babyfish.jimmer.sql.DissociateAction;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.JoinColumn;
import org.babyfish.jimmer.sql.ManyToOne;
import org.babyfish.jimmer.sql.OnDissociate;
import org.babyfish.jimmer.sql.meta.UUIDIdGenerator;

@Entity
public interface BalanceTransaction extends Auditable {

  @Id
  @GeneratedValue(generatorType = UUIDIdGenerator.class)
  UUID id();

  BigDecimal amount();

  CurrencyCode currencyCode();

  BalanceTransactionType type();

  BalanceTransactionCause cause();

  @ManyToOne
  @OnDissociate(DissociateAction.DELETE)
  @JoinColumn(name = "balance_id")
  Balance balance();

  String CAUSE_FIELD = "cause";
  String TYPE_FIELD = "type";
  String AMOUNT_FIELD = "amount";
}
