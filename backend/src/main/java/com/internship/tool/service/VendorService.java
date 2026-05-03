package com.internship.tool.service;

import com.internship.tool.entity.Vendor;
import com.internship.tool.exception.ValidationException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.VendorRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    // ==========================================
    // CREATE (evict caches)
    // ==========================================
    @CacheEvict(value = {"vendors", "vendorsAll"}, allEntries = true)
    public Vendor createVendor(Vendor vendor) {
        validateVendor(vendor);
        return vendorRepository.save(vendor);
    }

    // ==========================================
    // GET ALL (cached by page)
    // ==========================================
    @Cacheable(value = "vendorsAll", key = "#pageable.pageNumber", unless = "#result == null")
    public Page<Vendor> getAllVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable);
    }

    // ==========================================
    // GET BY ID (cached)
    // ==========================================
    @Cacheable(value = "vendors", key = "#id", unless = "#result == null")
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
    }

    // ==========================================
    // UPDATE (evict cache for this id + list)
    // ==========================================
    @CacheEvict(value = {"vendors", "vendorsAll"}, allEntries = true)
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
    // SOFT DELETE (evict caches)
    // ==========================================
    @CacheEvict(value = {"vendors", "vendorsAll"}, allEntries = true)
    public void softDeleteVendor(Long id) {
        Vendor vendor = getVendorById(id);
        vendor.setDeleted(true);
        vendorRepository.save(vendor);
    }

    // ==========================================
    // SEARCH (no cache)
    // ==========================================
    public List<Vendor> searchVendor(String keyword) {
        return vendorRepository.searchVendors(keyword, Pageable.unpaged()).getContent();
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