package com.usercinema.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** 顾客累计购票信息的单元测试。 */
class CustomerTest
{
    @Test
    void shouldRecordValidPurchase()
    {
        Customer customer = customerWith(new BigDecimal("100.00"), 2);

        customer.recordPurchase(new BigDecimal("47.50"), 2);

        assertEquals(new BigDecimal("147.50"), customer.getTotalSpent());
        assertEquals(4, customer.getTicketCount());
    }

    @Test
    void shouldAllowZeroAmountForAComplimentaryTicket()
    {
        Customer customer = customerWith(new BigDecimal("100.00"), 2);

        customer.recordPurchase(BigDecimal.ZERO, 1);

        assertEquals(new BigDecimal("100.00"), customer.getTotalSpent());
        assertEquals(3, customer.getTicketCount());
        assertEquals("测试顾客", customer.getName());
    }

    @Test
    void shouldRejectNegativePurchaseAmount()
    {
        Customer customer = customerWith(BigDecimal.ZERO, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customer.recordPurchase(new BigDecimal("-0.01"), 1));

        assertEquals("购票金额和票数必须有效", exception.getMessage());
        assertEquals(BigDecimal.ZERO, customer.getTotalSpent());
        assertEquals(0, customer.getTicketCount());
    }

    @Test
    void shouldRejectPurchaseWithZeroTickets()
    {
        Customer customer = customerWith(BigDecimal.ZERO, 0);

        assertThrows(IllegalArgumentException.class,
                () -> customer.recordPurchase(new BigDecimal("10.00"), 0));

        assertEquals(BigDecimal.ZERO, customer.getTotalSpent());
        assertEquals(0, customer.getTicketCount());
    }

    @Test
    void shouldChangeMembershipLevel()
    {
        Customer customer = customerWith(BigDecimal.ZERO, 0);

        customer.changeMembershipLevel(MembershipLevel.SILVER);

        assertEquals(MembershipLevel.SILVER, customer.getMembershipLevel());
    }

    @Test
    void shouldRejectNullMembershipLevel()
    {
        Customer customer = customerWith(BigDecimal.ZERO, 0);

        assertThrows(IllegalArgumentException.class,
                () -> customer.changeMembershipLevel(null));

        assertEquals(MembershipLevel.BRONZE, customer.getMembershipLevel());
    }

    private Customer customerWith(BigDecimal totalSpent, int ticketCount)
    {
        return new Customer(7, "测试顾客", MembershipLevel.BRONZE,
                totalSpent, ticketCount);
    }
}
