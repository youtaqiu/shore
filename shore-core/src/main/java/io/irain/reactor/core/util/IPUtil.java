package io.irain.reactor.core.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * @author youta
 **/
public class IPUtil {

    private IPUtil() {
    }

    private static final boolean IP_LOCAL = false;
    private static final String URI = "https://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";

    /**
     * 根据ip获取详细地址
     *
     * @param ip ip
     * @return 详细地址
     */
    public static String getCityInfo(String ip) {
        if (IP_LOCAL) {
            return null;
        } else {
            return getHttpCityInfo(ip);
        }
    }

    /**
     * 根据ip获取详细地址
     *
     * @param ip ip
     * @return 详细地址
     */
    public static String getHttpCityInfo(String ip) {
        String api = String.format(URI, ip);
        JSONObject object = JSONUtil.parseObj(HttpUtil.get(api));
        return object.getStr("addr");
    }

}
