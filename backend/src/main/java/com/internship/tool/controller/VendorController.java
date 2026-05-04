import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;





@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping
    public ResponseEntity<Vendor> createVendor(@Valid @RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorService.saveVendor(vendor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendor(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    @GetMapping
    public ResponseEntity<Page<Vendor>> listVendors(Pageable pageable) {
        return ResponseEntity.ok(vendorService.getAllVendors(pageable));
    }
}
