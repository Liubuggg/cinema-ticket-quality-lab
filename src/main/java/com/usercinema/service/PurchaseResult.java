package com.usercinema.service;

import com.usercinema.domain.Ticket;

import java.math.BigDecimal;
import java.util.List;

/**
 * 保存购票操作的结果信息。
 */
public final class PurchaseResult
{
    private final boolean success;
    private final String message;
    private final BigDecimal totalAmount;
    private final List<Ticket> tickets;

    private PurchaseResult(boolean success, String message,
                           BigDecimal totalAmount, List<Ticket> tickets)
    {
        this.success = success;
        this.message = message;
        this.totalAmount = totalAmount;
        this.tickets = List.copyOf(tickets);
    }

    public static PurchaseResult success(BigDecimal totalAmount, List<Ticket> tickets)
    {
        return new PurchaseResult(true, "购票成功", totalAmount, tickets);
    }

    public static PurchaseResult failure(String message)
    {
        return new PurchaseResult(false, message, BigDecimal.ZERO, List.of());
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getMessage()
    {
        return message;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public List<Ticket> getTickets()
    {
        return tickets;
    }
}
