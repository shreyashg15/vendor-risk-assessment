package com.internship.tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.internship.tool.entity.Vendor;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByName(String name);
}
