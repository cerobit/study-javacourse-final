package co.com.bancolombia.api.model;

import lombok.Data;

@Data
public class UpdateBoxNameRequest {
    private String name;

    // Validadation for use case 3
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Box name cannot be null or empty");
        }
        this.name = name;
    }
}