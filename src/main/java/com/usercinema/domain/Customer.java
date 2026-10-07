package com.usercinema.domain;

import java.math.BigDecimal;

/**
 * 顾客账户及累计购票信息。
 */
public final class Customer
{
    // 会员等级的累计消费和购票数量门槛。
    private static final BigDecimal GOLD_SPENDING_THRESHOLD = new BigDecimal("1000.00");
    private static final int GOLD_TICKET_THRESHOLD = 50;
    private static final BigDecimal SILVER_SPENDING_THRESHOLD = new BigDecimal("300.00");
    private static final int SILVER_TICKET_THRESHOLD = 20;

    private final int id;
    private final String name;
    private MembershipLevel membershipLevel;
    private BigDecimal totalSpent;
    private int ticketCount;

    public Customer(int id, String name, MembershipLevel membershipLevel,
                    BigDecimal totalSpent, int ticketCount)
    {
        this.id = id;
        this.name = name;
        this.membershipLevel = membershipLevel;
        this.totalSpent = totalSpent;
        this.ticketCount = ticketCount;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public MembershipLevel getMembershipLevel()
    {
        return membershipLevel;
    }

    public BigDecimal getTotalSpent()
    {
        return totalSpent;
    }

    public int getTicketCount()
    {
        return ticketCount;
    }

    /** 累加顾客本次购票的金额和票数。 */
    public void recordPurchase(BigDecimal amount, int purchasedTickets)
    {
        if (amount.signum() < 0 || purchasedTickets < 1)
        {
            throw new IllegalArgumentException("购票金额和票数必须有效");
        }
        totalSpent = totalSpent.add(amount);
        ticketCount += purchasedTickets;
    }

    /** 根据累计消费和购票数量更新会员等级。 */
    public void updateMembershipLevel()
    {
        if (totalSpent.compareTo(GOLD_SPENDING_THRESHOLD) >= 0
                || ticketCount >= GOLD_TICKET_THRESHOLD)
        {
            changeMembershipLevel(MembershipLevel.GOLD);
        }
        else if (totalSpent.compareTo(SILVER_SPENDING_THRESHOLD) >= 0
                || ticketCount >= SILVER_TICKET_THRESHOLD)
        {
            changeMembershipLevel(MembershipLevel.SILVER);
        }
    }

    /** 修改顾客的会员等级。 */
    public void changeMembershipLevel(MembershipLevel membershipLevel)
    {
        if (membershipLevel == null)
        {
            throw new IllegalArgumentException("会员等级不能为空");
        }
        this.membershipLevel = membershipLevel;
    }
}
