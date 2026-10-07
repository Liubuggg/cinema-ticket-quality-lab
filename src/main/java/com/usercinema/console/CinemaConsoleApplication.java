package com.usercinema.console;

import com.usercinema.domain.CinemaDay;
import com.usercinema.domain.Film;
import com.usercinema.domain.Screening;
import com.usercinema.domain.Seat;
import com.usercinema.domain.Ticket;
import com.usercinema.repository.InMemoryCinemaRepository;
import com.usercinema.service.PurchaseRequest;
import com.usercinema.service.PurchaseResult;
import com.usercinema.service.TicketPurchaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 电影院管理系统的控制台入口。
 */
public final class CinemaConsoleApplication
{
    private final Scanner scanner;
    private final InMemoryCinemaRepository repository;
    private final TicketPurchaseService purchaseService;

    public CinemaConsoleApplication()
    {
        scanner = new Scanner(System.in);
        repository = new InMemoryCinemaRepository();
        repository.initializeSampleData();
        purchaseService = new TicketPurchaseService(repository);
    }

    public static void main(String[] args)
    {
        CinemaConsoleApplication application = new CinemaConsoleApplication();
        application.run();
    }

    private void run()
    {
        System.out.println("HELLO, WELCOME TO YD CINEMA");
        boolean running = true;
        while (running)
        {
            printMenu();
            String choice = scanner.nextLine().trim();
            try
            {
                switch (choice)
                {
                    case "1" -> outputAllFilms();
                    case "2" -> outputAllScreenings();
                    case "3" -> buyTicket();
                    case "4" -> pickUpTicket();
                    case "5" -> outputHistory();
                    case "0" -> running = false;
                    default -> System.out.println("不存在的功能，请重新选择");
                }
            }
            catch (RuntimeException exception)
            {
                System.out.println("操作失败: " + exception.getMessage());
            }
        }
        System.out.println("感谢使用 YD CINEMA");
    }

    private void printMenu()
    {
        System.out.println();
        System.out.println("1. 查看影片");
        System.out.println("2. 查看场次");
        System.out.println("3. 购买电影票");
        System.out.println("4. 取票");
        System.out.println("5. 查看购票历史");
        System.out.println("0. 退出");
        System.out.print("请选择功能: ");
    }

    private void outputAllFilms()
    {
        for (Film film : repository.findAllFilms())
        {
            System.out.println("片名: " + film.getName());
            System.out.println("导演: " + film.getDirector());
            System.out.println("主演: " + film.getActor());
            System.out.println("剧情简介: " + film.getStory());
            System.out.println("时长: " + film.getDurationMinutes() + " 分钟");
            System.out.println();
        }
    }

    private void outputAllScreenings()
    {
        for (Screening screening : repository.findAllScreenings())
        {
            System.out.println(formatScreening(screening)
                    + "，剩余座位: " + screening.getSeatMap().availableCount());
        }
    }

    private void buyTicket()
    {
        System.out.print("顾客ID: ");
        int customerId = readInt();
        System.out.print("日期代码(Mon/Tue/Wed/Thur/Fri/Sat/Sun): ");
        CinemaDay day = CinemaDay.fromCode(scanner.nextLine().trim());
        System.out.print("影厅编号: ");
        int hallNumber = readInt();
        System.out.print("场次编号(1/2/3): ");
        int timeSlot = readInt();
        System.out.print("购票张数: ");
        int ticketCount = readInt();

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < ticketCount; i++)
        {
            System.out.print("第 " + (i + 1) + " 张票的排号: ");
            int row = readInt();
            System.out.print("第 " + (i + 1) + " 张票的座号: ");
            int column = readInt();
            seats.add(new Seat(row, column));
        }

        System.out.print("支付密码: ");
        String paymentPassword = scanner.nextLine();
        PurchaseRequest request = new PurchaseRequest(
                customerId, day, hallNumber, timeSlot, seats, paymentPassword);
        PurchaseResult result = purchaseService.purchase(request);

        System.out.println(result.getMessage());
        if (result.isSuccess())
        {
            System.out.println("支付金额: " + result.getTotalAmount() + " 元");
            for (Ticket ticket : result.getTickets())
            {
                System.out.println("电子票ID: " + ticket.getElectronicId()
                        + "，座位: " + ticket.getSeat());
            }
        }
    }

    private void pickUpTicket()
    {
        System.out.print("请输入电子票ID: ");
        Ticket ticket = purchaseService.pickUpTicket(scanner.nextLine().trim());
        System.out.println("取票成功");
        System.out.println(formatTicket(ticket));
    }

    private void outputHistory()
    {
        System.out.print("顾客ID: ");
        int customerId = readInt();
        List<Ticket> tickets = purchaseService.findPurchaseHistory(customerId);
        if (tickets.isEmpty())
        {
            System.out.println("暂无购票记录");
            return;
        }
        for (Ticket ticket : tickets)
        {
            System.out.println(formatTicket(ticket));
        }
    }

    private String formatScreening(Screening screening)
    {
        return screening.getDay().getDisplayName()
                + "，" + screening.getHallNumber() + "号厅"
                + "，" + screening.getTimeText()
                + "，" + screening.getFilm().getName()
                + "，票价 " + screening.getPrice() + " 元";
    }

    private String formatTicket(Ticket ticket)
    {
        return "票号: " + ticket.getElectronicId()
                + "，" + formatScreening(ticket.getScreening())
                + "，座位: " + ticket.getSeat()
                + "，实付: " + ticket.getPaidPrice() + " 元"
                + "，是否已取票: " + ticket.isPickedUp();
    }

    private int readInt()
    {
        String value = scanner.nextLine().trim();
        return Integer.parseInt(value);
    }
}
