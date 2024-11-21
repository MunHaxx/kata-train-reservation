package fr.arolla.trainreservation.ticket_office.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.arolla.trainreservation.ticket_office.Seat;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClient implements BookingService {

  private final RestTemplate restTemplate;

  public HttpClient() {
    this.restTemplate = new RestTemplate();
  }

  @Override
  public String getBookingReference() {
    return restTemplate.getForObject("http://127.0.0.1:8082/booking_reference", String.class);
  }

  @Override
  public Train getTrainData(String trainId) {
    var json = restTemplate.getForObject("http://127.0.0.1:8081/data_for_train/" + trainId, String.class);
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayList<Seat> seats = new ArrayList<>();
    try {
      var tree = objectMapper.readTree(json);
      var seatsNode = tree.get("seats");
      for (JsonNode node : seatsNode) {
        String coach = node.get("coach").asText();
        String seatNumber = node.get("seat_number").asText();
        var jsonBookingReference = node.get("booking_reference").asText();
        if (jsonBookingReference.isEmpty()) {
          var seat = new Seat(seatNumber, coach, null);
          seats.add(seat);
        } else {
          var seat = new Seat(seatNumber, coach, jsonBookingReference);
          seats.add(seat);
        }
      }
      return new Train(seats);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void postBookingReservation(String trainId, List<String> seats, String bookingReference) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("train_id", trainId);
    payload.put("seats", seats);
    payload.put("booking_reference", bookingReference);
    String url = "http://127.0.0.1:8081/reserve";
    restTemplate.postForObject(url, payload, String.class);
  }
}
