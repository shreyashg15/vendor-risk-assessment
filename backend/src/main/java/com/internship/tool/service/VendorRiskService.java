package com.internship.tool.service;

import org.springframework.stereotype.Service;
import com.internship.tool.entity.Vendor;

@Service
public class VendorRiskService {

    public int calculateRiskScore(Vendor vendor) {
        int score = 0;

        // Example weighted factors
        if (vendor.getFinancialRating() < 50) score += 30;
        if (!vendor.isCompliant()) score += 40;
        if (vendor.getSecurityIncidents() > 5) score += 30;

        return score;
    }
}

