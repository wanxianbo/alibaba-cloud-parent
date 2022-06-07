package com.wanxb.constants;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author 邱理
 * @description
 * @date 创建于 2019/5/31
 */
public interface DateTimeFormatConstants {

    String COMMON_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String COMMON_DATETIME_FORMAT2 = "yyyy/MM/dd HH:mm:ss";
    String DATE_FORMAT_WITHOUT_SECONDS = "yyyy-MM-dd HH:mm";
    String COMMON_DATETIME_FORMAT_EXAMPLE = "2019-06-05 10:00:00";

    String NORM_DATE_PATTERN = "yyyy-MM-dd";
    String PURE_DATETIME_PATTERN  = "yyyyMMddHHmmss";
    String OTHER_DATE_PATTERN = "yyyy.MM.dd";
    String CN_DATE_PATTERN = "yyyy年MM月dd日";
    String CN_DATE_BIRTHDAY = "MM月dd日";
    String TIME_ZONE = "GMT+8";
    String Local="zh";

    String MONTH_DAY = "MM-dd";

    String YEAR_MONTH = "yyyy-MM";

    ZoneId UTC = ZoneId.of("UTC");

    ZoneId DEFAULT_ZONE = ZoneOffset.systemDefault();

    DateTimeFormatter COMMON_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(COMMON_DATETIME_FORMAT);

    DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern(YEAR_MONTH);


    /**
     * 动态生成表日期后缀
     */
    DateTimeFormatter YEAR_MONTH_TABLE_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM");

}
