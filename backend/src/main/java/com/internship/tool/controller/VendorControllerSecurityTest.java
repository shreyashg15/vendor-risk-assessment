@SpringBootTest
@AutoConfigureMockMvc
class VendorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/vendors"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="apoorva", roles={"ADMIN"})
    void testAuthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/vendors"))
               .andExpect(status().isOk());
    }
}
