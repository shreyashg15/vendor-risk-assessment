import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching   // <-- this activates Spring’s caching mechanism
public class VendorRiskAssessmentApp {
    public static void main(String[] args) {
        SpringApplication.run(VendorRiskAssessmentApp.class, args);
    }
}
