package edu.poly.boatbooking.service.impl;

import edu.poly.boatbooking.dto.ScheduleDto;
import edu.poly.boatbooking.entity.Boat;
import edu.poly.boatbooking.entity.Schedule;
import edu.poly.boatbooking.exception.ResourceNotFoundException;
import edu.poly.boatbooking.mapper.BoatMapper;
import edu.poly.boatbooking.mapper.ScheduleMapper;
import edu.poly.boatbooking.repository.ScheduleRepository;
import edu.poly.boatbooking.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {this.scheduleRepository = scheduleRepository;}

    @Override
    public ScheduleDto createSchedule(ScheduleDto scheduleDto) {
        Schedule schedule = ScheduleMapper.mapToSchedule(scheduleDto);
        if (!scheduleRepository.existsById(schedule.getId())) {
            Schedule saveSchedule = scheduleRepository.save(schedule);
            return ScheduleMapper.mapToScheduleDto(saveSchedule);
        } else {
            return null;
        }
    }

    @Override
    public ScheduleDto getScheduleById(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Schedule is not exists with given id: " + scheduleId));
        return ScheduleMapper.mapToScheduleDto(schedule);
    }

    @Override
    public List<ScheduleDto> getAllSchedule() {
        List<Schedule> boats = scheduleRepository.findAll();
        return boats.stream().map((boat) -> ScheduleMapper.mapToScheduleDto(boat))
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDto updatedSchedule(Long scheduleId, ScheduleDto updatedSchedule) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResourceNotFoundException("Schedule is not exists with given id: " + scheduleId)
        );

        schedule.setBoat_name(updatedSchedule.getBoat_name());
        schedule.setPort_name(updatedSchedule.getPort_name());
        schedule.setFromLocation(updatedSchedule.getFromLocation());
        schedule.setToLocation(updatedSchedule.getToLocation());
        schedule.setDepartureDay(updatedSchedule.getDepartureDay());
        schedule.setDepartureTime(updatedSchedule.getDepartureTime());
        schedule.setPrice(updatedSchedule.getPrice());
        schedule.setArrivalTime(updatedSchedule.getArrivalTime());


        Schedule updatedScheduleObj = scheduleRepository.save(schedule);
        return ScheduleMapper.mapToScheduleDto(updatedScheduleObj);
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        Schedule boat = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResourceNotFoundException("Schedule is not exists with given id: " + scheduleId)
        );

        scheduleRepository.deleteById(scheduleId);
    }

    @Override
    public List<Schedule> findSchedule(String fromLocation, String toLocation, String departureDay) {
            List<Schedule> flights;
            flights = scheduleRepository.findByFromLocationAndToLocationAndDepartureDay(fromLocation, toLocation, departureDay);
            return flights;
    }

    @Override
    public List<Schedule> searchTime(String fromLocation, String toLocation, String departureTime, String arrivalTime, String departureDay, String boatName) {
        List<Schedule> flights = new ArrayList<>();
        List<Schedule> details = scheduleRepository.findByFromLocationAndToLocationAndDepartureDay(fromLocation, toLocation, departureDay);
        if (departureTime.isEmpty() && arrivalTime.isEmpty() && !boatName.isEmpty()){
            for (Schedule detail : details) {
                if (detail.getBoat_name().equals(boatName)) {
                    flights.add(detail);
                }
            }
            return flights;
        }
        if (!departureTime.isEmpty() && !arrivalTime.isEmpty()){
            int dep = Integer.parseInt(departureTime.substring(0,2));
            int arr = Integer.parseInt(arrivalTime.substring(0,2));
            if (boatName.isEmpty()) {
                for (Schedule detail : details) {
                    if (Integer.parseInt(detail.getDepartureTime().substring(0,2)) > dep &&
                            Integer.parseInt(detail.getDepartureTime().substring(0,2)) < arr) {
                        flights.add(detail);
                    }
                }
                return flights;
            }
            else {
                for (Schedule detail : details) {
                    if (Integer.parseInt(detail.getDepartureTime().substring(0,2)) > dep &&
                            Integer.parseInt(detail.getDepartureTime().substring(0,2)) < arr &&
                            detail.getBoat_name().equals(boatName)) {
                        flights.add(detail);
                    }
                }
                return flights;
            }
        }
        else {
            flights = scheduleRepository.findByFromLocationAndToLocationAndDepartureDay(fromLocation, toLocation, departureDay);
            return flights;
        }
    }
}
