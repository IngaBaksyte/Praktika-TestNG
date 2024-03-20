package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Calculator {
    public static WebDriver browser;
    public static String operationId;


    public static void main(String[] args) {
        System.out.println("EGZAMINAS. Selenium + TestNG");

//        Login newUser = new Login("Test03", "Test03");
//        Operation operation = new Operation(8, 6, '*');
//        Operation operationEdited = new Operation(8, 6, '+', 14);
//        Operation operationTest = new Operation(10, 1, '+');

//        setup();

//        System.out.println(createNewAccount(newUser));
//        logout();
//        System.out.println(login(newUser));
//        System.out.println(createOperation(operation));
//        operationId = searchOperation(operation);
//        System.out.println(operationId);
//  NEREIKIA      waitClickable("rodyti", operationId);
//        System.out.println(actionWithOperation(operationId, "rodyti"));
//        System.out.println(actionWithOperation(operationId, "atnaujinti"));
//        System.out.println(editOperation(operationEdited));
//        System.out.println(actionWithOperation(operationId, "trinti"));
//        String testId = searchOperation(operationTest);
//        System.out.println(testId);

//        close();

    }

    public static void setup(){
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
//        chromeOptions.addArguments("--ignore-certificate-errors");
        browser = new ChromeDriver(chromeOptions);
        browser.get("http://localhost:8080/");
    }

    public static void waitClickable(String action, String id) {
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/" + action + "?id=" + id + "')]")));
    }

    public static void logout() {
        browser.findElement(By.partialLinkText("Logout,")).click();
    }

    public static void close(){
        browser.close();
    }

    public static String createNewAccount(Login info) {
        browser.findElement(By.xpath("//a[text()='Sukurti naują paskyrą']")).click();
        browser.findElement(By.id("username")).sendKeys(info.getName());
        browser.findElement(By.id("password")).sendKeys(info.getPassword());
        browser.findElement(By.id("passwordConfirm")).sendKeys(info.getPassword());
        browser.findElement(By.xpath("//button[text()='Sukurti']")).click();
        return browser.getCurrentUrl();
        // http://localhost:8080/skaiciuotuvas {teig} arba http://localhost:8080/registruoti (neig)
    }

    public static String login(Login info) {
        browser.get("http://localhost:8080/prisijungti");
        browser.findElement(By.name("username")).sendKeys(info.getName());
        browser.findElement(By.name("password")).sendKeys(info.getPassword());
        browser.findElement(By.xpath("//button[text()='Prisijungti']")).click();
        return browser.getCurrentUrl();
        // http://localhost:8080/ (teig) http://localhost:8080/prisijungti?error (neig)
    }

    public static String createOperation(Operation operation) {
        browser.findElement(By.linkText("Skaičiuotuvas")).click();
        WebElement num1 = browser.findElement(By.id("sk1"));
        num1.clear();
        num1.sendKeys(Integer.toString(operation.getFirstNum()));
        WebElement num2 = browser.findElement(By.id("sk2"));
        num2.clear();
        num2.sendKeys(Integer.toString(operation.getSecondNum()));
        Select select = new Select(browser.findElement(By.name("zenklas")));
        select.selectByValue(Character.toString(operation.getOperation()));
        try {
            browser.findElement(By.xpath("//*[@id=\"number\"]/input[3]")).click();
            return browser.findElement(By.tagName("h4")).getText();
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    public static String searchOperation(Operation operation) {
        browser.findElement(By.linkText("Atliktos operacijos")).click();
        try {
            WebElement link = browser.findElement(By.xpath("//tr[td[1]='" + operation.getFirstNum() + "' and td[2]='" + operation.getOperation() + "' and td[3]='" + operation.getSecondNum() + "']/td/a[text()='Rodyti']"));
            String href = link.getAttribute("href");
            String[] parts = href.split("=");
            //        int id = Integer.parseInt(parts[1]);
            return parts[1];
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public static String actionWithOperation(String id, String action) {
        browser.findElement(By.linkText("Atliktos operacijos")).click();
        try {
            WebElement showLink = browser.findElement(By.xpath("//a[contains(@href, '/" + action + "?id=" + id + "')]"));
            ((JavascriptExecutor) browser).executeScript("arguments[0].scrollIntoView(true);", showLink);
            showLink.click();
            try {
                Alert alert = browser.switchTo().alert();
                alert.accept();
            } catch (NoAlertPresentException e) {
                return browser.getCurrentUrl();
            }
        } catch (NoSuchElementException e) {
            return browser.getCurrentUrl();
        }
        return browser.getCurrentUrl();
    }

    public static boolean editOperation(Operation editedOperation) {
        WebElement num1 = browser.findElement(By.name("sk1"));
        num1.clear();
        num1.sendKeys(Integer.toString(editedOperation.getFirstNum()));
        WebElement operator = browser.findElement(By.name("zenklas"));
        operator.clear();
        operator.sendKeys(Character.toString(editedOperation.getOperation()));
        WebElement num2 = browser.findElement(By.name("sk2"));
        num2.clear();
        num2.sendKeys(Integer.toString(editedOperation.getSecondNum()));
        WebElement result = browser.findElement(By.name("rezult"));
        result.clear();
        result.sendKeys(Integer.toString(editedOperation.getResult()));
        try {
            browser.findElement(By.xpath("//input[@value='Atnaujinti']")).click();
            return (browser.findElement(By.xpath("//tr[2]/td[2]")).getText().equals(Integer.toString(editedOperation.getFirstNum())) |
                    browser.findElement(By.xpath("//tr[3]/td[2]")).getText().equals(Character.toString(editedOperation.getOperation())) |
                    browser.findElement(By.xpath("//tr[4]/td[2]")).getText().equals(Integer.toString(editedOperation.getSecondNum())) |
                    browser.findElement(By.xpath("//tr[5]/td[2]")).getText().equals(Integer.toString(editedOperation.getResult())));
        } catch (NoSuchElementException e) {
            browser.navigate().back();
            return false;
        }
    }

}