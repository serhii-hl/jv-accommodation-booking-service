INSERT INTO users (id, company_name, email, first_name, is_deleted,
                   last_name, password, phone_number, role, tax_number,
                   telegram_chat_id, telegram_user_id)
VALUES (200, NULL, 'customer@example.com',
        'Customer', false, 'User',
        'customerPass', '+1987654321', 'USER',
        NULL, NULL, NULL);