package app.shop.util.loger;

import java.util.ArrayList;
import java.util.List;

public class Logger {
    private static StringBuilder builder = new StringBuilder();
    private static List<String> list = new ArrayList<>();
    private static int count = 0;

    public static void debugLog(String log){
        System.out.println("DEBUG LOG   INFO: " + log);
    }

    /**
     * Вывести в консоль информацию уникальную на протяжении 3-х сообщений.
     * @param log сообщение.
     */

    public static void debugLogNews(String log){
        if(list.contains(log)) {
            count++;
            return;
        }
        if(count > 2) count = 0;
        list.add(log);
        debugLog(log);
    }
}
