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

