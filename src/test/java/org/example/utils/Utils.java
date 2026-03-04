package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static final long DEFAULT_TIMEOUT = 10;

    public static WebElement fluentWait(WebDriver driver, By by) {
        return fluentWait(driver, by, DEFAULT_TIMEOUT);
    }

    public static WebElement fluentWait(WebDriver driver, By by, long seconds) {
        logger.info("FluentWait for: {} ({}s)", by, seconds);
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);
        return wait.until((Function<WebDriver, WebElement>) dr -> {
            assert dr != null;
            return dr.findElement(by);
        });
    }

    public static <T> T explicitWait(WebDriver driver, ExpectedCondition<T> condition, long seconds) {
        logger.info("ExplicitWait ({}s)", seconds);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(condition);
    }
}
