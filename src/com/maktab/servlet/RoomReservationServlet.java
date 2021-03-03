package com.maktab.servlet;

import com.maktab.filter.SystemFilter;
import com.maktab.java.Date;
import com.maktab.java.RoomReservation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;

@WebServlet(name = "RoomReservationServlet")
public class RoomReservationServlet extends HttpServlet {
    private ArrayList<RoomReservation> rooms = new ArrayList<>();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.print("<html><body>");
        String option = request.getParameter("mainForm");
        switch (option) {
            case "reserve a room":
                reserveRoom(request, writer);
                request.getRequestDispatcher("roomReservationSystem.html").include(request, response);
                break;
            case "change reserve info":
                changeReserve(request, writer);
                request.getRequestDispatcher("roomReservationSystem.html").include(request, response);
                break;
            case "see reserve(s) info":
                break;
            case "cancel reserve":
                break;
        }
        writer.print("</body></html>");
        writer.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void reserveRoom(HttpServletRequest request, PrintWriter writer) {
        String[] splitStartDate = request.getParameter("startDate1").split("/");
        String[] splitEndDate = request.getParameter("endDate1").split("/");
        Date sDate = new Date(Integer.parseInt(splitStartDate[0]), Integer.parseInt(splitStartDate[1]), Integer.parseInt(splitStartDate[2]));
        Date eDate = new Date(Integer.parseInt(splitEndDate[0]), Integer.parseInt(splitEndDate[1]), Integer.parseInt(splitEndDate[2]));
        RoomReservation room = new RoomReservation(
                request.getParameter("fullName1"),
                request.getParameter("nationalCode1"),
                sDate,
                eDate,
                Integer.parseInt(request.getParameter("roomCapacity1"))
        );
        rooms.add(room);
        writer.println("<div>" +
                "<h3> The room was reserved for you successfully</h3>" +
                "<h3> Room number = " + room.getRoomNumber() + "</h3>" +
                "<h3> Reserve code = " + room.getReserveCode() + "</h3>" +
                "</div>");
    }

    private void changeReserve(HttpServletRequest request, PrintWriter writer) {
        int reserveCode = Integer.parseInt(request.getParameter("reserveCode2"));
        String startDate = request.getParameter("startDate2");
        String endDate = request.getParameter("endDate2");
        int roomCapacity = Integer.parseInt(request.getParameter("roomCapacity2"));
        Optional<RoomReservation> optionalRoom = getRoomByReserveCode(reserveCode);
        if (optionalRoom.isPresent()) {
            RoomReservation room = optionalRoom.get();
            Date sDate = SystemFilter.convertStringDateToObjectDate(startDate);
            Date eDate = SystemFilter.convertStringDateToObjectDate(endDate);
            room.setStartDate(sDate);
            room.setEndDate(eDate);
            room.setRoomCapacity(roomCapacity);
            writer.println("<div>" +
                    "<h3> Changes saved successfully</h3>" +
                    "</div>");
        }
        else {
            writer.println("<div>" +
                    "<h3> !Error: Room with reservation code " + reserveCode + "not found" + "</h3>" +
                    "</div>");
        }
    }

    private Optional<RoomReservation> getRoomByReserveCode(int reserveCode) {
        return rooms.stream().filter(room -> room.getReserveCode() == reserveCode).findFirst();
    }
}
