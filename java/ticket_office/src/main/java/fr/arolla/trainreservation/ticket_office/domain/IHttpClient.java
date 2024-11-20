package fr.arolla.trainreservation.ticket_office.domain;

public interface IHttpClient {

  public String getBookingReference();
  public String getTrainData(String trainId);
  public void postBookingReservation(Object payload);
}
