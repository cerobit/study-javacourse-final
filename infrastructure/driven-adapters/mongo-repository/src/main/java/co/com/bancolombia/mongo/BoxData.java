package co.com.bancolombia.mongo;

import co.com.bancolombia.model.boxstatus.BoxStatus;
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
@Document("Boxes")
public class BoxData {

    @Id
    private String id;
    private String name;
    private BoxStatus status;
    private BigDecimal openingAmount;
    private BigDecimal closingAmount;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal currentBalance;

}