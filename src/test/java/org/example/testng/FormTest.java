package org.example.testng;

import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.pom.FormPom;
import org.example.utils.Driver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;


@Epic("DemoQA Practice Form")
@Feature("Student Registration Form")
public class FormTest {

    private static final Logger logger = LogManager.getLogger(FormTest.class);
    private WebDriver driver;

    // ── Date de test ──────────────────────────────────────────────────────────
    private static final String URL        = "https://demoqa.com/";
    private static final String FIRST_NAME = "Alexei";
    private static final String LAST_NAME  = "Suhari";
    private static final String EMAIL      = "suharia7002@mail.ru";
    private static final String GENDER     = "Male";
    private static final String SUBJECT    = "Maths";
    private static final String PHONE      = "0609750000";
    private static final String DATE       = "30 March 2007";
    private static final String HOBBY      = "Sports";
    private static final String STATE      = "Rajasthan";
    private static final String CITY       = "Jaipur";

    @BeforeMethod(alwaysRun = true)
    @Step("Setup: initializeaza browser-ul")
    public void beforeMethod() {
        logger.info("=== BEFORE METHOD: pornire driver ===");
        driver = Driver.getRemoteDriver();
        driver.manage().window().maximize();
        logger.info("Driver pornit: {}", driver.getClass().getSimpleName());
    }

    @Test(description = "Completeaza si trimite formularul de inregistrare student")
    @Story("Completare formular")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test end-to-end: navigare -> completare campuri -> submit -> verificare date in modal")
    public void studentFormTest() {
        logger.info("=== START TEST: studentFormTest ===");

        FormPom formPom = new FormPom(driver);

        step_navigateToForm(formPom);
        step_fillPersonalData(formPom);
        step_fillAdditionalData(formPom);
        step_fillLocation(formPom);
        step_submitAndVerify(formPom);

        logger.info("=== FINISH TEST: studentFormTest ===");
    }

    // ── Pasi test ─────────────────────────────────────────────────────────────

    @Step("Navigare la formular: Forms > Practice Form")
    private void step_navigateToForm(FormPom formPom) {
        logger.info("Navigare la {}", URL);
        driver.get(URL);
        formPom.clickForms();
        formPom.clickPracticeForm();
        logger.info("Formular deschis cu succes");
    }

    @Step("Completare date personale: {0} {1}")
    private void step_fillPersonalData(FormPom formPom) {
        logger.info("Completare date personale...");
        formPom.setFirstName(FIRST_NAME);
        formPom.setLastName(LAST_NAME);
        formPom.setUserEmail(EMAIL);
        formPom.setGender(GENDER);
        formPom.setPhone(PHONE);
        formPom.setDOB(DATE);
        logger.info("Date personale completate");
    }

    @Step("Completare date aditionale: materie={0}, hobby={1}")
    private void step_fillAdditionalData(FormPom formPom) {
        logger.info("Completare subject si hobby...");
        formPom.setSubject(SUBJECT);
        formPom.setHobby(HOBBY);
        logger.info("Date aditionale completate");
    }

    @Step("Completare locatie: {0}, {1}")
    private void step_fillLocation(FormPom formPom) {
        logger.info("Completare locatie: {} / {}", STATE, CITY);
        formPom.setState(STATE);
        formPom.setCity(CITY);
        logger.info("Locatie completata");
    }

    @Step("Submit formular si verificare date in modal")
    private void step_submitAndVerify(FormPom formPom) {
        logger.info("Click Submit...");
        formPom.clickSubmit();

        // Verificari cu Assert
        String actualName = formPom.getFinalData("Student Name");
        Assert.assertEquals(actualName, FIRST_NAME + " " + LAST_NAME,
                "Numele din modal nu corespunde!");
        logger.info("✅ Student Name verificat: {}", actualName);

        String actualEmail = formPom.getFinalData("Student Email");
        Assert.assertEquals(actualEmail, EMAIL,
                "Email-ul din modal nu corespunde!");
        logger.info("✅ Student Email verificat: {}", actualEmail);

        String actualGender = formPom.getFinalData("Gender");
        Assert.assertEquals(actualGender, GENDER,
                "Gender-ul din modal nu corespunde!");
        logger.info("✅ Gender verificat: {}", actualGender);

        logger.info("✅ Toate verificarile au trecut!");
    }

    @AfterMethod(alwaysRun = true)
    @Step("Teardown: inchide browser-ul")
    public void afterMethod() {
        logger.info("=== AFTER METHOD: inchidere driver ===");
        if (driver != null) {
            driver.quit();
            logger.info("Driver inchis.");
        }
    }
}
