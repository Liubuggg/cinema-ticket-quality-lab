package com.usercinema.domain;

import java.math.BigDecimal;

/**
 * 一个确定日期、影厅和时间段的放映场次。
 */
public final class Screening
{
    private final CinemaDay day;
    private final int hallNumber;
    private final int timeSlot;
    private final String timeText;
    private final Film film;
    private final BigDecimal price;
    private final SeatMap seatMap;
    private boolean onSale;

    public Screening(CinemaDay day, int hallNumber, int timeSlot, String timeText,
                     Film film, BigDecimal price)
    {
        this.day = day;
        this.hallNumber = hallNumber;
        this.timeSlot = timeSlot;
        this.timeText = timeText;
        this.film = film;
        this.price = price;
        this.seatMap = new SeatMap();
        this.onSale = true;
    }

    public CinemaDay getDay()
    {
        return day;
    }

    public int getHallNumber()
    {
        return hallNumber;
    }

    public int getTimeSlot()
    {
        return timeSlot;
    }

    public String getTimeText()
    {
        return timeText;
    }

    public Film getFilm()
    {
        return film;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public SeatMap getSeatMap()
    {
        return seatMap;
    }

    public boolean isOnSale()
    {
        return onSale;
    }

    /** 停止当前场次的售票。 */
    public void stopSale()
    {
        onSale = false;
    }

    /** 生成用于查找场次的唯一键。 */
    public String getKey()
    {
        return day.getCode() + "-" + hallNumber + "-" + timeSlot;
    }
}
