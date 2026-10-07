package com.usercinema.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购票成功后生成的电子电影票。
 */
public final class Ticket
{
    private final String electronicId;
    private final int customerId;
    private final Screening screening;
    private final Seat seat;
    private final BigDecimal paidPrice;
    private final LocalDateTime purchasedAt;
    private boolean pickedUp;

    public Ticket(String electronicId, int customerId, Screening screening,
                  Seat seat, BigDecimal paidPrice, LocalDateTime purchasedAt)
    {
        this.electronicId = electronicId;
        this.customerId = customerId;
        this.screening = screening;
        this.seat = seat;
        this.paidPrice = paidPrice;
        this.purchasedAt = purchasedAt;
        this.pickedUp = false;
    }

    public String getElectronicId()
    {
        return electronicId;
    }

    public int getCustomerId()
    {
        return customerId;
    }

    public Screening getScreening()
    {
        return screening;
    }

    public Seat getSeat()
    {
        return seat;
    }

    public BigDecimal getPaidPrice()
    {
        return paidPrice;
    }

    public LocalDateTime getPurchasedAt()
    {
        return purchasedAt;
    }

    public boolean isPickedUp()
    {
        return pickedUp;
    }

    /** 将电影票标记为已取票。 */
    public void pickUp()
    {
        if (pickedUp)
        {
            throw new IllegalStateException("电影票已经取出，不能重复取票");
        }
        pickedUp = true;
    }
}
