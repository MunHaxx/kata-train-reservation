from requests import Session
import json


from flask import Flask, request

from python.ticket_office.real_external_services import BookingReferenceService, TrainDataService
from python.ticket_office.reservation_service import ReservationService


def create_app():
    session = Session()
    booking_reference_service = BookingReferenceService(session)
    train_data_service = TrainDataService(session)
    reservation_service = ReservationService(booking_reference_service, train_data_service)
    
    app = Flask("ticket_office")

    @app.post("/reserve")
    def reserve():
        payload = request.json
        seat_count = payload["count"]
        train_id = payload["train_id"]

        reservation = reservation_service.reserve(seat_count, train_id)

        response = train_data_service.post_reservation(reservation)
        
        assert response.status_code == 200, response.text
        response = response.json()

        return json.dumps(reservation)

    return app


if __name__ == "__main__":
    app = create_app()
    app.run(debug=True, port=8083)
