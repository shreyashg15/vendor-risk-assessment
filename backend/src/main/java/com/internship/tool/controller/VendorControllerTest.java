@SpringBootTest
@AutoConfigureMockMvc
class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateVendor() throws Exception {
        String vendorJson = "{\"name\":\"TestVendor\",\"contactEmail\":\"test@vendor.com\",\"riskLevel\":\"LOW\"}";

        mockMvc.perform(post("/api/vendors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(vendorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestVendor"));
    }
}
