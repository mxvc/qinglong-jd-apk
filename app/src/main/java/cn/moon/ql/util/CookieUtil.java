package cn.moon.ql.util;

import android.text.TextUtils;
import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookieUtil {

    public static Map<String, String> parse(String cookies) {
        String[] cookiesArr = cookies.split(";");

        Map<String, String> map = new HashMap<>();
        for (String ck : cookiesArr) {
            String[] arr = ck.split("=");
            if (arr.length == 2) {
                String k = arr[0];
                String v = arr[1];

                if(!TextUtils.isEmpty(v)){
                    map.put(k.trim(), v.trim());
                }


            }
        }

        return map;
    }

    public static Map<String, String> parse(String cookies,String... keys) {
        Map<String, String> map = parse(cookies);

        Map<String,String> newMap = new HashMap<>();
        for (String key : keys) {
            String value = map.get(key);
            if(!TextUtils.isEmpty(value)){
                newMap.put(key, value);
            }
        }
        return newMap;
    }

    public static String join(Map<String, String> cookieMap) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> e : cookieMap.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();

            list.add(key+"="+value);
        }
        return TextUtils.join("; ", list);
    }


}
