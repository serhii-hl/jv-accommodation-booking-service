INSERT INTO users (id, company_name, email, first_name, is_deleted, last_name, password, phone_number, role, tax_number, telegram_chat_id, telegram_user_id)
VALUES (100, 'Test Company', 'owner@example.com', 'Test', false, 'Owner', 'Password1234!', '+1234567890', 'OWNER', '1234567890', '123456789', '987654321');

INSERT INTO accommodations (id, type, size, daily_price, owner_id, is_deleted)
VALUES (100, 'APARTMENT', 'ONE_BEDROOM', 75.50, 100, false);

INSERT INTO locations (accommodation_id, country, city, street, number, is_deleted)
VALUES (100, 'Ukraine', 'Kyiv', 'Shevchenko', '1', false);

INSERT INTO accommodation_units (id, accommodation_id, unit_number, is_active)
VALUES (100, 100, '101', true);

INSERT INTO accommodation_amenities (accommodation_id, amenity)
VALUES
    (100, 'WIFI'),
    (100, 'KITCHEN'),
    (100, 'PARKING');

INSERT INTO accommodation_photos (id, accommodation_id, url)
VALUES (100, 100, 'http://example.com/apartment_photo1.jpg');

INSERT INTO bookings (id, check_in_date, check_out_date, accommodation_id, unit_id, user_id, status, is_deleted, total_price)
VALUES (100, '2025-08-01', '2025-08-07', 100, 100, 100, 'CONFIRMED', FALSE, 450.00);