package edu.poly.boatbooking.service;

import edu.poly.boatbooking.dto.ScheduleDto;
import edu.poly.boatbooking.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleService {
    ScheduleDto createSchedule(ScheduleDto scheduleDto);

    ScheduleDto getScheduleById(Long scheduleId);

    List<ScheduleDto> getAllSchedule();

    ScheduleDto updatedSchedule(Long scheduleId, ScheduleDto updatedSchedule);

    void deleteSchedule(Long scheduleId);

    List<Schedule> findSchedule(String fromLocation, String toLocation, String departureDay);
    List<Schedule> searchTime(String fromLocation, String toLocation,String departureTime, String arrivalTime, String departureDay, String boatName);
}
