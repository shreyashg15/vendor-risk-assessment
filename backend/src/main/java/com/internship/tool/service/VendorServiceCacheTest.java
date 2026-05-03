@SpringBootTest
class VendorServiceCacheTest {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorRepository vendorRepository;

    @Test
    void testCacheHitVsMiss() {
        Vendor vendor = new Vendor();
        vendor.setName("Cached Vendor");
        vendorRepository.save(vendor);

        long start = System.nanoTime();
        vendorService.getVendorById(vendor.getId()); // cache miss
        long missTime = System.nanoTime() - start;

        start = System.nanoTime();
        vendorService.getVendorById(vendor.getId()); // cache hit
        long hitTime = System.nanoTime() - start;

        System.out.println("Cache miss time: " + missTime);
        System.out.println("Cache hit time: " + hitTime);

        assertTrue(hitTime < missTime, "Cache hit should be faster than miss");
    }
}
