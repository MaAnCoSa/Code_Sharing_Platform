package platform.Model;

import lombok.Data;

// This is just to receive the snippet when a new one is being posted.
@Data
public class CodeReceiver {
    private String code;    // The code snippet itself.
    private long time;      // Amount of time left by restriction (0 is no restriction).
    private long views;     // Amount of views left by restriction (0 is no restriction).
}
