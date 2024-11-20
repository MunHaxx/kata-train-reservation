package fr.arolla.trainreservation.ticket_office.domain;

import java.util.HashMap;
import java.util.Map;

public class FakeHttpClient implements IHttpClient {

  private final Map<String, String> responses = new HashMap<>();
  @Override
  public String getBookingReference() {
    return "b_01";
  }

  @Override
  public String getTrainData(String trainId) {
    return "";
  }

  @Override
  public void postBookingReservation(Object payload) {
  }

  public void setResponse(String url, String response) {
    responses.put(url, response);
  }
  public String post(String url, Object payload, Class<?> responseType) {
    // Simule une réponse en fonction de l'URL
    return responses.getOrDefault(url, "");
  }
}
