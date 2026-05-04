package com.internship.tool.controller;

import com.internship.tool.entity.Vendor;
import com.internship.tool.repository.VendorRepository;
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
class VendorRiskReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VendorRepository vendorRepository;

    @Test
    void testGetRiskReportApi() throws Exception {
        // Add sample vendors
        Vendor v1 = new Vendor();
        v1.setName("Vendor A");
        v1.setComplianceRating(2);
        v1.setFinancialStability(4);
        v1.setSecurityCertified(false);
        vendorRepository.save(v1);

        Vendor v2 = new Vendor();
        v2.setName("Vendor B");
        v2.setComplianceRating(5);
        v2.setFinancialStability(7);
        v2.setSecurityCertified(true);
        vendorRepository.save(v2);

        // Call the report API
        mockMvc.perform(get("/api/vendors/risk/report")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVendors").value(2))
                .andExpect(jsonPath("$.averageRisk").exists())
                .andExpect(jsonPath("$.highestRiskVendor").value("Vendor A"))
                .andExpect(jsonPath("$.highestScore").exists());
    }
}
