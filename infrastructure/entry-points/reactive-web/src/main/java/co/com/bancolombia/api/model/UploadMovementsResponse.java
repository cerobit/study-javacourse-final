package co.com.bancolombia.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadMovementsResponse {
        private String boxId;
        private int total;
        private int success;
        private int failed;
        private LocalDateTime uploadedAt;
}


