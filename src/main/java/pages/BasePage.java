package pages;

import custom.properties.TestData;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
     * Объект явных ожиданий (для метода реального клика)
     */
    private final WebDriverWait waitClick = new WebDriverWait(getWebDriver(), Duration.ofMillis(timeoutExplicitMs));

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
        open(TestData.browser.baseUrlSberAst());
        maxWindow();
        return page(PageSberAstMain.class); }

    /**
     * Ожидание и выполнение реального клика, при ElementClickInterceptedException (перекрытие элемента)
     * отправляется ESC в фокус для попытки снятия попапа
     * @param el  элемент для клика
     * @return true- клик сделан
     */
    public boolean waitRealClick(SelenideElement el) {
        boolean[] isClick = new boolean[]{false};
        waitClick.until((ExpectedCondition<Boolean>) x->{
            try { el.toWebElement().click();  // селениумным кликом !! иначе селенидный исключение блокирует
            } catch (ElementClickInterceptedException e) {
                actions().sendKeys(Keys.ESCAPE).perform();  // попытка снять попап
                return false;
            } catch (Exception e) {
                return false;
            } isClick[0] = true; return true; });
        return isClick[0];
    }
}
