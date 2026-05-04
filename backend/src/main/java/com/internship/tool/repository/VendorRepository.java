public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByName(String name);
}
