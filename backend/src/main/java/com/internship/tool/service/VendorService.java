import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;



@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    // Cache GET by ID for 10 minutes (configured in RedisConfig)
    @Cacheable(value = "vendors", key = "#id", unless = "#result == null")
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id).orElse(null);
    }

    // Cache GET all vendors (paginated)
    @Cacheable(value = "vendorsAll", key = "#pageable.pageNumber", unless = "#result == null")
    public Page<Vendor> getAllVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable);
    }

    // Evict cache on create
    @CacheEvict(value = {"vendors", "vendorsAll"}, allEntries = true)
    public Vendor createVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    // Evict cache on delete
    @CacheEvict(value = {"vendors", "vendorsAll"}, allEntries = true)
    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }
}

