package com.usercinema.repository;

import com.usercinema.domain.CinemaDay;
import com.usercinema.domain.Customer;
import com.usercinema.domain.Film;
import com.usercinema.domain.MembershipLevel;
import com.usercinema.domain.Screening;
import com.usercinema.domain.Ticket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 在内存中保存影片、场次、顾客和电影票数据。
 */
public final class InMemoryCinemaRepository
{
    // 系统运行期间使用的内存数据集合。
    private final Map<String, Film> films = new LinkedHashMap<>();
    private final Map<Integer, Customer> customers = new LinkedHashMap<>();
    private final Map<CinemaDay, List<Screening>> schedules = new EnumMap<>(CinemaDay.class);
    private final Map<String, Ticket> tickets = new LinkedHashMap<>();

    public InMemoryCinemaRepository()
    {
        for (CinemaDay day : CinemaDay.values())
        {
            schedules.put(day, new ArrayList<>());
        }
    }

    /** 创建控制台演示所需的初始数据。 */
    public void initializeSampleData()
    {
        Film operationRedSea = new Film(
                "红海行动", "林超贤", "张译",
                "蛟龙突击队执行撤侨和人质营救任务", 138);
        Film homeComing = new Film(
                "万里归途", "饶晓志", "张译",
                "外交官返回战区，带领被困同胞回家", 137);
        Film detectiveChinatown = new Film(
                "唐人街探案3", "陈思诚", "王宝强",
                "唐仁和秦风前往东京调查谋杀案", 136);

        addFilm(operationRedSea);
        addFilm(homeComing);
        addFilm(detectiveChinatown);

        addScreening(new Screening(CinemaDay.MONDAY, 1, 1, "8-11时",
                operationRedSea, new BigDecimal("25.00")));
        addScreening(new Screening(CinemaDay.WEDNESDAY, 1, 2, "14-17时",
                homeComing, new BigDecimal("30.00")));
        addScreening(new Screening(CinemaDay.WEDNESDAY, 3, 2, "14-17时",
                operationRedSea, new BigDecimal("35.00")));

        Customer customer = new Customer(1, "qwert", MembershipLevel.SILVER,
                new BigDecimal("30.50"), 1);
        customers.put(customer.getId(), customer);
    }

    public void addFilm(Film film)
    {
        films.put(film.getName(), film);
    }

    public List<Film> findAllFilms()
    {
        return List.copyOf(films.values());
    }

    public void addScreening(Screening screening)
    {
        schedules.get(screening.getDay()).add(screening);
    }

    public List<Screening> findAllScreenings()
    {
        List<Screening> result = new ArrayList<>();
        for (CinemaDay day : CinemaDay.values())
        {
            result.addAll(schedules.get(day));
        }
        return List.copyOf(result);
    }

    public Screening findScreening(CinemaDay day, int hallNumber, int timeSlot)
    {
        for (Screening screening : schedules.get(day))
        {
            if (screening.getHallNumber() == hallNumber && screening.getTimeSlot() == timeSlot)
            {
                return screening;
            }
        }
        return null;
    }

    public Customer findCustomer(int customerId)
    {
        return customers.get(customerId);
    }

    public void saveTicket(Ticket ticket)
    {
        tickets.put(ticket.getElectronicId(), ticket);
    }

    /** 保存一次购票生成的全部电影票。 */
    public void saveTickets(List<Ticket> newTickets)
    {
        for (Ticket ticket : newTickets)
        {
            saveTicket(ticket);
        }
    }

    public Ticket findTicket(String electronicId)
    {
        return tickets.get(electronicId);
    }

    public List<Ticket> findTicketsByCustomer(int customerId)
    {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets.values())
        {
            if (ticket.getCustomerId() == customerId)
            {
                result.add(ticket);
            }
        }
        return List.copyOf(result);
    }
}
