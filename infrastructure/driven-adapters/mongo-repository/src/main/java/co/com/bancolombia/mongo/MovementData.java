package co.com.bancolombia.mongo;

import co.com.bancolombia.model.movement.MovementType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Document("Movements")
public class MovementData {
   @Id
   private String id;
   private String movementId;
   private String boxId;
   private LocalDateTime date;
   private MovementType type; // INCOME o EXPENSE
   private BigDecimal amount;
   private String currency;
   private String description;
}
