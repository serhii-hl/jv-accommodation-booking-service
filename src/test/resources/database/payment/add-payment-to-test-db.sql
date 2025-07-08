INSERT INTO payments (id, payment_status, booking_id, session_url, session_id,
                      price, currency, booking_date, is_deleted)
VALUES (100, 'PENDING', 100,
        'http://stripe.com/session_100', 'cs_test_123456789', 450.00,
        'USD', '2025-07-05 00:00:00', FALSE);
