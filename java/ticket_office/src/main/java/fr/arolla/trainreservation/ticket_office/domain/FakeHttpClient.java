package fr.arolla.trainreservation.ticket_office.domain;

public class FakeHttpClient implements IHttpClient {


  @Override
  public String getBookingReference() {
    return "";
  }

  @Override
  public String getTrainData(String trainId) {
    return "";
  }

  @Override
  public void postBookingReservation(Object payload) {
  }
}
