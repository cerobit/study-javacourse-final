package co.com.bancolombia.model.transaction;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transaction {
    private String id;
    private String boxId;
    private TransactionType type; // INCOME, EXPENSE
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;

}
