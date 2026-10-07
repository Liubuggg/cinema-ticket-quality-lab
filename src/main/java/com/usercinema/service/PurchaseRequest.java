package com.usercinema.service;

import com.usercinema.domain.CinemaDay;
import com.usercinema.domain.Seat;

import java.util.List;

/**
 * 保存一次购票请求所需的信息。
 */
public final class PurchaseRequest
{
    private final int customerId;
    private final CinemaDay day;
    private final int hallNumber;
    private final int timeSlot;
    private final List<Seat> seats;
    private final String paymentPassword;

    public PurchaseRequest(int customerId, CinemaDay day, int hallNumber,
                           int timeSlot, List<Seat> seats, String paymentPassword)
    {
        this.customerId = customerId;
        this.day = day;
        this.hallNumber = hallNumber;
        this.timeSlot = timeSlot;
        this.seats = seats == null ? List.of() : List.copyOf(seats);
        this.paymentPassword = paymentPassword;
    }

    public int getCustomerId()
    {
        return customerId;
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

    public List<Seat> getSeats()
    {
        return seats;
    }

    public String getPaymentPassword()
    {
        return paymentPassword;
    }
}
