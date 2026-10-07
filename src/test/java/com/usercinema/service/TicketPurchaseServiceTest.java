package com.usercinema.service;

import com.usercinema.domain.CinemaDay;
import com.usercinema.domain.Customer;
import com.usercinema.domain.Film;
import com.usercinema.domain.MembershipLevel;
import com.usercinema.domain.Screening;
import com.usercinema.domain.Seat;
import com.usercinema.domain.SeatStatus;
import com.usercinema.domain.Ticket;
import com.usercinema.repository.InMemoryCinemaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 购票、会员升级、取票和历史查询的业务测试。 */
class TicketPurchaseServiceTest
{
    private InMemoryCinemaRepository repository;
    private TicketPurchaseService service;

    @BeforeEach
    void setUp()
    {
        repository = new InMemoryCinemaRepository();
        repository.initializeSampleData();
        service = new TicketPurchaseService(repository);
    }

    @Test
    void shouldRejectNullRequest()
    {
        PurchaseResult result = service.purchase(null);

        assertFailure(result, "购票请求不完整");
    }

    @Test
    void shouldRejectRequestWithoutDay()
    {
        PurchaseRequest request = new PurchaseRequest(
                1, null, 1, 1, List.of(new Seat(1, 1)), "123456");

        PurchaseResult result = service.purchase(request);

        assertFailure(result, "购票请求不完整");
    }

    @Test
    void shouldRejectUnknownCustomer()
    {
        PurchaseResult result = service.purchase(requestFor(
                404, CinemaDay.MONDAY, 1, 1,
                List.of(new Seat(1, 1)), "123456"));

        assertFailure(result, "顾客不存在");
    }

    @Test
    void shouldRejectUnknownScreening()
    {
        PurchaseResult result = service.purchase(requestFor(
                1, CinemaDay.MONDAY, 9, 9,
                List.of(new Seat(1, 1)), "123456"));

        assertFailure(result, "没有找到对应场次");
    }

    @Test
    void shouldRejectScreeningThatIsNotOnSale()
    {
        Screening screening = mondayScreening();
        screening.stopSale();

        PurchaseResult result = service.purchase(validRequest(List.of(new Seat(1, 1))));

        assertFailure(result, "场次未开售、座位为空或座位已被占用");
    }

    @Test
    void shouldRejectEmptySeatSelection()
    {
        PurchaseResult result = service.purchase(validRequest(List.of()));

        assertFailure(result, "场次未开售、座位为空或座位已被占用");
    }

    @Test
    void shouldRejectUnavailableSeat()
    {
        Seat unavailable = new Seat(1, 1);
        mondayScreening().getSeatMap().reserve(unavailable);

        PurchaseResult result = service.purchase(validRequest(List.of(unavailable)));

        assertFailure(result, "场次未开售、座位为空或座位已被占用");
    }

    @Test
    void shouldRejectDuplicateSeatSelection()
    {
        Seat duplicated = new Seat(1, 1);

        PurchaseResult result = service.purchase(
                validRequest(List.of(duplicated, duplicated)));

        assertFailure(result, "一次购票请求中不能重复选择同一座位");
    }

    @Test
    void shouldRejectNullPaymentPassword()
    {
        PurchaseResult result = service.purchase(requestFor(
                1, CinemaDay.MONDAY, 1, 1,
                List.of(new Seat(1, 1)), null));

        assertFailure(result, "支付密码不能为空");
    }

    @Test
    void shouldRejectBlankPaymentPassword()
    {
        PurchaseResult result = service.purchase(requestFor(
                1, CinemaDay.MONDAY, 1, 1,
                List.of(new Seat(1, 1)), "   "));

        assertFailure(result, "支付密码不能为空");
    }

    @Test
    void shouldPurchaseOneTicketUsingSilverDiscount()
    {
        Seat selected = new Seat(1, 1);

        PurchaseResult result = service.purchase(validRequest(List.of(selected)));

        assertTrue(result.isSuccess());
        assertEquals("购票成功", result.getMessage());
        assertEquals(new BigDecimal("23.75"), result.getTotalAmount());
        assertEquals(1, result.getTickets().size());
        Ticket ticket = result.getTickets().get(0);
        assertTrue(ticket.getElectronicId().matches("YD\\d{13}"));
        assertEquals(new BigDecimal("23.75"), ticket.getPaidPrice());
        assertEquals(selected, ticket.getSeat());
        assertEquals(SeatStatus.SOLD, mondayScreening().getSeatMap().getStatus(selected));

        Customer customer = repository.findCustomer(1);
        assertEquals(new BigDecimal("54.25"), customer.getTotalSpent());
        assertEquals(2, customer.getTicketCount());
    }

    @Test
    void shouldPurchaseMultipleTicketsAndChargeForEverySeat()
    {
        List<Seat> selected = List.of(new Seat(1, 1), new Seat(1, 2));

        PurchaseResult result = service.purchase(validRequest(selected));

        assertTrue(result.isSuccess());
        assertEquals(new BigDecimal("47.50"), result.getTotalAmount());
        assertEquals(2, result.getTickets().size());
        assertEquals(82, mondayScreening().getSeatMap().availableCount());
    }

    @Test
    void shouldUpgradeToGoldWhenSpendingReachesOneThousand()
    {
        List<Seat> selected = firstSeats(41);

        PurchaseResult result = service.purchase(validRequest(selected));

        assertTrue(result.isSuccess());
        Customer customer = repository.findCustomer(1);
        assertEquals(new BigDecimal("1004.25"), customer.getTotalSpent());
        assertEquals(42, customer.getTicketCount());
        assertEquals(MembershipLevel.GOLD, customer.getMembershipLevel());
    }

    @Test
    void shouldUpgradeToGoldAtExactSpendingBoundary()
    {
        Customer customer = repository.findCustomer(1);
        customer.recordPurchase(new BigDecimal("968.55"), 1);
        addLowPriceScreening();

        PurchaseResult result = service.purchase(requestFor(
                1, CinemaDay.TUESDAY, 9, 9,
                List.of(new Seat(1, 1)), "123456"));

        assertTrue(result.isSuccess());
        assertEquals(new BigDecimal("1000.00"), customer.getTotalSpent());
        assertEquals(MembershipLevel.GOLD, customer.getMembershipLevel());
    }

    @Test
    void shouldUpgradeToSilverAtExactSpendingBoundary()
    {
        Customer customer = repository.findCustomer(1);
        customer.recordPurchase(new BigDecimal("268.50"), 1);
        customer.changeMembershipLevel(MembershipLevel.BRONZE);
        addLowPriceScreening();

        PurchaseResult result = service.purchase(requestFor(
                1, CinemaDay.TUESDAY, 9, 9,
                List.of(new Seat(1, 1)), "123456"));

        assertTrue(result.isSuccess());
        assertEquals(new BigDecimal("300.00"), customer.getTotalSpent());
        assertEquals(MembershipLevel.SILVER, customer.getMembershipLevel());
    }

    @Test
    void shouldUpgradeToSilverAtExactTicketBoundary()
    {
        Customer customer = repository.findCustomer(1);
        customer.recordPurchase(BigDecimal.ZERO, 18);
        customer.changeMembershipLevel(MembershipLevel.BRONZE);
        addLowPriceScreening();

        PurchaseResult result = service.purchase(requestFor(
                1, CinemaDay.TUESDAY, 9, 9,
                List.of(new Seat(1, 1)), "123456"));

        assertTrue(result.isSuccess());
        assertEquals(20, customer.getTicketCount());
        assertEquals(MembershipLevel.SILVER, customer.getMembershipLevel());
    }

    @Test
    void shouldUpgradeToGoldWhenTicketCountReachesFifty()
    {
        addLowPriceScreening();
        List<Seat> selected = firstSeats(49);
        PurchaseRequest request = requestFor(
                1, CinemaDay.TUESDAY, 9, 9, selected, "123456");

        PurchaseResult result = service.purchase(request);

        assertTrue(result.isSuccess());
        Customer customer = repository.findCustomer(1);
        assertEquals(new BigDecimal("77.05"), customer.getTotalSpent());
        assertEquals(50, customer.getTicketCount());
        assertEquals(MembershipLevel.GOLD, customer.getMembershipLevel());
    }

    @Test
    void shouldPickUpPurchasedTicketOnlyOnce()
    {
        PurchaseResult purchase = service.purchase(
                validRequest(List.of(new Seat(1, 1))));
        String electronicId = purchase.getTickets().get(0).getElectronicId();

        Ticket pickedUp = service.pickUpTicket(electronicId);

        assertTrue(pickedUp.isPickedUp());
        assertThrows(IllegalStateException.class,
                () -> service.pickUpTicket(electronicId));
    }

    @Test
    void shouldRejectUnknownElectronicTicketId()
    {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.pickUpTicket("YD0000000000000"));

        assertEquals("电影票不存在: YD0000000000000", exception.getMessage());
    }

    @Test
    void shouldReturnPurchaseHistoryForKnownCustomer()
    {
        service.purchase(validRequest(List.of(new Seat(1, 1))));

        List<Ticket> history = service.findPurchaseHistory(1);

        assertEquals(1, history.size());
        assertEquals(1, history.get(0).getCustomerId());
    }

    @Test
    void shouldRejectPurchaseHistoryForUnknownCustomer()
    {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.findPurchaseHistory(404));

        assertEquals("顾客不存在: 404", exception.getMessage());
    }

    private Screening mondayScreening()
    {
        return repository.findScreening(CinemaDay.MONDAY, 1, 1);
    }

    private PurchaseRequest validRequest(List<Seat> seats)
    {
        return requestFor(1, CinemaDay.MONDAY, 1, 1, seats, "123456");
    }

    private PurchaseRequest requestFor(int customerId, CinemaDay day,
                                       int hallNumber, int timeSlot,
                                       List<Seat> seats, String password)
    {
        return new PurchaseRequest(customerId, day, hallNumber, timeSlot,
                seats, password);
    }

    private void addLowPriceScreening()
    {
        Screening screening = new Screening(
                CinemaDay.TUESDAY, 9, 9, "测试场次",
                new Film("测试影片", "导演", "演员", "简介", 90),
                new BigDecimal("1.00"));
        repository.addScreening(screening);
    }

    private List<Seat> firstSeats(int count)
    {
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= 7 && seats.size() < count; row++)
        {
            for (int column = 1; column <= 12 && seats.size() < count; column++)
            {
                seats.add(new Seat(row, column));
            }
        }
        return seats;
    }

    private void assertFailure(PurchaseResult result, String expectedMessage)
    {
        assertFalse(result.isSuccess());
        assertEquals(expectedMessage, result.getMessage());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertTrue(result.getTickets().isEmpty());
    }
}
