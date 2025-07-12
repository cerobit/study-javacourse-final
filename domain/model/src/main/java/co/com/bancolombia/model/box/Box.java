package co.com.bancolombia.model.box;
import co.com.bancolombia.model.boxstatus.BoxStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Box {
    private String id;                // ID de la caja

    private String name;              // Nombre o número de caja

    private BoxStatus status;         // Estado: OPENED, CLOSED
    private BigDecimal openingAmount; // Monto de apertura
    private BigDecimal closingAmount; // Monto de cierre
    private LocalDateTime openedAt;   // Fecha y hora de apertura
    private LocalDateTime closedAt;   // Fecha y hora de cierre
    private BigDecimal currentBalance;// Saldo actual de la caja

    // Métodos de negocio (sin cambios)
    public void open(BigDecimal amount) {
        if (status == BoxStatus.OPENED) {
            throw new IllegalStateException("La caja ya está abierta");
        }
        this.openingAmount = amount;
        this.currentBalance = amount;
        this.status = BoxStatus.OPENED;
        this.openedAt = LocalDateTime.now();
    }

    public void close(BigDecimal closingAmount) {
        if (status != BoxStatus.OPENED) {
            throw new IllegalStateException("La caja no está abierta");
        }
        this.closingAmount = closingAmount;
        this.status = BoxStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    public void addAmount(BigDecimal amount) {
        if (status != BoxStatus.OPENED) {
            throw new IllegalStateException("La caja debe estar abierta para registrar movimientos");
        }
        this.currentBalance = this.currentBalance.add(amount);
    }

    public void subtractAmount(BigDecimal amount) {
        if (status != BoxStatus.OPENED) {
            throw new IllegalStateException("La caja debe estar abierta para registrar movimientos");
        }
        if (this.currentBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        this.currentBalance = this.currentBalance.subtract(amount);
    }

}
