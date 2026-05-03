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
