package Custom.Json;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

/**
 * Класс работы с Json
 */
public class MyJson {
    /**
     * Получение Json-string из List<Map<String, String>>
     * @param list входной List<Map<String, String>>
     * @return Json-string / null при err
     */
    public static String listMapToJson(List<Map<String, String>> list) {
        JSONArray json = new JSONArray(list);
        return json.toString(2);
    }
}

