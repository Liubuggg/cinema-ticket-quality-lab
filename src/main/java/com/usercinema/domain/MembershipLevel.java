package com.usercinema.domain;

import java.math.BigDecimal;

/**
 * 会员等级及对应折扣。
 */
public enum MembershipLevel
{
    BRONZE("铜牌", new BigDecimal("1.00")),
    SILVER("银牌", new BigDecimal("0.95")),
    GOLD("金牌", new BigDecimal("0.88"));

    private final String displayName;
    private final BigDecimal discountRate;

    MembershipLevel(String displayName, BigDecimal discountRate)
    {
        this.displayName = displayName;
        this.discountRate = discountRate;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public BigDecimal getDiscountRate()
    {
        return discountRate;
    }
}
