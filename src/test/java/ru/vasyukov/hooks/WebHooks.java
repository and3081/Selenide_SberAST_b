package ru.vasyukov.hooks;

import custom.listeners.Listeners;
import custom.properties.TestData;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.WebDriverListener;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.WebDriverRunner.*;

/**
 * Класс хуков для настройки web драйвера по проперти browser.properties:
 *  - локального
 *  - удаленного (Selenide)
 *  - ремоут (Selenoid)
 */
public class WebHooks {
    /**
     * Объект Listeners в зависимости от настройки в проперти или null
     */
    private static final WebDriverListener listener = Listeners.getListener();

    // Настройка опций браузера и листенера первый раз
    static {
        Configuration.timeout = Long.parseLong(TestData.browser.defaultTimeoutImplicitMs());
        if (listener != null) addListener(listener);
        if (TestData.browser.headlessMode() != null) Configuration.headless = true;
        if (TestData.browser.dontCloseBrowser() != null) Configuration.holdBrowserOpen = true;

        if (TestData.browser.remoteUrl() != null) {
            Configuration.remote = TestData.browser.remoteUrl();
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("browserName", "chrome");
            capabilities.setCapability("browserVersion", "101.0");
            Map<String, Object> map = new HashMap<>();
            map.put("enableVNC", true);
            map.put("enableVideo", true);
            capabilities.setCapability("selenoid:options", map);
            Configuration.browserCapabilities = capabilities;
        } else {
            if ((TestData.browser.typeBrowser() == null || TestData.browser.typeBrowser().equals("chrome")) &&
                    TestData.browser.webdriverChromeLocalPath() != null) {
                System.setProperty("webdriver.chrome.driver", TestData.browser.webdriverChromeLocalPath());
                WebDriver driver = new ChromeDriver();
                setWebDriver(driver);
            } else if (TestData.browser.typeBrowser() != null)
                if (TestData.browser.typeBrowser().equals("edge")) {
                    System.setProperty("webdriver.edge.driver", TestData.browser.webdriverEdgeLocalPath());
                    WebDriver driver = new EdgeDriver();
                    setWebDriver(driver);
                } else Configuration.browser = TestData.browser.typeBrowser();
            else Configuration.browser = "chrome";
        }
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("start-maximized");  // устарел, use  getWebDriver().manage().window().maximize();
//        DesiredCapabilities capabilities = new DesiredCapabilities();  // old
//        MutableCapabilities capabilities = new MutableCapabilities();  // new
//        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
//        Configuration.browserCapabilities = capabilities;
    }

    /**
     * перед каждым тестом (тут нет, open в тесте)
     */
    @BeforeEach
    public void openBrowser() {
    }

    /**
     * Закрытие браузера после каждого теста,
     * необходимо при повторе теста по параметризованным производителям
     */
    @AfterEach
    public void closeBrowser() {
        //closeWindow();  // holdBrowserOpen с этим не работает
        closeWebDriver();
    }
}
