package com.github.joergpfruender;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({RecordVideoOnFailedTestExtension.class})
public class SampleTest implements HasWebDriver {

    static WebDriver webDriver;
    private static int port = 8080;
    private static HttpServer httpServer;

    @BeforeAll
    public static void setUp() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                httpServer = new HttpServer(port);
                httpServer.startServer();
            } catch (IOException e) {
                throw new RuntimeException("could not run http server", e);
            }
        });
        Testcontainers.exposeHostPorts(port);
        BrowserWebDriverContainer container = new BrowserWebDriverContainer()
                .withCapabilities(new ChromeOptions())
                .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING, new File("./build/"));
        webDriver = VideoRecordingWebDriver.create(container);
    }


    @AfterAll
    static void quitWebDriver() {
        webDriver.quit();
    }

    @AfterAll
    public static void stopServer() {
        if (httpServer != null) {
            httpServer.stopServer();
        }
    }

    @Test
    public void sampleTest() throws Exception {
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.get("http://host.testcontainers.internal:" + port + "/");
        WebElement element = webDriver.findElement(By.className("selenium-id-helloworld"));
        assertEquals("Hello World", element.getText());
        WebElement input = webDriver.findElement(By.className("selenium-id-testinput"));
        input.sendKeys("meinTest");
        assertEquals("meinTest", input.getAttribute("value"));
        Thread.sleep(2000);
        org.junit.Assert.fail("fail"); //fail to get screenshot
    }

    @Override
    public WebDriver getWebDriver() {
        return webDriver;
    }
}
