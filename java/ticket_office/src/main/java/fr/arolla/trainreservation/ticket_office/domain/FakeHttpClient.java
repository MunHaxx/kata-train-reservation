package fr.arolla.trainreservation.ticket_office.domain;

import fr.arolla.trainreservation.ticket_office.Seat;

import java.util.*;

public class FakeHttpClient implements BookingService {

  private final Map<String, String> responses = new HashMap<>();
  @Override
  public String getBookingReference() {
    return "b_01";
  }

  @Override
  public Train getTrainData(String trainId) {
    List<Seat> seats = Arrays.asList(
      new Seat("1", "A", "b_01"),
      new Seat("2", "A", null),
      new Seat("3", "A", null),
      new Seat("1", "B", "b_01")
    );
    return new Train(seats);
  }

  @Override
  public void postBookingReservation(String trainId, List<String> seat_ids, String bookingReference) {

  }

  public void setResponse(String url, String response) {
    responses.put(url, response);
  }
  public String post(String url, Object payload, Class<?> responseType) {
    // Simule une réponse en fonction de l'URL
    return responses.getOrDefault(url, "");
  }
}
