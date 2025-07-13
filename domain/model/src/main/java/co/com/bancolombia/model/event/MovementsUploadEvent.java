package co.com.bancolombia.model.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MovementsUploadEvent {
    private String boxId;
    private int total;
    private int success;
    private int failed;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
}