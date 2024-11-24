class BookingReferenceService:
    def __init__(self, session):
        self.session = session
    
    def get_booking_reference(self):
        return self.session.get("http://localhost:8082/booking_reference").text

class TrainDataService:
    def __init__(self, session):
        self.session = session

    def get_train_data(self,train_id):
        return self.session.get(f"http://localhost:8081/data_for_train/{train_id}").json()
    
    def post_reservation(self, reservation):
        return self.session.post("http://localhost:8081/reserve", json=reservation)