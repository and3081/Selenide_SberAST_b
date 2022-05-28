package ru.vasyukov.test;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Класс провайдера данных для тестов
 */
public class DataProvider {
    /**
     * Метод-провайдер для тест-кейса testSberAstChoice()
     * @return  стрим аргументов: текст для поиска, больше цены, валюта,
     *                            фрагмент закона, макс.кол-во позиций просмотра, количество для выборки
     */
    protected static Stream<Arguments> providerSberAst() {
        return Stream.of(arguments("Страхование", 600000.0, "RUB", "44-ФЗ", 120, 10));
    }
}
