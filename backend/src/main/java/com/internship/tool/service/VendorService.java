package com.internship.tool.service;

import com.internship.tool.entity.Vendor;
import com.internship.tool.exception.ValidationException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.VendorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    // ==========================================
    // CREATE
    // ==========================================
    public Vendor createVendor(Vendor vendor) {
        validateVendor(vendor);
        return vendorRepository.save(vendor);
    }

    // ==========================================
    // GET ALL (Pagination)
    // ==========================================
    public Page<Vendor> getAllVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable);
    }

    // ==========================================
    // GET BY ID
    // ==========================================
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
    }

    // ==========================================
    // UPDATE
    // ==========================================
    public Vendor updateVendor(Long id, Vendor updatedVendor) {

        Vendor existing = getVendorById(id);
        validateVendor(updatedVendor);

        existing.setVendorName(updatedVendor.getVendorName());
        existing.setEmail(updatedVendor.getEmail());
        existing.setPhone(updatedVendor.getPhone());
        existing.setStatus(updatedVendor.getStatus());
        existing.setRiskScore(updatedVendor.getRiskScore());

        return vendorRepository.save(existing);
    }

    // ==========================================
    // SOFT DELETE
    // ==========================================
    public void softDeleteVendor(Long id) {

        Vendor vendor = getVendorById(id);
        vendor.setDeleted(true);

        vendorRepository.save(vendor);
    }

    // ==========================================
    // SEARCH
    // ==========================================
    public List<Vendor> searchVendor(String keyword) {

        return vendorRepository.searchVendors(
                keyword,
                Pageable.unpaged()
        ).getContent();
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    public Map<String, Object> getDashboardStats() {

        long total = vendorRepository.count();
        long active = vendorRepository.findByDeletedFalse().size();

        return Map.of(
                "totalVendors", total,
                "activeVendors", active
        );
    }

    // ==========================================
    // EXPORT CSV
    // ==========================================
    public String exportVendorsToCsv() {

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Vendor Name,Email,Status,Risk Score\n");

        vendorRepository.findAll().forEach(vendor -> {
            csv.append(vendor.getId()).append(",");
            csv.append(vendor.getVendorName()).append(",");
            csv.append(vendor.getEmail()).append(",");
            csv.append(vendor.getStatus()).append(",");
            csv.append(vendor.getRiskScore()).append("\n");
        });

        return csv.toString();
    }

    // ==========================================
    // SCHEDULER METHODS
    // ==========================================
    public void processOverdueVendors() {
        System.out.println("Processing overdue vendors...");
    }

    public void processUpcomingDeadlines() {
        System.out.println("Processing upcoming deadlines...");
    }

    public void generateWeeklySummary() {
        System.out.println("Generating weekly summary...");
    }

    // ==========================================
    // VALIDATION
    // ==========================================
    private void validateVendor(Vendor vendor) {

        if (vendor == null) {
            throw new ValidationException("Vendor object cannot be null");
        }

        if (vendor.getVendorName() == null || vendor.getVendorName().trim().isEmpty()) {
            throw new ValidationException("Vendor name is required");
        }

        if (vendor.getEmail() == null || !vendor.getEmail().contains("@")) {
            throw new ValidationException("Valid email is required");
        }

        if (vendor.getPhone() != null && vendor.getPhone().length() < 10) {
            throw new ValidationException("Phone number must be at least 10 digits");
        }
    }
}