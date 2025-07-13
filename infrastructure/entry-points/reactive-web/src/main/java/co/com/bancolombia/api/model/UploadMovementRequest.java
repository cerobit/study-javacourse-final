package co.com.bancolombia.api.model;

import co.com.bancolombia.model.movement.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadMovementRequest {
    private String movementId;
    @NotBlank
    private String boxId;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private MovementType type; // INCOME or EXPENSE
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be positive")
    private BigDecimal amount;
    @NotBlank
    private String currency;
    private String description;
    // Getters and setters or @Data from Lombok
}
