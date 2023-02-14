package platform.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Code { // implements Comparable<Code> {
    @Id
    @JsonIgnore
    private String id;              // Actually a UUID, but parsed to String.

    private String code;            // The code snippet itself.

    @JsonIgnore
    @UpdateTimestamp
    LocalDateTime dateNoFormat;     // Date in LocalDateTime format.

    private String date;            // Date in String format (to compare).

    @JsonIgnore
    private boolean restrictedView; // To signal restriction by no. of views.

    @JsonIgnore
    private boolean restrictedTime; // To signal restriction by limited time.

    private long time;              // Time left to delete restricted snippet.

    private long views;             // Views left to delete restricted snippet.
}
