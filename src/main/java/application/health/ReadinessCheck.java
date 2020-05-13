package application.health;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Endpoint(id = "readiness")
@Component
public class ReadinessCheck {

    @ReadOperation
    public String testReadiness() {
        return "{\"status\":\"UP\"}";
    }
}
