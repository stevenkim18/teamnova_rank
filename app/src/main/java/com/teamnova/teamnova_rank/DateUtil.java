package com.teamnova.teamnova_rank;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * 클래스명 : DateUtil
 * 설명 : 날짜를 여러 형태로 가공할때 쓰는 클래스
 */
public class DateUtil {

    /**
     * 오늘 날짜를 yyyy-MM-dd 형태로 반환한다.
     * @return
     */
    public static String getToday(){
        GregorianCalendar today = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(today.getTime());
        return todayStr;
    }
}
