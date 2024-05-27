package io.irain.reactor.core.util;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;


/**
 * @author youta
 **/
public class LocaleMsgUtil {

    private static MessageSource messageSource;

    /**
     * 初始化
     * @param messageSource 国际化信息源
     */
    public static void inti(MessageSource messageSource) {
        LocaleMsgUtil.messageSource = messageSource;
    }

    /**
     * 获取国际化信息
     * @param msgKey 信息key
     * @return 信息
     */
    public static String get(String msgKey) {
        if (CharSequenceUtil.isBlank(msgKey)) {
            return msgKey;
        }
        try {
            return messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return msgKey;
        }
    }

}
