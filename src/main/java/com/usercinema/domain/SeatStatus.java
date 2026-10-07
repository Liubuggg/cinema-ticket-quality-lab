package com.usercinema.domain;

/**
 * 座位状态，预留用于购票流程中暂时锁定座位。
 */
public enum SeatStatus
{
    AVAILABLE,
    RESERVED,
    SOLD
}
