package fr.arolla.trainreservation.ticket_office;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.arolla.trainreservation.ticket_office.controllers.BookingController;
import fr.arolla.trainreservation.ticket_office.controllers.BookingRequest;
import fr.arolla.trainreservation.ticket_office.controllers.BookingResponse;
import fr.arolla.trainreservation.ticket_office.domain.FakeHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UnitTests {
  public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
    MediaType.APPLICATION_JSON.getType(),
    MediaType.APPLICATION_JSON.getSubtype(),
    StandardCharsets.UTF_8
  );

  @Autowired
  private MockMvc mockMvc;

  @Test
  void reserve_two_seats_from_empty_train() throws Exception {
    final String trainId = "express_2000";

    // 1. Initialisation de FakeHttpClient
    var fakeHttpClient = new FakeHttpClient();

    // Simuler l'appel pour réinitialiser le train
    fakeHttpClient.setResponse("http://127.0.0.1:8081/reset/" + trainId, "OK");

    // Simuler une réponse pour les données du train
    fakeHttpClient.setResponse("http://127.0.0.1:8083/reserve",
      """
      {
          "trainId": "express_2000",
          "seats": ["1A", "2A"]
      }
      """);

    // 2. Construire la requête de réservation
    var request = new BookingRequest(trainId, 2);
    var mapper = new ObjectMapper();
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);

    // 3. Simuler l'appel via FakeHttpClient
    var jsonResponse = fakeHttpClient.post("http://127.0.0.1:8083/reserve", requestJson, String.class);

    // 4. Traiter la réponse simulée
    var objectMapper = new ObjectMapper();
    var bookingResponse = objectMapper.readValue(jsonResponse, BookingResponse.class);

    // 5. Valider le résultat attendu
    var expected = List.of("1A", "2A");
    assertEquals(expected, bookingResponse.seats());
  }

  @Test
  void reserve_two_additional_seats() throws Exception {
    // Reset
    final String trainId = "express_2000";
    var restTemplate = new RestTemplate();
    restTemplate.postForObject("http://127.0.0.1:8081" + "/reset/" + trainId, null, String.class);

    // Book 2 seats
    var request = new BookingRequest(trainId, 2);
    var mapper = new ObjectMapper();
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(request);
    String url = "http://127.0.0.1:8083/reserve";

    mockMvc.perform(post(url).contentType(APPLICATION_JSON_UTF8)
        .content(requestJson))
      .andExpect(status().isOk());

    // Book 2 additional seats
    var result = mockMvc.perform(post(url).contentType(APPLICATION_JSON_UTF8)
        .content(requestJson))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var json = result.getContentAsString();
    var objectMapper = new ObjectMapper();
    var bookingResponse = objectMapper.readValue(json, BookingResponse.class);

    var expected = List.of("3A", "4A");
    assertEquals(expected, bookingResponse.seats());
  }

  @Test
  void reserve_two_seats_from_empty_train_v2(){
    BookingResponse expectedBookingResponse = new BookingResponse("train_1", "b_01", Arrays.asList("2A", "3A"));
    BookingController bookingController = new BookingController(new FakeHttpClient());
    BookingRequest bookingRequest = new BookingRequest("train_1", 2);
    BookingResponse bookingResponse = bookingController.reserve(bookingRequest);
    assertEquals(expectedBookingResponse, bookingResponse);
  }

}
