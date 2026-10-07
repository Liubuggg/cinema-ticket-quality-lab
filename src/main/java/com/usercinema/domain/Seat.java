package com.usercinema.domain;

import java.util.Objects;

/**
 * 座位坐标使用从 1 开始的排号和座号，与用户输入保持一致。
 */
public final class Seat
{
    private final int row;
    private final int column;

    public Seat(int row, int column)
    {
        this.row = row;
        this.column = column;
    }

    public int getRow()
    {
        return row;
    }

    public int getColumn()
    {
        return column;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof Seat seat))
        {
            return false;
        }
        return row == seat.row && column == seat.column;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(row, column);
    }

    @Override
    public String toString()
    {
        return row + "排" + column + "座";
    }
}
