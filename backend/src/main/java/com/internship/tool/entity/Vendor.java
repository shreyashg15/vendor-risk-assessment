@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    private String contactEmail;

    @NotBlank
    private String riskLevel; // e.g., LOW, MEDIUM, HIGH

    // getters and setters
}
