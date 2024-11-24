class ReservationService:
    def __init__(self, booking_reference_service, train_data_service):
        self.booking_reference_service = booking_reference_service
        self.train_data_service = train_data_service

    def reserve(self, seat_count, train_id):
        booking_reference = self.booking_reference_service.get_booking_reference()
        train_data = self.train_data_service.get_train_data()
        
        available_seats = (
            s
            for s in train_data["seats"].values()
            if s["coach"] == "A" and not s["booking_reference"]
        )
        to_reserve = []
        for i in range(seat_count):
            to_reserve.append(next(available_seats))

        seat_ids = [s["seat_number"] + s["coach"] for s in to_reserve]
        
        return {
            "train_id": train_id,
            "booking_reference": booking_reference,
            "seats": seat_ids,
        }
        
    