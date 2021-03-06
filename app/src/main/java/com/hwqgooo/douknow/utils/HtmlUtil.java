package com.hwqgooo.douknow.utils;

import android.text.TextUtils;

import java.util.List;

public class HtmlUtil {

    public static final String MIME_TYPE = "text/html; charset=utf-8";
    public static final String ENCODING = "utf-8";
    //css样式,隐藏header
    private static final String HIDE_HEADER_STYLE = "<style>div.headline{display:none;}</style>";
    //css style tag,需要格式化
    private static final String NEEDED_FORMAT_CSS_TAG = "<link rel=\"stylesheet\" " +
            "type=\"text/css\" href=\"%s\"/>";
    // js script tag,需要格式化
    private static final String NEEDED_FORMAT_JS_TAG = "<script src=\"%s\"></script>";

    private HtmlUtil() {
    }

    /**
     * 生成完整的HTML文档
     *
     * @return String
     */
//    public static String createHtmlData(DailyDetail detail) {
//        final String css = HtmlUtil.createCssTag(detail.getCss());
//        final String js = HtmlUtil.createJsTag(detail.getJs());
//        return createHtmlData(detail.getBody(), css, js);
//    }

    /**
     * 根据多个css链接生成Link标签
     *
     * @param urls List<String>
     * @return String
     */
    public static String createCssTag(List<String> urls) {
        final StringBuilder sb = new StringBuilder();
        for (String url : urls) {
            sb.append(createCssTag(url));
        }
        return sb.toString();
    }

    /**
     * 根据多个js链接生成Script标签
     *
     * @param urls List<String>
     * @return String
     */
    public static String createJsTag(List<String> urls) {
        final StringBuilder sb = new StringBuilder();
        for (String url : urls) {
            sb.append(createJsTag(url));
        }
        return sb.toString();
    }

    /**
     * 根据样式标签,html字符串,js标签
     * 生成完整的HTML文档
     *
     * @param html string
     * @param css  string
     * @param js   string
     * @return string
     */
    public static String createHtmlData(String html, String css, String js) {
        if (TextUtils.isEmpty(html)) {
            return "";
        }

        return css.concat(HIDE_HEADER_STYLE).concat(html).concat(js);
    }

    /**
     * 根据css链接生成Link标签
     *
     * @param url String
     * @return String
     */
    public static String createCssTag(String url) {
        return String.format(NEEDED_FORMAT_CSS_TAG, url);
    }

    /**
     * 根据js链接生成Script标签
     *
     * @param url String
     * @return String
     */
    public static String createJsTag(String url) {
        return String.format(NEEDED_FORMAT_JS_TAG, url);
    }
}
