@Service
public class VendorRiskService {

    public int calculateRiskScore(Vendor vendor) {
        int score = 0;
        if (vendor.getComplianceRating() < 3) score += 30;
        if (vendor.getFinancialStability() < 5) score += 20;
        if (!vendor.isSecurityCertified()) score += 50;
        return score;
    }
}
