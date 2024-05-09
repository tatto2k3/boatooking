package edu.poly.boatbooking.service;

import edu.poly.boatbooking.dto.ScheduleDto;
import edu.poly.boatbooking.dto.TicketDto;
import edu.poly.boatbooking.dto.TicketReviewDto;
import edu.poly.boatbooking.entity.Ticket;

import java.util.List;

public interface TicketService {
    TicketDto createTicket(TicketDto ticketDto);

    TicketDto getTicketById(Long ticketId);

    List<TicketDto> getAllTicket();

    TicketDto updatedTicket(Long ticketId, TicketDto updatedTicket);

    void deleteTicket(Long ticketId);

    TicketDto findByC_id(Long cId);
    List<Ticket> findSeatIdBySId(Long sId);

    TicketReviewDto ticketReview(String numId, String fromLocation, String toLocation, String departureDay);
}
