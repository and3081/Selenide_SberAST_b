package ru.vasyukov.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.vasyukov.hooks.WebHooks;

import static pages.BasePage.openFirstPageSberAst;

/**
 * Класс тест-кейсов
 * @author Васюков А.Ю.  GitHub  https://github.com/and3081/Selenide_SberAST_b
 * @version 1.0
 * Описание тест-кейса:
 * 1. сайт https://www.sberbank-ast.ru госзакупки
 * 2. проверить title
 * 3. запрос "Страхование"
 * 4. вывести инфо о первых 10 заказах удовлетворяющих условиям:
 *    цена > 600 тыс. руб,
 *    тип Госзакупки по 44-ФЗ.
 *    Проверять только первые 120 результатов в списке.
 * 5. проверить количество фактической выборки
 * 6. В инфо выводить: название, цену, номер (в консоль, в Step аллюра),
 *    Json-attachment в аллюр.
 *
 * Настраиваемые листенеры в проперти:
 *       - какие типы методов скринить: драйвер, элементы, все варианты или отключить
 *       - какие выборочно по названиям методы скринить
 *       - какие скрины вокруг методов делать: перед, после, оба
 *       - при скрининге элементов:     всё окно, только элемент или оба скрина сразу
 *
 *       В аллюре скрины подписаны- перед и после какого метода, аргументы и возврат метода
 *
 *       - выбор браузера в проперти для прогона тестов:  Chrome, Edge
 */
public class Tests extends WebHooks {
    /**
     * Тест-кейс выборки и поиска в Сбербанк-АСТ
     * параметры поставляются провайдером данных providerSberAst()
     * @param search          текст для поиска (Страхование)
     * @param price           больше цены (600000)
     * @param currency        валюта (RUB)
     * @param law             фрагмент закона (44-ФЗ)
     * @param maxCountView    макс.кол-во позиций просмотра (120)
     * @param countChoice     количество для выборки (10)
     */
    @DisplayName("Тестирование выборки в Сбер-АСТ")
    @ParameterizedTest(name = "{arguments}")
    @MethodSource("ru.vasyukov.test.DataProvider#providerSberAst")
    public void testSberAstChoice(String search, double price, String currency, String law,
                                  int maxCountView, int countChoice) {
        openFirstPageSberAst(1)
                .checkSberAstTitle(2)
                .inputSearchField(3, search)
                .collectAllPageResults(4, price, currency, law, maxCountView, countChoice)
                .assertResults(5, countChoice)
                .reportResults(6);
    }
}
