package com.aura;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.Assert.assertEquals;

public class SampleSauceTest {
//Environment variable for user and Sauce_accesskey
    public String sauce_username = System.getenv("SAUCE_USERNAME");
    public String sauce_accesskey = System.getenv("SAUCE_ACCESS_KEY");

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();
    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{

//                // Windows
                new Object[]{"browser","chrome", "latest", "Windows 10",""},
                new Object[]{"browser","MicrosoftEdge", "latest", "Windows 10",""},
                new Object[]{"browser","firefox", "latest-2", "Windows 10",""},
                new Object[]{"browser","internet explorer", "11", "Windows 8.1",""},
                new Object[]{"browser","firefox", "55.0", "Windows 7",""},

                // Mac
                new Object[]{"browser","firefox", "latest", "macOS 10.14",""},
                new Object[]{"browser","safari", "latest", "macOS 10.13",""},
                new Object[]{"browser","safari", "11.0", "macOS 10.12",""},
                new Object[]{"browser","chrome", "76.0", "OS X 10.11",""},

                //Devices
                new Object[]{"device","", "", "Android","Samsung.*"},
                new Object[]{"device","", "", "iOS", "iPhone.*"},
        };
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the  instance.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    private WebDriver createDriver(String environment, String browser, String version, String os, String device, String methodName) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("username", sauce_username);
        capabilities.setCapability("accesskey", sauce_accesskey);
        String jobName = methodName;
        capabilities.setCapability("name", jobName);

        if (environment == "browser") {
            capabilities.setCapability("browserName", browser);
            capabilities.setCapability("version", version);
            capabilities.setCapability("platform", os);
            capabilities.setCapability("extendedDebugging", true);
            capabilities.setCapability("capturePerformance", true);
        } else {
            capabilities.setCapability("platformName", os);
            capabilities.setCapability("deviceName", device);
        }

        //US
        //Creates Selenium Driver
        webDriver.set(new RemoteWebDriver(
                new URL("https://ondemand.us-west-1.saucelabs.com:443/wd/hub"),
                capabilities));
        // EU
        // webDriver.set(new RemoteWebDriver(
        //         new URL("https://ondemand.eu-central-1.saucelabs.com/wd/hub"),
        //         capabilities));

        //Keeps track of the unique Selenium session ID used to identify jobs on Sauce Labs
       String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);

        //For CI plugins
        String message = String.format("SauceOnDemandSessionID=%1$s job-name=%2$s", id, jobName);
        System.out.println(message);

        return webDriver.get();
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws Exception {
        boolean status = result.isSuccess();
        ((JavascriptExecutor)webDriver.get()).executeScript("sauce:job-result="+ status);
        webDriver.get().quit();
    }

    /**
     * Runs a simple test verifying the title of the wikipedia.org home page.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @throws Exception if an error occurs during the running of the test
     */

    @Test(dataProvider = "hardCodedBrowsers")
    public void AuraPageTitle(String type, String browser, String version, String os, String device, Method method) throws Exception {

        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.zedaerospace.com/");
        assertEquals(driver.getTitle(), "ZED");
    }
    @Test(dataProvider = "hardCodedBrowsers")
    public void AuraPageAircraft(String type, String browser, String version, String os, String device, Method method) throws Exception {

        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.zedaerospace.com/");
        driver.findElement(By.linkText("THE AIRCRAFT")).click();
    }
    @Test(dataProvider = "hardCodedBrowsers")
    public void AuraPageAirports(String type, String browser, String version, String os, String device, Method method) throws Exception {

        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.zedaerospace.com/");
        driver.findElement(By.linkText("THE AIRPORT")).click();
    }
    @Test(dataProvider = "hardCodedBrowsers")
    public void AuraPageGastronomy(String type, String browser, String version, String os, String device, Method method) throws Exception {

        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.zedaerospace.com/");
        driver.findElement(By.linkText("GASTRONOMY")).click();
    }
    @Test(dataProvider = "hardCodedBrowsers")
    public void AuraPageRoutes(String type, String browser, String version, String os, String device, Method method) throws Exception {

        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.zedaerospace.com/");
        driver.findElement(By.linkText("ROUTES + PRICE")).click();
    }
    @Test(dataProvider = "hardCodedBrowsers")
    public void AuraPageJoin(String type, String browser, String version, String os, String device, Method method) throws Exception {

        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.zedaerospace.com/");
        driver.findElement(By.xpath("//nav[@id='nav-menu-container']/ul/li[6]/a/button")).click();
    }
    /**
     * @return the {@link WebDriver} for the current thread
     */
    public WebDriver getWebDriver() {
        System.out.println("WebDriver" + webDriver.get());
        return webDriver.get();
    }

    /**
     *
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }
}