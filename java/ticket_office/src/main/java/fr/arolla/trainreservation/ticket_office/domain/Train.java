package fr.arolla.trainreservation.ticket_office.domain;

import fr.arolla.trainreservation.ticket_office.Seat;

import java.util.List;
import java.util.stream.Stream;

public class Train {

  List<Seat> seats;
  public Stream<Seat> getAvailableSeats(String coach) {
    return seats.stream().filter(seat -> seat.coach().equals(coach) && seat.bookingReference() == null);
  }

  public Train(List<Seat> seats) {
    this.seats = seats;
  }
}
