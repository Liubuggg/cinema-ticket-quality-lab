package com.usercinema.service;

import com.usercinema.domain.Customer;
import com.usercinema.domain.MembershipLevel;
import com.usercinema.domain.Screening;
import com.usercinema.domain.Seat;
import com.usercinema.domain.Ticket;
import com.usercinema.repository.InMemoryCinemaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 处理购票、取票和购票记录查询。
 */
public final class TicketPurchaseService
{
    // 会员等级的累计消费和购票数量门槛。
    private static final BigDecimal GOLD_SPENDING_THRESHOLD = new BigDecimal("1000.00");
    private static final int GOLD_TICKET_THRESHOLD = 50;
    private static final BigDecimal SILVER_SPENDING_THRESHOLD = new BigDecimal("300.00");
    private static final int SILVER_TICKET_THRESHOLD = 20;

    private final InMemoryCinemaRepository repository;
    private final Random random;

    public TicketPurchaseService(InMemoryCinemaRepository repository)
    {
        this.repository = repository;
        this.random = new Random();
    }

    /** 校验购票请求并生成电影票。 */
    public PurchaseResult purchase(PurchaseRequest request)
    {
        if (request == null || request.getDay() == null)
        {
            return PurchaseResult.failure("购票请求不完整");
        }

        Customer customer = repository.findCustomer(request.getCustomerId());
        if (customer == null)
        {
            return PurchaseResult.failure("顾客不存在");
        }

        Screening screening = repository.findScreening(
                request.getDay(), request.getHallNumber(), request.getTimeSlot());
        if (screening == null)
        {
            return PurchaseResult.failure("没有找到对应场次");
        }

        List<Seat> selectedSeats = request.getSeats();
        if (!canEnterPayment(screening, selectedSeats))
        {
            return PurchaseResult.failure("场次未开售、座位为空或座位已被占用");
        }

        if (containsDuplicateSeat(selectedSeats))
        {
            return PurchaseResult.failure("一次购票请求中不能重复选择同一座位");
        }

        if (request.getPaymentPassword() == null || request.getPaymentPassword().isBlank())
        {
            return PurchaseResult.failure("支付密码不能为空");
        }

        for (Seat seat : selectedSeats)
        {
            screening.getSeatMap().reserve(seat);
        }

        BigDecimal unitPrice = screening.getPrice()
                .multiply(customer.getMembershipLevel().getDiscountRate())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(selectedSeats.size()));

        List<Ticket> tickets = new ArrayList<>();
        for (Seat seat : selectedSeats)
        {
            Ticket ticket = new Ticket(
                    generateElectronicId(),
                    customer.getId(),
                    screening,
                    seat,
                    unitPrice,
                    LocalDateTime.now());
            screening.getSeatMap().markAsSold(seat);
            repository.saveTicket(ticket);
            tickets.add(ticket);
        }

        customer.recordPurchase(totalAmount, selectedSeats.size());
        updateMembershipLevel(customer);
        return PurchaseResult.success(totalAmount, tickets);
    }

    /** 根据电子票号完成取票。 */
    public Ticket pickUpTicket(String electronicId)
    {
        Ticket ticket = repository.findTicket(electronicId);
        if (ticket == null)
        {
            throw new IllegalArgumentException("电影票不存在: " + electronicId);
        }
        ticket.pickUp();
        return ticket;
    }

    /** 查询指定顾客的购票记录。 */
    public List<Ticket> findPurchaseHistory(int customerId)
    {
        if (repository.findCustomer(customerId) == null)
        {
            throw new IllegalArgumentException("顾客不存在: " + customerId);
        }
        return repository.findTicketsByCustomer(customerId);
    }

    private boolean canEnterPayment(Screening screening, List<Seat> selectedSeats)
    {
        return screening.isOnSale()
                && !selectedSeats.isEmpty()
                && screening.getSeatMap().areAllAvailable(selectedSeats);
    }

    private boolean containsDuplicateSeat(List<Seat> selectedSeats)
    {
        Set<Seat> uniqueSeats = new HashSet<>(selectedSeats);
        return uniqueSeats.size() != selectedSeats.size();
    }

    private void updateMembershipLevel(Customer customer)
    {
        if (customer.getTotalSpent().compareTo(GOLD_SPENDING_THRESHOLD) >= 0
                || customer.getTicketCount() >= GOLD_TICKET_THRESHOLD)
        {
            customer.changeMembershipLevel(MembershipLevel.GOLD);
        }
        else if (customer.getTotalSpent().compareTo(SILVER_SPENDING_THRESHOLD) >= 0
                || customer.getTicketCount() >= SILVER_TICKET_THRESHOLD)
        {
            customer.changeMembershipLevel(MembershipLevel.SILVER);
        }
    }

    private String generateElectronicId()
    {
        StringBuilder builder = new StringBuilder("YD");
        for (int i = 0; i < 13; i++)
        {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
