import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;






@RestController
@RequestMapping("/api/vendors")
public class VendorRiskController {

    @Autowired
    private VendorRiskService riskService;

    @Autowired
    private VendorRepository vendorRepository;

    @GetMapping("/{id}/risk")
    public ResponseEntity<Integer> getVendorRisk(@PathVariable Long id) {
        Vendor vendor = vendorRepository.findById(id).orElseThrow();
        int score = riskService.calculateRiskScore(vendor);
        return ResponseEntity.ok(score);
    }
}

