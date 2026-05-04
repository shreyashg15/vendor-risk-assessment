package com.internship.tool.controller;

import com.internship.tool.entity.Vendor;
import com.internship.tool.repository.VendorRepository;
import com.internship.tool.service.VendorRiskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VendorRiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private VendorRiskService riskService;

    @Test
    void testRiskScoreCalculation() {
        Vendor vendor = new Vendor();
        vendor.setComplianceRating(2);
        vendor.setFinancialStability(4);
        vendor.setSecurityCertified(false);

        int score = riskService.calculateRiskScore(vendor);
        // Expected: 30 + 20 + 50 = 100
        assert(score == 100);
    }

    @Test
    void testGetVendorRiskApi() throws Exception {
        Vendor vendor = new Vendor();
        vendor.setName("Test Vendor");
        vendor.setComplianceRating(5);
        vendor.setFinancialStability(7);
        vendor.setSecurityCertified(true);
        vendor = vendorRepository.save(vendor);

        mockMvc.perform(get("/api/vendors/" + vendor.getId() + "/risk")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("0")); // No penalties, score = 0
    }
}

