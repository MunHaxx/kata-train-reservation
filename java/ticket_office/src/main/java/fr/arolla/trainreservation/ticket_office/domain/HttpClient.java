package fr.arolla.trainreservation.ticket_office.domain;

import org.springframework.web.client.RestTemplate;

public class HttpClient implements IHttpClient {

  private final RestTemplate restTemplate;

  public HttpClient() {
    this.restTemplate = new RestTemplate();
  }

  @Override
  public String getBookingReference() {
    return restTemplate.getForObject("http://127.0.0.1:8082/booking_reference", String.class);
  }

  @Override
  public String getTrainData(String trainId) {
    return restTemplate.getForObject("http://127.0.0.1:8081/data_for_train/" + trainId, String.class);
  }

  @Override
  public void postBookingReservation(Object payload) {
    String url = "http://127.0.0.1:8082/booking_reference";
    restTemplate.postForObject(url, payload, String.class);
  }
}
