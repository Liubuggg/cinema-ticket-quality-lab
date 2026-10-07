package com.usercinema.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 座位状态和边界校验的单元测试。 */
class SeatMapTest
{
    @Test
    void shouldInitializeAllEightyFourSeatsAsAvailable()
    {
        SeatMap seatMap = new SeatMap();

        assertEquals(84, seatMap.availableCount());
        assertEquals(SeatStatus.AVAILABLE, seatMap.getStatus(new Seat(1, 1)));
        assertEquals(SeatStatus.AVAILABLE, seatMap.getStatus(new Seat(7, 12)));
    }

    @Test
    void shouldReserveAndSellAvailableSeat()
    {
        SeatMap seatMap = new SeatMap();
        Seat seat = new Seat(3, 5);

        seatMap.reserve(seat);
        assertEquals(SeatStatus.RESERVED, seatMap.getStatus(seat));

        seatMap.markAsSold(seat);
        assertEquals(SeatStatus.SOLD, seatMap.getStatus(seat));
        assertEquals(83, seatMap.availableCount());
    }

    @Test
    void shouldReleaseReservedSeat()
    {
        SeatMap seatMap = new SeatMap();
        Seat seat = new Seat(2, 4);
        seatMap.reserve(seat);

        seatMap.release(seat);

        assertEquals(SeatStatus.AVAILABLE, seatMap.getStatus(seat));
        assertEquals(84, seatMap.availableCount());
    }

    @Test
    void shouldLeaveAvailableSeatUnchangedWhenReleased()
    {
        SeatMap seatMap = new SeatMap();
        Seat seat = new Seat(2, 4);

        seatMap.release(seat);

        assertEquals(SeatStatus.AVAILABLE, seatMap.getStatus(seat));
    }

    @Test
    void shouldRejectReservingUnavailableSeat()
    {
        SeatMap seatMap = new SeatMap();
        Seat seat = new Seat(4, 6);
        seatMap.reserve(seat);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> seatMap.reserve(seat));

        assertEquals("座位不可预留: 4排6座", exception.getMessage());
    }

    @Test
    void shouldRejectSellingSeatThatWasNotReserved()
    {
        SeatMap seatMap = new SeatMap();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> seatMap.markAsSold(new Seat(1, 1)));

        assertEquals("座位尚未预留: 1排1座", exception.getMessage());
    }

    @Test
    void shouldReportWhetherAllSelectedSeatsAreAvailable()
    {
        SeatMap seatMap = new SeatMap();
        Seat unavailable = new Seat(1, 1);
        seatMap.reserve(unavailable);

        assertTrue(seatMap.areAllAvailable(List.of(new Seat(1, 2), new Seat(1, 3))));
        assertFalse(seatMap.areAllAvailable(List.of(new Seat(1, 2), unavailable)));
    }

    @Test
    void shouldRenderAvailableAndUnavailableSeatsInSnapshot()
    {
        SeatMap seatMap = new SeatMap();
        seatMap.reserve(new Seat(1, 1));

        List<String> snapshot = seatMap.snapshot();

        assertEquals(7, snapshot.size());
        assertEquals("X 0 0 0 0 0 0 0 0 0 0 0", snapshot.get(0));
        assertEquals("0 0 0 0 0 0 0 0 0 0 0 0", snapshot.get(6));
        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.add("invalid"));
    }

    @Test
    void shouldRejectNullSeat()
    {
        SeatMap seatMap = new SeatMap();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> seatMap.getStatus(null));

        assertEquals("座位不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectRowBelowMinimum()
    {
        SeatMap seatMap = new SeatMap();

        assertThrows(IllegalArgumentException.class,
                () -> seatMap.getStatus(new Seat(0, 1)));
    }

    @Test
    void shouldRejectRowAboveMaximum()
    {
        SeatMap seatMap = new SeatMap();

        assertThrows(IllegalArgumentException.class,
                () -> seatMap.getStatus(new Seat(8, 1)));
    }

    @Test
    void shouldRejectColumnBelowMinimum()
    {
        SeatMap seatMap = new SeatMap();

        assertThrows(IllegalArgumentException.class,
                () -> seatMap.getStatus(new Seat(1, 0)));
    }

    @Test
    void shouldRejectColumnAboveMaximum()
    {
        SeatMap seatMap = new SeatMap();

        assertThrows(IllegalArgumentException.class,
                () -> seatMap.getStatus(new Seat(1, 13)));
    }
}
