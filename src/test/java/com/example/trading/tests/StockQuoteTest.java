package com.example.trading.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class StockQuoteTest {
    private WebDriver driver;

    @BeforeClass
    public void setUpDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        boolean headless = Boolean.parseBoolean(System.getProperty("selenium.headless", "true"));
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-plugins");
            options.addArguments("--disable-images");
        }

        // Additional Chrome options for better performance and stability
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterClass(alwaysRun = true)
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void openEt52WeekHighPage() {
        driver.get("https://economictimes.indiatimes.com/stocks/marketstats/52-week-high");
        Assert.assertTrue(
                driver.getCurrentUrl().contains("economictimes.indiatimes.com"),
                "Expected ET Markets domain in current URL"
        );

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        String companiesXpath = "//td[@class='MarketTable_fixedTD__JmGP7']//a[@class='MarketTable_ellipses__M8PxM']";
        List<WebElement> companyLinks = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(companiesXpath))
        );

        List<String> companyNames = companyLinks.stream()
                .map(e -> e.getText().trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        System.out.println("Total companies found: " + companyNames.size());
        companyNames.forEach(System.out::println);

        try {
            Path outDir = Paths.get("C:\\Users\\Administrator\\OneDrive\\Swing Tadeing");
            Files.createDirectories(outDir);
            Path outFile = outDir.resolve("companies.txt");

            String today = LocalDate.now().toString();
            String header = "=== " + today + " ===";

            // Create backup of existing file before updating
            if (Files.exists(outFile)) {
                Path backupDir = outDir.resolve("backup");
                Files.createDirectories(backupDir);
                String backupFileName = today + ".txt";
                Path backupFile = backupDir.resolve(backupFileName);
                Files.copy(outFile, backupFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backup created: " + backupFile.toAbsolutePath());
            }

            List<String> existing = Files.exists(outFile)
                    ? Files.readAllLines(outFile, StandardCharsets.UTF_8)
                    : List.of();

            // Remove any prior occurrences of today's header
            List<String> filtered = existing.stream()
                    .filter(line -> !line.equals(header))
                    .collect(Collectors.toList());

            // Remove any companies that are part of today's list from the historical section
            for (String name : companyNames) {
                filtered = filtered.stream()
                        .filter(line -> !line.equals(name))
                        .collect(Collectors.toList());
            }

            // Trim leading blank lines from filtered (for neatness)
            while (!filtered.isEmpty() && filtered.get(0).isBlank()) {
                filtered = filtered.subList(1, filtered.size());
            }

            // Build new content: today's header + names, then a blank line, then previous content
            List<String> newContent = new java.util.ArrayList<>();
            newContent.add(header);
            newContent.addAll(companyNames);
            if (!filtered.isEmpty()) {
                newContent.add("");
                newContent.addAll(filtered);
            }

            Files.write(outFile, newContent, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Updated companies file: " + outFile.toAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write companies file", e);
        }
    }
}


