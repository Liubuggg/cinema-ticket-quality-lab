package com.usercinema.domain;

/**
 * 电影院一周内使用的日期标识。
 */
public enum CinemaDay
{
    MONDAY("Mon", "周一"),
    TUESDAY("Tue", "周二"),
    WEDNESDAY("Wed", "周三"),
    THURSDAY("Thur", "周四"),
    FRIDAY("Fri", "周五"),
    SATURDAY("Sat", "周六"),
    SUNDAY("Sun", "周日");

    private final String code;
    private final String displayName;

    CinemaDay(String code, String displayName)
    {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode()
    {
        return code;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    /** 根据日期代码查找对应的星期。 */
    public static CinemaDay fromCode(String code)
    {
        for (CinemaDay day : values())
        {
            if (day.code.equalsIgnoreCase(code))
            {
                return day;
            }
        }
        throw new IllegalArgumentException("不存在的放映日期: " + code);
    }
}
