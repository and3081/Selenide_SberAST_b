package Custom.properties;

import org.aeonbits.owner.ConfigFactory;

/**
 * Класс экзекутор для работы с проперти
 * usage:  TestData.props.имяМетодаКлюча()
 */
public class TestData {
    /**
     * static метод для работы с проперти из файла browser.properties и listener.properties
     */
    public static Browser browser = ConfigFactory.create(Browser.class);
    public static Listener listener = ConfigFactory.create(Listener.class);
}
