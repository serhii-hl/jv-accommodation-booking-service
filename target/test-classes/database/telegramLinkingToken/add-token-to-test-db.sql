INSERT INTO users (id, company_name, email, first_name, is_deleted,
                   last_name, password, phone_number, role, tax_number,
                   telegram_chat_id, telegram_user_id)
VALUES (100, 'Test Company', 'owner@example.com',
        'Test', false, 'Owner', 'Password1234!',
        '+1234567890', 'OWNER', '1234567890',
        '123456789', '987654321');

INSERT INTO telegram_linking_tokens (id, token, user_id, created_at, expires_at, is_used)
VALUES (100, 'test token', 100,
        '2025-07-05 00:00:00', '2025-07-07 00:00:00', false)