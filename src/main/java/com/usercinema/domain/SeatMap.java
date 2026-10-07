package com.usercinema.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装座位数组，外部只能通过业务方法读取和修改座位状态。
 */
public final class SeatMap
{
    // 影厅的座位规格。
    public static final int ROW_COUNT = 7;
    public static final int COLUMN_COUNT = 12;

    private final SeatStatus[][] seats;

    public SeatMap()
    {
        seats = new SeatStatus[ROW_COUNT][COLUMN_COUNT];
        for (int row = 0; row < ROW_COUNT; row++)
        {
            for (int column = 0; column < COLUMN_COUNT; column++)
            {
                seats[row][column] = SeatStatus.AVAILABLE;
            }
        }
    }

    public SeatStatus getStatus(Seat seat)
    {
        validate(seat);
        return seats[toIndex(seat.getRow())][toIndex(seat.getColumn())];
    }

    public boolean isAvailable(Seat seat)
    {
        return getStatus(seat) == SeatStatus.AVAILABLE;
    }

    public boolean areAllAvailable(List<Seat> selectedSeats)
    {
        for (Seat seat : selectedSeats)
        {
            if (!isAvailable(seat))
            {
                return false;
            }
        }
        return true;
    }

    /** 将可用座位设为预留状态。 */
    public void reserve(Seat seat)
    {
        validate(seat);
        if (!isAvailable(seat))
        {
            throw new IllegalStateException("座位不可预留: " + seat);
        }
        seats[toIndex(seat.getRow())][toIndex(seat.getColumn())] = SeatStatus.RESERVED;
    }

    /** 释放处于预留状态的座位。 */
    public void release(Seat seat)
    {
        validate(seat);
        if (getStatus(seat) == SeatStatus.RESERVED)
        {
            seats[toIndex(seat.getRow())][toIndex(seat.getColumn())] = SeatStatus.AVAILABLE;
        }
    }

    /** 将已预留座位设为售出状态。 */
    public void markAsSold(Seat seat)
    {
        validate(seat);
        if (getStatus(seat) != SeatStatus.RESERVED)
        {
            throw new IllegalStateException("座位尚未预留: " + seat);
        }
        seats[toIndex(seat.getRow())][toIndex(seat.getColumn())] = SeatStatus.SOLD;
    }

    /** 统计当前仍可购买的座位数量。 */
    public int availableCount()
    {
        int count = 0;
        for (int row = 0; row < ROW_COUNT; row++)
        {
            for (int column = 0; column < COLUMN_COUNT; column++)
            {
                if (seats[row][column] == SeatStatus.AVAILABLE)
                {
                    count++;
                }
            }
        }
        return count;
    }

    /** 生成用于控制台显示的座位图。 */
    public List<String> snapshot()
    {
        List<String> rows = new ArrayList<>();
        for (int row = 0; row < ROW_COUNT; row++)
        {
            StringBuilder line = new StringBuilder();
            for (int column = 0; column < COLUMN_COUNT; column++)
            {
                SeatStatus status = seats[row][column];
                line.append(status == SeatStatus.AVAILABLE ? '0' : 'X');
                if (column < COLUMN_COUNT - 1)
                {
                    line.append(' ');
                }
            }
            rows.add(line.toString());
        }
        return List.copyOf(rows);
    }

    private void validate(Seat seat)
    {
        if (seat == null)
        {
            throw new IllegalArgumentException("座位不能为空");
        }
        if (seat.getRow() < 1 || seat.getRow() > ROW_COUNT)
        {
            throw new IllegalArgumentException("排号必须在 1 到 " + ROW_COUNT + " 之间");
        }
        if (seat.getColumn() < 1 || seat.getColumn() > COLUMN_COUNT)
        {
            throw new IllegalArgumentException("座号必须在 1 到 " + COLUMN_COUNT + " 之间");
        }
    }

    private int toIndex(int number)
    {
        return number - 1;
    }
}
