package com.finance.platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FinancePlatformApiIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void viewerCanAccessDashboardButNotRecords() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                        .with(httpBasic("viewer@finance.com", "viewer123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordCount").value(19));

        mockMvc.perform(get("/api/dashboard/recent")
                        .with(httpBasic("viewer@finance.com", "viewer123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));

        mockMvc.perform(get("/api/records")
                        .with(httpBasic("viewer@finance.com", "viewer123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void analystCanReadFilteredRecordsButCannotCreateThem() throws Exception {
        mockMvc.perform(get("/api/records")
                        .param("category", "salary")
                        .param("type", "INCOME")
                        .with(httpBasic("analyst@finance.com", "analyst123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3));

        mockMvc.perform(get("/api/records")
                        .param("startDate", "2025-03-01")
                        .param("endDate", "2025-03-31")
                        .with(httpBasic("analyst@finance.com", "analyst123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(6));

        mockMvc.perform(post("/api/records")
                        .with(httpBasic("analyst@finance.com", "analyst123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 100.00,
                                  "type": "EXPENSE",
                                  "category": "SNACKS",
                                  "date": "2025-03-10",
                                  "description": "Team snacks"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidDateRangeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/records")
                        .param("startDate", "2025-04-01")
                        .param("endDate", "2025-03-01")
                        .with(httpBasic("admin@finance.com", "admin123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("endDate must be on or after startDate"));
    }

    @Test
    void invalidQueryParameterTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/records")
                        .param("type", "INVALID")
                        .with(httpBasic("analyst@finance.com", "analyst123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'INVALID' for parameter 'type'. Allowed values: INCOME, EXPENSE"));

        mockMvc.perform(get("/api/records")
                        .param("startDate", "bad-date")
                        .with(httpBasic("analyst@finance.com", "analyst123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'bad-date' for parameter 'startDate'. Use ISO date format YYYY-MM-DD"));
    }

    @Test
    void h2ConsoleRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/h2-console/")
                        .with(httpBasic("viewer@finance.com", "viewer123")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/h2-console/"))
                .andExpect(status().isUnauthorized());
    }
}
