@Service
public class VendorRiskReportService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private VendorRiskService riskService;

    public Map<String, Object> generateReport() {
        List<Vendor> vendors = vendorRepository.findAll();
        int totalScore = 0;
        int highestScore = 0;
        Vendor highestRiskVendor = null;

        for (Vendor v : vendors) {
            int score = riskService.calculateRiskScore(v);
            totalScore += score;
            if (score > highestScore) {
                highestScore = score;
                highestRiskVendor = v;
            }
        }

        Map<String, Object> report = new HashMap<>();
        report.put("totalVendors", vendors.size());
        report.put("averageRisk", vendors.isEmpty() ? 0 : totalScore / vendors.size());
        report.put("highestRiskVendor", highestRiskVendor != null ? highestRiskVendor.getName() : "None");
        report.put("highestScore", highestScore);

        return report;
    }
}
