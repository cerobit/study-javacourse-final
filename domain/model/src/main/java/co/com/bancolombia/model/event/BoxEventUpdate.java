package co.com.bancolombia.model.event;

import co.com.bancolombia.model.box.Box;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BoxEventUpdate {
    private String previousName;
    private String newName;
    private Timestamp  updatedAt;
}
