import json
from unittest.mock import Mock
import requests
from reservation_service import ReservationService

def test_reserve_seats_from_empty_train():
    
    mock_booking_reference_service = Mock()
    mock_train_data_service = Mock()

    # Create the ReservationService with mocked services
    reservation_service = ReservationService(
        mock_booking_reference_service,
        mock_train_data_service
    )
    
    # Mock the behavior of the booking reference service
    mock_booking_reference_service.get_booking_reference.return_value = "BR123"
    
    # Mock the behavior of the train data service returning an empty train
    mock_train_data_service.get_train_data.return_value = {
            "seats": {
                "1A": {"coach": "A", "seat_number": "1", "booking_reference": ""},
                "2A": {"coach": "A", "seat_number": "2", "booking_reference": ""},
                "3A": {"coach": "A", "seat_number": "3", "booking_reference": ""},
                "4A": {"coach": "A", "seat_number": "4", "booking_reference": ""},
            }
    }
    
    train_id = "express_2000"
    reservation = reservation_service.reserve(4, train_id)
    
    assert reservation["train_id"] == "express_2000", "Expected train_id to be express_2000"
    assert len(reservation["seats"]) == 4, "Expected 4 seats to be reserved"
    assert reservation["seats"] == ["1A", "2A", "3A", "4A"] , "Expected seats 1A, 2A, 3A, 4A to be reserved"
    assert reservation["booking_reference"] == "BR123", "Expected booking reference to be BR123"


def test_reserve_four_additional_seats():
    
    mock_booking_reference_service = Mock()
    mock_train_data_service = Mock()

    # Create the ReservationService with mocked services
    reservation_service = ReservationService(
        mock_booking_reference_service,
        mock_train_data_service
    )
    
    # Mock the behavior of the booking reference service
    mock_booking_reference_service.get_booking_reference.return_value = "BR123"
    
    # Mock the behavior of the train data service returning a train half full representing the first reservation
    mock_train_data_service.get_train_data.return_value = {
            "seats" : {"1A": { "coach" : "A", "seat_number" : "1", "booking_reference" : "BR123" },
                                "2A": { "coach" : "A", "seat_number" : "2", "booking_reference" : "BR123" }, 
                                "3A": { "coach" : "A", "seat_number" : "3", "booking_reference" : "BR123" },
                                "4A": { "coach" : "A", "seat_number" : "4", "booking_reference" : "BR123" },
                                "5A": { "coach" : "A", "seat_number" : "5", "booking_reference" : "" },
                                "6A": { "coach" : "A", "seat_number" : "6", "booking_reference" : "" },
                                "7A": { "coach" : "A", "seat_number" : "7", "booking_reference" : "" },
                                "8A": { "coach" : "A", "seat_number" : "8", "booking_reference" : "" },
                                "1B": { "coach" : "B", "seat_number" : "1", "booking_reference" : "" },
                                "2B": { "coach" : "B", "seat_number" : "2", "booking_reference" : "" }, 
                                "3B": { "coach" : "B", "seat_number" : "3", "booking_reference" : "" },
                                "4B": { "coach" : "B", "seat_number" : "4", "booking_reference" : "" },
                                "5B": { "coach" : "B", "seat_number" : "5", "booking_reference" : "" },
                                "6B": { "coach" : "B", "seat_number" : "6", "booking_reference" : "" },
                                "7B": { "coach" : "B", "seat_number" : "7", "booking_reference" : "" },
                                "8B": { "coach" : "B", "seat_number" : "8", "booking_reference" : "" }
                               }
            }
    
    train_id = "express_2000"
    
    # Second reservation
    second_reservation = reservation_service.reserve(4, train_id)
    
    assert second_reservation["train_id"] == "express_2000"
    assert len(second_reservation["seats"]) == 4
    assert second_reservation["seats"] == ["5A", "6A", "7A", "8A"]
