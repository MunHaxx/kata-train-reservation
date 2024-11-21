package fr.arolla.trainreservation.ticket_office.domain;


import java.util.List;

public interface BookingService {

  String getBookingReference();
  Train getTrainData(String trainId);
  void postBookingReservation(String trainId, List<String> seat_ids, String bookingReference);
}
