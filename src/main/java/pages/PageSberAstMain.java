package pages;

import custom.json.MyJson;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Page класс страницы https://www.sberbank-ast.ru/
 */
@SuppressWarnings("FieldCanBeLocal")
public class PageSberAstMain extends BasePage {
    /**
     * title базовой страницы
     */
    private final String TITLE_SBER_AST = "Сбербанк-АСТ - электронная торговая площадка";
    /**
     * xPath поля поиска
     */
    private final String XPATH_SEARCH_FIELD = "//div[@class='default_search_border']//input";
    /**
     * xPath элемента для анализа прогресса поиска/выборки
     */
    private final String XPATH_PROGRESS = "//div[@id='ajax-background']";
    /**
     * xPath результатов поиска/выборки
     */
    private final String XPATH_RESULTS = "//div[@id='resultTable']//div[@content='node:hits']";
    /**
     * xPath поля Цена в результатах
     */
    private final String XPATH_ITEM_AMOUNT = ".//span[@class='es-el-amount']";
    /**
     * xPath поля Валюта в результатах
     */
    private final String XPATH_ITEM_CURRENCY = ".//span[@class='es-el-currency']";
    /**
     * xPath поля Закон в результатах
     */
    private final String XPATH_ITEM_LAW = ".//span[@class='es-el-source-term']";
    /**
     * xPath поля Название в результатах
     */
    private final String XPATH_ITEM_NAME = ".//span[@class='es-el-name']";
    /**
     * xPath поля Организация в результатах
     */
    private final String XPATH_ITEM_ORG = ".//div[@class='es-el-org-name']";
    /**
     * xPath поля Код в результатах
     */
    private final String XPATH_ITEM_CODE = ".//span[@class='es-el-code-term']";
    /**
     * xPath кнопки перехода на следующую страницу
     */
    private final String XPATH_NEXT_PAGE_BUTTON = "//div[@id='pager']//span[text()='>']";
    /**
     * Структура для сбора выборки
     */
    protected List<Map<String, String>> listAll = new ArrayList<>();

    /**
     * Шаг Проверить title страницы
     * @param step номер шага для аллюра
     * @return свой PO
     */
    public PageSberAstMain checkSberAstTitle(int step) {
        checkTitleFragment(step, TITLE_SBER_AST);
        return this;
    }

    /**
     * Шаг Ввод значения поиска и нажатие Enter
     * @param step номер шага для аллюра
     * @param text значения поиска
     * @return свой PO
     */
    @Step("step {step}. Поиск '{text}'")  // step 3
    public PageSberAstMain inputSearchField(int step, String text) {
        $x(XPATH_SEARCH_FIELD).shouldBe(visible, enabled).setValue(text).pressEnter();
        return this;
    }

    /**
     * Шаг Сбор информации со всех страниц выборки и выборка по условиям
     * @param step номер шага для аллюра
     * @param price           больше цены
     * @param currency        валюта
     * @param law             фрагмент закона
     * @param maxCountView    макс.кол-во позиций просмотра
     * @param countChoice     количество для выборки
     * @return свой PO
     */
    @Step("step {step}. Сбор информации '{price}' '{currency}' '{law}' (позиций: {countChoice})")  // step 4
    public PageSberAstMain collectAllPageResults(int step, double price, String currency, String law,
                                                 int maxCountView, int countChoice) {
        int countView = 0, nextPageNumber = 1, subStep = 0;
        do {
            subStep++;
            nextPageNumber++;
            List<Map<String, String>> list = collectPageResults(step, subStep);
            countView += list.size();
            list.stream()
                    .filter(el -> el.get("currency").equals(currency)
                            && el.get("law").contains(law)
                            && Double.parseDouble(el.get("price")) > price)
                    .forEach(el -> {
                        if (listAll.size() < countChoice) listAll.add(el);
                    });
        } while (listAll.size() < countChoice && countView < maxCountView && clickNextPage(nextPageNumber));
        return this;
    }

    /**
     * Шаг Ожидание прогресса поиска и Сбор найденной информации с текущей страницы
     * @param step    номер шага для аллюра
     * @param subStep номер текущей страницы
     * @return List<Map> с информацией
     */
    @Step("step {step}. Сбор со страницы: {subStep}")  // step 4...
    public List<Map<String, String>> collectPageResults(int step, int subStep) {
        waitEndProgress();
        List<Map<String, String>> list = new ArrayList<>();
        $$x(XPATH_RESULTS)
                .shouldBe(sizeGreaterThan(0))
                .asDynamicIterable()
                .forEach(el-> list.add(Map.of(
                        "price", el.$x(XPATH_ITEM_AMOUNT).getText().replace(" ", ""),
                        "currency", el.$x(XPATH_ITEM_CURRENCY).getText(),
                        "law", el.$x(XPATH_ITEM_LAW).getText(),
                        "name", el.$x(XPATH_ITEM_NAME).getText(),
                        "org", el.$x(XPATH_ITEM_ORG).getText(),
                        "code", el.$x(XPATH_ITEM_CODE).getText())));
        return list;
    }

    /**
     * Шаг Проверка количества выборки
     * @param step        номер шага для аллюра
     * @param countChoice ожидаемое количество выборки
     * @return свой PO
     */
    @Step("step {step}. Проверка количества выборки: {countChoice}")  // step 5
    public PageSberAstMain assertResults(int step, int countChoice) {
        Assertions.assertEquals(countChoice, listAll.size(),
                "Не найдено заданное количество требуемых позиций");
        return this;
    }

    /**
     * Шаг Формирование отчета (название, цена, номер):
     *   вывод в консоль,
     *   вывод в названиях Step аллюра,
     *   Json-attachment в аллюр
     * @param step номер шага для аллюра
     * @return свой PO
     */
    @Step("step {step}. Отчет")  // step 6
    public PageSberAstMain reportResults(int step) {
        AtomicInteger i = new AtomicInteger(1);
        listAll.forEach(el-> {
            System.out.printf("%2d Название: %s\n"+
                            "   Цена:     %s\n"+
                            "   Номер:    %s\n-----------------------------------------\n",
                    i.get(), el.get("name"), el.get("price"), el.get("code"));
            reportStep(step, i.get(), el.get("name"), el.get("price"), el.get("code"));
            i.incrementAndGet();
        });
        String json = MyJson.listMapToJson(listAll);
        if (json !=null) Allure.addAttachment("Данные выборки Json", "application/json", json);
        return this;
    }

    /**
     * Подшаг Вывод инфо в названиях Step аллюра
     * @param step    номер шага для аллюра
     * @param subStep порядковый номер подшага
     * @param name    название
     * @param price   цена
     * @param code    код
     */
    @Step("step {step}.{subStep}. {name}  Цена:{price}  Код:{code}")  // step 6
    public void reportStep(int step, int subStep, String name, String price, String code) {}

    /**
     * Ожидание окончания прогресса выборки/поиска
     */
    public void waitEndProgress() {
        $x(XPATH_PROGRESS)
                .shouldBe(exist, attribute("style", "display: none;"));
    }

    /**
     * Клик кнопки перехода на следующую страницу поиска при ее актуальности
     * @param nextNumber номер следующей страницы
     * @return true- кнопка актуальна (есть следующая страница), клик выполнен
     */
    public boolean clickNextPage(int nextNumber) {
        String attr = $x(XPATH_NEXT_PAGE_BUTTON).shouldBe(exist).getAttribute("content");
        if (attr!=null && nextNumber == Integer.parseInt(attr)) {
            $x(XPATH_NEXT_PAGE_BUTTON).shouldBe(visible, enabled).click();
            return true;
        }
        return false;
    }
}
