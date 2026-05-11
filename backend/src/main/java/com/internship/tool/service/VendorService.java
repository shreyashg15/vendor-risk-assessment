package com.internship.tool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.Vendor;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private AuditLogService auditLogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Page<Vendor> searchVendors(String q, String status, Pageable pageable) {
        return vendorRepository.searchVendors(q, status, pageable);
    }

    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .filter(v -> !v.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id " + id));
    }

    public Vendor createVendor(Vendor vendor) {
        Vendor saved = vendorRepository.save(vendor);
        logAudit(saved.getId(), "CREATE", null, saved);
        return saved;
    }

    public Vendor updateVendor(Long id, Vendor vendorDetails) {
        Vendor vendor = getVendorById(id);
        String oldVal = toJson(vendor);

        vendor.setVendorName(vendorDetails.getVendorName());
        vendor.setContactPerson(vendorDetails.getContactPerson());
        vendor.setEmail(vendorDetails.getEmail());
        vendor.setPhone(vendorDetails.getPhone());
        vendor.setRiskScore(vendorDetails.getRiskScore());
        vendor.setStatus(vendorDetails.getStatus());
        vendor.setDescription(vendorDetails.getDescription());
        vendor.setReviewDate(vendorDetails.getReviewDate());

        Vendor updated = vendorRepository.save(vendor);
        logAudit(updated.getId(), "UPDATE", oldVal, updated);
        return updated;
    }

    public void deleteVendor(Long id) {
        Vendor vendor = getVendorById(id);
        String oldVal = toJson(vendor);
        vendor.setDeleted(true);
        vendorRepository.save(vendor);
        logAudit(vendor.getId(), "DELETE", oldVal, vendor);
    }

    private void logAudit(Long id, String action, String oldVal, Vendor newVal) {
        String username = "system";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        auditLogService.logAction("Vendor", id, action, username, oldVal, toJson(newVal));
    }

    private String toJson(Object obj) {
        try {
            // Registering JavaTimeModule would be proper here, but catching is enough for MVP
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
