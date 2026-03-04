package org.example.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

/**
 * Driver factory - suporta local si remote (Selenoid / GitHub Actions).
 * Parametrii se pot trece din CLI:
 *   -Duse.remote=true -Dselenoid.url=http://localhost:4444/wd/hub
 */
public class Driver {

    private static final Logger logger = LogManager.getLogger(Driver.class);

    /**
     * Returneaza driver-ul potrivit pe baza system property "use.remote".
     * Folosit in teste ca punct unic de intrare.
     */
    public static WebDriver getDriver() {
        boolean useRemote = Boolean.parseBoolean(
                System.getProperty("use.remote", "false"));
        logger.info("Driver mode: {}", useRemote ? "REMOTE (Selenoid)" : "LOCAL");
        return useRemote ? getRemoteDriver() : getAutoLocalDriver();
    }

    // ── Local (CI fara Selenoid, sau rulare locala) ────────────────────────────

    public static WebDriver getAutoLocalDriver() {
        logger.info("Starting local ChromeDriver...");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = buildLocalOptions();
        WebDriver driver = new ChromeDriver(options);
        configureTimeouts(driver);
        return driver;
    }

    public static WebDriver getLocalDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = buildLocalOptions();
        WebDriver driver = new ChromeDriver(options);
        configureTimeouts(driver);
        return driver;
    }

    // ── Remote (Selenoid) ──────────────────────────────────────────────────────

    public static WebDriver getRemoteDriver() {
        String selenoidUrl = System.getProperty("selenoid.url",
                System.getenv().getOrDefault("SELENOID_URL", "http://localhost:4444/wd/hub"));

        logger.info("Connecting to Selenoid at: {}", selenoidUrl);

        try {
            ChromeOptions options = buildRemoteOptions();
            RemoteWebDriver driver = new RemoteWebDriver(new URL(selenoidUrl), options);
            configureTimeouts(driver);
            logger.info("RemoteWebDriver session started: {}", driver.getSessionId());
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenoid URL: " + selenoidUrl, e);
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private static ChromeOptions buildLocalOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        // Headless in CI fara Selenoid
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    private static ChromeOptions buildRemoteOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");

        // Selenoid capabilities (video + VNC)
        HashMap<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVideo", true);
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableLog", true);
        selenoidOptions.put("sessionTimeout", "15m");
        selenoidOptions.put("videoName", "test-" + System.currentTimeMillis() + ".mp4");
        options.setCapability("selenoid:options", selenoidOptions);

        return options;
    }

    private static void configureTimeouts(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));
    }
}
