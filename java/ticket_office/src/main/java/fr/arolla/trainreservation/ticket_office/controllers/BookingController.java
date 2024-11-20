package fr.arolla.trainreservation.ticket_office.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.arolla.trainreservation.ticket_office.Seat;
import fr.arolla.trainreservation.ticket_office.domain.HttpClient;
import fr.arolla.trainreservation.ticket_office.domain.IHttpClient;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BookingController {

  private final IHttpClient httpClient;
public BookingController(final IHttpClient httpClient) {
    this.httpClient = httpClient;
  }
  public BookingController() {
    this.httpClient = new HttpClient();
  }


  @VisibleForTesting
  @RequestMapping("/reserve")
  public BookingResponse reserve(@RequestBody BookingRequest bookingRequest) {
    var seatCount = bookingRequest.count();
    var trainId = bookingRequest.train_id();

    // Step 1: Get a booking reference
    var bookingReference = httpClient.getBookingReference();
    // Step 2: Retrieve train data for the given train ID
    var json = httpClient.getTrainData(trainId);
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
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    // Step 3: find available seats (hard-code coach 'A' for now)
    var availableSeats = seats.stream().filter(seat -> seat.coach().equals("A") && seat.bookingReference() == null);

    // Step 4: call the '/reserve' end point
    var toReserve = availableSeats.limit(seatCount);
    var ids = toReserve.map(seat -> seat.number() + seat.coach()).toList();

    Map<String, Object> payload = new HashMap<>();
    payload.put("train_id", trainId);
    payload.put("seats", ids);
    payload.put("booking_reference", bookingReference);
    httpClient.postBookingReservation(payload);

    // Step 5: return reference and booked seats
    return new BookingResponse(trainId, bookingReference, ids);
  }
}
