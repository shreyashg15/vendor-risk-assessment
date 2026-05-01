package com.internship.tool;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerTest {

    @Test
    void testContainerRunning() {

        // Skip test safely if Docker environment is unavailable
        Assumptions.assumeTrue(
                DockerClientFactory.instance().isDockerAvailable(),
                "Docker/Testcontainers environment not available, skipping test."
        );

        // Start PostgreSQL container only if Docker is available
        try (PostgreSQLContainer<?> postgres =
                     new PostgreSQLContainer<>("postgres:15")
                             .withDatabaseName("testdb")
                             .withUsername("test")
                             .withPassword("test")) {

            postgres.start();

            System.out.println("Container started successfully.");
            System.out.println("DB URL: " + postgres.getJdbcUrl());
        }
    }
}