package com.woot;

import com.saucelabs.visual.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import com.saucelabs.visual.testng.TestMetaInfoListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import static org.testng.Assert.assertEquals;

public class WootTest {
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
    private static VisualApi visual;
    private static RemoteWebDriver driver;

    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{

//                // Windows
//                new Object[]{"browser","chrome", "latest", "Windows 11",""},
//                new Object[]{"browser","chrome", "latest-1", "Windows 10",""},
                new Object[]{"browser","MicrosoftEdge", "124", "Windows 10",""},
//                new Object[]{"browser","firefox", "latest-2", "Windows 10",""},
/*
                // Mac
                new Object[]{"browser","safari", "latest", "macOS 10.13",""},
                new Object[]{"browser","chrome", "latest", "macOS 10.14",""},
                new Object[]{"browser","firefox", "latest", "macOS 10.14",""},
                new Object[]{"browser","chrome", "latest", "OS X 10.14",""},

                //Devices
                new Object[]{"device","", "", "Android","Samsung Galaxy S22"},
                new Object[]{"device","", "", "iOS", "iPhone 14 Pro Max.*"},
                new Object[]{"device","", "", "iOS", "iPad 10.2"},
                new Object[]{"device","", "", "Android","Samsung.*"},
                new Object[]{"device","", "", "iOS", "iPhone.*"},
                new Object[]{"device","", "", "Android","Google.*"},
                new Object[]{"device","", "", "iOS", "iPad.*"}, */
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
//        capabilities.setCapability("tunnelIdentifier","FrankTunnel");
        String jobName = methodName;
        capabilities.setCapability("name", jobName);

        if (environment == "browser") {
            capabilities.setCapability("browserName", browser);
            capabilities.setCapability("version", version);
            capabilities.setCapability("platform", os);
//            capabilities.setCapability("extendedDebugging", true);
//            capabilities.setCapability("capturePerformance", true);
        } else {
            capabilities.setCapability("platformName", os);
            capabilities.setCapability("deviceName", device);
        }

        //US
        //Creates Selenium Driver
        //capabilities.setCapability("sauce:options", capabilities);

        URL url = new URL("https://ondemand.us-west-1.saucelabs.com/wd/hub");
        driver = new RemoteWebDriver(url, capabilities);
        visual = new VisualApi.Builder(driver, sauceUsername, sauceAccessKey, DataCenter.US_WEST_1)
                .withBuild("Woot Test")
                .withBranch("main")
                .withProject("Woot Example")
                .withCaptureDom(true)
                .build();
        // EU
        // webDriver.set(new RemoteWebDriver(
        //         new URL("https://ondemand.eu-central-1.saucelabs.com/wd/hub"),
        //         capabilities));

        //Keeps track of the unique Selenium session ID used to identify jobs on Sauce Labs
        String id = driver.getSessionId().toString();
        sessionId.set(id);

        //For CI plugins
        String message = String.format("SauceOnDemandSessionID=%1$s job-name=%2$s", id, jobName);
        System.out.println(message);

        return driver;
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws Exception {
        boolean status = result.isSuccess();
        driver.executeScript("sauce:job-result="+ status);
        driver.quit();
    }
    @AfterSuite
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    /**
     * Runs a simple test verifying the title of the wikipedia.org home page.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @throws Exception if an error occurs during the running of the test
     */
    public static String sauceUsername = System.getenv("SAUCE_USERNAME");
    public static String sauceAccessKey = System.getenv("SAUCE_ACCESS_KEY");
    @BeforeSuite
    public static void init() {
    }
    @Listeners({TestMetaInfoListener.class})
    public class MyTestNGTestClass {
//    ...
    }
    @Test(dataProvider = "hardCodedBrowsers")
    public void WootPageTitle (String type, String browser, String version, String os, String device, Method method) throws Exception {
        WebDriver driver = createDriver(type, browser, version, os, device, method.getName());
        driver.get("https://www.woot.com/alldeals?ref=w_ngh_et_1");
        assertEquals(driver.getTitle(), "Woot");
        visual.sauceVisualCheck("Woot Page");
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
