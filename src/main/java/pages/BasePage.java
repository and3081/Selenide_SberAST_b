package pages;

import custom.properties.TestData;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Класс родительского PO (общие xpath и методы, методы создания PO страниц)
 */
public class BasePage {
    /**
     * Значение явного ожидания ms из проперти
     */
    public long timeoutExplicitMs = Long.parseLong(TestData.browser.defaultTimeoutExplicitMs());

    /**
     * Шаг Проверить фрагмент title страницы
     * @param step  номер шага для аллюра
     * @param title фрагмент title
     */
    @Step("step {step}. Проверить фрагмент title страницы '{title}'")  // step 2
    public void checkTitleFragment(int step, String title) {
        $x("//head/title").shouldHave(match("Проверка фрагмента title",
                (el)-> el.getAttribute("textContent").contains(title)));
    }

    /**
     * Максимизация окна браузера
     * static
     */
    public static void maxWindow() { getWebDriver().manage().window().maximize(); }

    /**
     * Шаг Открыть браузер и стартовую страницу Сбер-АСТ, максимизация окна браузера
     * static
     * @param step  номер шага для аллюра
     * @return PO PageSberAstMain
     */
    @Step("step {step}. Открыть браузер и стартовую страницу Сбер-АСТ")  // step 1
    public static PageSberAstMain openFirstPageSberAst(int step) {
        open(TestData.application.baseUrlSberAst());
        maxWindow();
        return page(PageSberAstMain.class); }

    /**
     * Ожидание и выполнение реального клика, при ElementClickInterceptedException (перекрытие элемента)
     * отправляется ESC в фокус для попытки снятия попапа
     * @param el  элемент для клика
     * @param xpath  для попытки заново получить элемент / null
     * @return true- клик сделан
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean waitRealClick(SelenideElement el, String xpath) {
        boolean[] isClick = new boolean[]{false};

        new WebDriverWait(getWebDriver(), Duration.ofMillis(timeoutExplicitMs))
                .pollingEvery(Duration.ofMillis(200))
                .ignoreAll(List.of(TimeoutException.class))
                .withMessage("Ожидание клика на элемент исчерпано (возможно элемент чем-то закрыт):\n" + xpath)
                .until((ExpectedCondition<Boolean>) driver -> {
                    try {
                        el.toWebElement().click();  // селениумным кликом !! иначе селенидный исключение блокирует
                    } catch (ElementClickInterceptedException e) {
                        actions().sendKeys(Keys.ESCAPE).perform();  // попытка снять попап
                        return false;
                    } catch (Exception e) {
                        if (xpath != null) {
                            assert driver != null;
                            driver.findElement(By.xpath(xpath)).click();  // попытка заново получить элемент
                        }
                        return false;
                    }
                    isClick[0] = true;
                    return true; });
        return isClick[0];
    }

    /**
     * Ожидание и выполнение реального send
     * @param el     элемент для send
     * @param xpath  для попытки заново получить элемент / null
     * @param text   текст для send
     * @return true- send сделан
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean waitRealSend(SelenideElement el, String xpath, String text) {
        boolean[] isSend = new boolean[]{false};

        new WebDriverWait(getWebDriver(), Duration.ofMillis(timeoutExplicitMs))
                .pollingEvery(Duration.ofMillis(200))
                .ignoreAll(List.of(TimeoutException.class))
                .withMessage("Ожидание send '"+ text +"' в элемент исчерпано:\n" + xpath)
                .until((ExpectedCondition<Boolean>) driver -> {
                    try {
                        el.sendKeys(text);
                    } catch (Exception e) {
                        if (xpath != null) {
                            assert driver != null;
                            driver.findElement(By.xpath(xpath)).sendKeys(text);  // попытка заново получить элемент
                        }
                        return false;
                    }
                    isSend[0] = true;
                    return true; });
        return isSend[0];
    }
}
