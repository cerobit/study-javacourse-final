package co.com.bancolombia.model.movement;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class Movement {
   private String movementId;
   private String boxId;
   private LocalDateTime date;
   private MovementType type; // INCOME o EXPENSE
   private BigDecimal amount;
   private String currency;
   private String description;

}


