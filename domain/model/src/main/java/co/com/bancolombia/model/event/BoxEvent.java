package co.com.bancolombia.model.event;

import co.com.bancolombia.model.box.Box;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BoxEvent {
    private String operation;
    private String result;
    private Box box;
}