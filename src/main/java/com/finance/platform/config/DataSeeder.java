package com.finance.platform.config;

import com.finance.platform.entity.FinancialRecord;
import com.finance.platform.entity.User;
import com.finance.platform.enums.RecordType;
import com.finance.platform.enums.Role;
import com.finance.platform.repository.FinancialRecordRepository;
import com.finance.platform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds sample users and financial records for local development.
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository,
                                   FinancialRecordRepository recordRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User("Admin User", "admin@finance.com",
                        passwordEncoder.encode("admin123"), Role.ADMIN);
                User analyst = new User("Analyst User", "analyst@finance.com",
                        passwordEncoder.encode("analyst123"), Role.ANALYST);
                User viewer = new User("Viewer User", "viewer@finance.com",
                        passwordEncoder.encode("viewer123"), Role.VIEWER);

                userRepository.save(admin);
                userRepository.save(analyst);
                userRepository.save(viewer);

                log.info("Seeded 3 users: admin, analyst, viewer");

                Long uid = admin.getId();

                seed(recordRepository, "75000.00", RecordType.INCOME, "SALARY",
                        LocalDate.of(2025, 1, 1), "January salary", uid);
                seed(recordRepository, "75000.00", RecordType.INCOME, "SALARY",
                        LocalDate.of(2025, 2, 1), "February salary", uid);
                seed(recordRepository, "75000.00", RecordType.INCOME, "SALARY",
                        LocalDate.of(2025, 3, 1), "March salary", uid);

                seed(recordRepository, "15000.00", RecordType.EXPENSE, "RENT",
                        LocalDate.of(2025, 1, 5), "January rent", uid);
                seed(recordRepository, "15000.00", RecordType.EXPENSE, "RENT",
                        LocalDate.of(2025, 2, 5), "February rent", uid);
                seed(recordRepository, "15000.00", RecordType.EXPENSE, "RENT",
                        LocalDate.of(2025, 3, 5), "March rent", uid);

                seed(recordRepository, "5000.00", RecordType.INCOME, "FREELANCE",
                        LocalDate.of(2025, 1, 15), "Consulting project A", uid);
                seed(recordRepository, "8000.00", RecordType.INCOME, "FREELANCE",
                        LocalDate.of(2025, 2, 20), "Consulting project B", uid);

                seed(recordRepository, "3500.00", RecordType.EXPENSE, "UTILITIES",
                        LocalDate.of(2025, 1, 10), "Electricity + water", uid);
                seed(recordRepository, "3200.00", RecordType.EXPENSE, "UTILITIES",
                        LocalDate.of(2025, 2, 10), "Electricity + water", uid);
                seed(recordRepository, "3800.00", RecordType.EXPENSE, "UTILITIES",
                        LocalDate.of(2025, 3, 10), "Electricity + water", uid);

                seed(recordRepository, "12000.00", RecordType.EXPENSE, "GROCERIES",
                        LocalDate.of(2025, 1, 8), "Monthly groceries", uid);
                seed(recordRepository, "11500.00", RecordType.EXPENSE, "GROCERIES",
                        LocalDate.of(2025, 2, 8), "Monthly groceries", uid);
                seed(recordRepository, "13000.00", RecordType.EXPENSE, "GROCERIES",
                        LocalDate.of(2025, 3, 8), "Monthly groceries", uid);

                seed(recordRepository, "2000.00", RecordType.INCOME, "INVESTMENT",
                        LocalDate.of(2025, 1, 25), "Dividend payout", uid);
                seed(recordRepository, "45000.00", RecordType.EXPENSE, "INSURANCE",
                        LocalDate.of(2025, 1, 15), "Annual health insurance", uid);

                seed(recordRepository, "7500.00", RecordType.EXPENSE, "TRANSPORT",
                        LocalDate.of(2025, 2, 12), "Car maintenance + fuel", uid);
                seed(recordRepository, "20000.00", RecordType.EXPENSE, "EDUCATION",
                        LocalDate.of(2025, 3, 1), "Online course subscription", uid);

                seed(recordRepository, "3000.00", RecordType.INCOME, "INVESTMENT",
                        LocalDate.of(2025, 3, 20), "Stock sale proceeds", uid);

                log.info("Seeded {} financial records", recordRepository.count());
            }
        };
    }

    private void seed(FinancialRecordRepository repo, String amount,
                      RecordType type, String category, LocalDate date,
                      String description, Long createdBy) {
        FinancialRecord r = new FinancialRecord();
        r.setAmount(new BigDecimal(amount));
        r.setType(type);
        r.setCategory(category);
        r.setDate(date);
        r.setDescription(description);
        r.setCreatedBy(createdBy);
        repo.save(r);
    }
}
