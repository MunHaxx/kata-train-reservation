package fr.arolla.trainreservation.ticket_office.controllers;

import fr.arolla.trainreservation.ticket_office.Seat;
import fr.arolla.trainreservation.ticket_office.domain.BookingService;
import fr.arolla.trainreservation.ticket_office.domain.HttpClient;
import fr.arolla.trainreservation.ticket_office.domain.Train;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
public class BookingController {

  private final BookingService bookingService;
public BookingController(final BookingService bookingService) {
    this.bookingService = bookingService;
  }
  public BookingController() {
    this.bookingService = new HttpClient();
  }


  @VisibleForTesting
  @RequestMapping("/reserve")
  public BookingResponse reserve(@RequestBody BookingRequest bookingRequest) {
    int seatCount = bookingRequest.count();
    String trainId = bookingRequest.train_id();

    // Step 1: Get a booking reference
    String bookingReference = bookingService.getBookingReference();

    // Step 2: Retrieve train data for the given train ID
    Train train = bookingService.getTrainData(trainId);

    // Step 3: find available seats (hard-code coach 'A' for now)
    Stream<Seat> availableSeats = train.getAvailableSeats("A");

    // Step 4: call the '/reserve' end point
    Stream<Seat> toReserve = availableSeats.limit(seatCount);
    List<String> ids = toReserve.map(seat -> seat.number() + seat.coach()).toList();

    bookingService.postBookingReservation(trainId, ids, bookingReference);

    // Step 5: return reference and booked seats
    return new BookingResponse(trainId, bookingReference, ids);
  }
}
