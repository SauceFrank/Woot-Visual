package com.woot;

import org.openqa.selenium.*;
import org.testng.Assert;
import java.util.Map;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.Assert.assertEquals;

public class WootTest {
//Environment variables
    public String buildTag = System.getenv("BUILD_TAG");
    public String username = System.getenv("SAUCE_USERNAME");
    public String accesskey = System.getenv("SAUCE_ACCESS_KEY");
    public String sauceTunnel = System.getenv("SAUCE_TUNNEL");

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
                new Object[]{"browser","firefox", "latest-2", "Windows 10",""},
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
        MutableCapabilities sauceVisual = new MutableCapabilities();
        sauceVisual.setCapability("apiKey", System.getenv("SCREENER_API_KEY"));
        sauceVisual.setCapability("projectName", "Woot-project");
//        sauceVisual.setCapability("viewportSize", "2580x1600");
        capabilities.setCapability("sauce:visual", sauceVisual);
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("username", username);
        sauceOptions.setCapability("accesskey", accesskey);
        sauceOptions.setCapability("tunnelIdentifier", sauceTunnel);
        capabilities.setCapability("build", buildTag);
        capabilities.setCapability("sauce:options", sauceOptions);
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
/*        webDriver.set(new RemoteWebDriver(
                new URL("https://ondemand.us-west-1.saucelabs.com:443/wd/hub"),
                capabilities));*/
//US
//Creates Visual Driver
        webDriver.set(new RemoteWebDriver(
                new URL("https://hub.screener.io:443/wd/hub"),
                capabilities));

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
    protected void annotate(String text) {
        ((JavascriptExecutor) webDriver.get()).executeScript("sauce:context=" + text);
    }
    protected void stopLog() {
        ((JavascriptExecutor) webDriver.get()).executeScript("sauce: disable log" );
    }
    protected void startLog() {
        ((JavascriptExecutor) webDriver.get()).executeScript("sauce: enable log" );
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
    public void WootPageTitle(String type, String browser, String version, String os, String device, Method method) throws Exception {
        this.createDriver(type, browser, version, os, device, method.getName());
        WebDriver driver = this.getWebDriver();
        driver.get("https://woot.com");
        assertEquals(driver.getTitle(), "Woot");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("/*@visual.init*/", "Woot Visual Test");

        js.executeScript("/*@visual.snapshot*/", "Woot Home Page");

        this.annotate("sleeping for a few seconds so humans can see the changes");
        js.executeScript("/*@visual.snapshot*/", "Woot Page");

    }
    @Test(dataProvider = "hardCodedBrowsers")
    public void WootElectronics(String type, String browser, String version, String os, String device, Method method) throws Exception {
        this.createDriver(type, browser, version, os, device, method.getName());
        WebDriver driver = this.getWebDriver();
        driver.get("https://woot.com");
        driver.findElement(By.xpath("//div[@id='category-tab-electronics-woot']/a/span")).click();
        assertEquals(driver.getTitle(), "Discount Electronics | Woot");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("/*@visual.init*/", "Woot Electronics Test");

        js.executeScript("/*@visual.snapshot*/", "Woot Electronics Page");

        this.annotate("sleeping for a few seconds so humans can see the changes");
        js.executeScript("/*@visual.snapshot*/", "Woot Electronics");
        Map response = (Map)((JavascriptExecutor) driver).executeScript("/*@visual.end*/");
        Assert.assertTrue((Boolean)response.get("passed"), (String)response.get("message"));

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