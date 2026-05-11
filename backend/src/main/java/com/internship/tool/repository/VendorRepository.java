package com.internship.tool.repository;

import com.internship.tool.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    
    Optional<Vendor> findByVendorName(String vendorName);
    
    // Custom query for search, filter by status
    @Query("SELECT v FROM Vendor v WHERE v.deleted = false AND " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:q IS NULL OR LOWER(v.vendorName) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(v.contactPerson) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(v.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Vendor> searchVendors(@Param("q") String q, @Param("status") String status, Pageable pageable);
    
}
