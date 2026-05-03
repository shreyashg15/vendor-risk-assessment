@RestController
@RequestMapping("/api/vendors/risk")
public class VendorRiskReportController {

    @Autowired
    private VendorRiskReportService reportService;

    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getRiskReport() {
        return ResponseEntity.ok(reportService.generateReport());
    }
}
