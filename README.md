# Accommodation Booking Service project description

Imagine a booking service in your area, offering individuals the opportunity to rent homes, apartments, and other accommodations for their chosen duration. Currently, this service faces significant operational challenges as it relies on antiquated, manual processes for managing properties, renters, financial transactions, and booking records. All data is documented on physical paperwork, creating inefficiencies and limiting the ability to check property availability in real-time. Furthermore, the service only accepts cash payments, leaving out the convenience of credit card transactions.

In this project, we aim to revolutionize the housing rental experience by resolving these issues. Your mission is to develop an advanced online management system for housing rentals. This system will not only simplify the tasks of service administrators but also provide renters with a seamless and efficient platform for securing accommodations, transforming the way people experience housing rentals

## If you work in a team

1. Create an organization on GitHub
   ![Create an organization](./description/create-an-organization.png)
2. Choose a `Free` plan
3. Use a name like `fe-feb20-team0` (your group + your team name)
4. It should belong to your personal account
   ![Set up your team](./description/set-up-your-team.png)
5. Add your teammates to the team by their usernames on Github
   ![Add your teammate](./description/add-your-teammate.png)

## Instructions

1. Create a new Spring Boot project
2. Create a new GitHub repo with `booking-app` name (or any other you like)
    - if you work in a team the repo MUST belong to the organization;
    - configure `Branch protection rules` for the organization repository using the following tutorial:

      <details>
        <summary>Tutorial for branch protection rules</summary>

      Go to the repository settings
      STEP #1
      ![Add your teammate](./description/first-step-for-branch-protection-rules.png)

      STEP #2
      ![Add your teammate](./description/second-step-for-branch-protection-rules.png)

      Add the rule with only next settings
      STEP #3
      ![Add your teammate](./description/third-step-for-branch-protection-rules.png)

      More information about all other settings at
      the [link](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/managing-a-branch-protection-rule)
      </details>

3. Connect a repo to a folder with your Spring Boot project (see the instructions on GitHub);
4. Setup checkstyle plugin in your project
5. Setup CI process to run `mvn clean verify` command by adding a `.github/workflows/ci.yml` file
6. The `master` (or `main`) branch is a base for your feature PullRequests.
7. PRs should be merged there after review

## How to develop a feature

1. `Pull` the latest `master`.
2. Create a `your-feature-name` branch and `push` it to GitHub.
3. Open a Pull Request (`PR`) from your branch to the `master` (`main`).
4. Discuss branches and commits naming practices. Your branch, PR, and commit names should be consistent across the
   team. Example of PR names:
    - [DB]: prefix for liquibase scripts
    - [API] for scripts with api changes
5. Do not use forks, work in single repo altogether!
6. Write your code, save it and push it to GitHub.
7. Ask your teammate to review and approve if everything is OK.
8. If some fixes are required discuss the comments and repeat steps 5-7.
9. Mentors will review your codebase everyday, but not PRs.

## App

### Requirements:

- Functional (what the system should do):
    - Web-based
    - Manage accommodation inventory
    - Manage rental bookings
    - Manage customers
    - Display notifications
    - Handle payments
- Non-functional (what the system should deal with):
    - Support up to 5 concurrent users
    - Manage up to 1000 rental accommodations
    - Handle 50,000 bookings per year
    - Approximately 30MB of data per year

### Architecture

![architecture](./description/architecture.png)

### Models

1. Accommodation:
    - ID: Long (Unique identifier for each accommodation)
    - Type: Enum (e.g., HOUSE, APARTMENT, CONDO, VACATION_HOME)
    - Location: Address (Address or location of the accommodation)
    - Size: String (e.g., Studio, 1 Bedroom, 2 Bedroom, etc.)
    - Amenities: Array of Strings (List of amenities available)
    - Daily Rate: BigDecimal (Price per day in $USD)
    - Availability: Integer (Number of available units of this accommodation)
2. User (Customer):
    - ID: Long (Unique identifier for each user)
    - Email: String
    - First Name: String
    - Last Name: String
    - Password: String (Stored securely)
    - Role: Enum (e.g., MANAGER (or ADMIN), CUSTOMER)
3. Booking:
    - ID: Long (Unique identifier for each booking)
    - Check-in Date: LocalDate
    - Check-out Date: LocalDate
    - Accommodation ID: Long (Reference to the booked accommodation)
    - User ID: Long (Reference to the booking user)
    - Status: Enum (e.g., PENDING, CONFIRMED, CANCELED, EXPIRED)
4. Payment:
    - ID: Long (Unique identifier for each payment)
    - Status: Enum (e.g., PENDING, PAID)
    - Booking ID: Long (Reference to the booking associated with the payment)
    - Session URL: URL (URL for the payment session with a payment provider)
    - Session ID: String (ID of the payment session)
    - Amount to Pay: BigDecimal (Total payment amount in $USD)

### Controllers

1. Authentication Controller:
    - POST: /register - Allows users to register a new account.
    - POST: /login - Grants JWT tokens to authenticated users.

2. User Controller: Managing authentication and user registration
    - PUT: /users/{id}/role - Enables users to update their roles, providing role-based access.
    - GET: /users/me - Retrieves the profile information for the currently logged-in user.
    - PUT/PATCH: /users/me - Allows users to update their profile information.

3. Accommodation Controller: Managing accommodation inventory (CRUD for Accommodations)
    - POST: /accommodations - Permits the addition of new accommodations.
    - GET: /accommodations - Provides a list of available accommodations.
    - GET: /accommodations/{id} - Retrieves detailed information about a specific accommodation.
    - PUT/PATCH: /accommodations/{id} - Allows updates to accommodation details, including inventory management.
    - DELETE: /accommodations/{id} - Enables the removal of accommodations.

4. Booking Controller: Managing users' bookings
    - POST: /bookings - Permits the creation of new accommodation bookings.
    - GET: /bookings/?user_id=...&status=... - Retrieves bookings based on user ID and their status. (Available for managers)
    - GET: /bookings/my - Retrieves user bookings
    - GET: /bookings/{id} - Provides information about a specific booking.
    - PUT/PATCH: /bookings/{id} - Allows users to update their booking details.
    - DELETE: /bookings/{id} - Enables the cancellation of bookings.

5. Payment Controller (Stripe): Facilitates payments for bookings through the platform. Interacts with Stripe API.
   Use stripe-java library.
    - GET: /payments/?user_id=... - Retrieves payment information for users.
    - POST: /payments/ - Initiates payment sessions for booking transactions.
    - GET: /payments/success/ - Handles successful payment processing through Stripe redirection.
    - GET: /payments/cancel/ - Manages payment cancellation and returns payment paused messages during Stripe redirection.

6. Notifications Service (Telegram):
    - Notifications about new bookings created/canceled, new created/released accommodations, and successful payments
    - Other services interact with it to send notifications to booking service administrators.
    - Uses Telegram API, Telegram Chats, and Bots.

## Coding

- Add checkstyle plugin.
- Separate PR to the `main` branch for each task is required.
- 60%+ of the custom code should be covered with tests.
- Make sure to name your commits & branches meaningfully.
- Do not use forks, work in single repo altogether.

## Tasks

### Infrastructure

- Create Trello board for task management
- Create a GitHub organization
- Create a project
- Add maven checkstyle plugin
- Configure CI process
- Add liquibase support
- Add health check controller
- Add Docker and docker-compose support
- Use .env file in the docker with all sensitive information and push only .env.sample with a skeleton data from .env
  file.
- Add swagger documentation to project
- Fulfill README.md after project finalization

### Implement CRUD functionality for Accommodation Service

- Add accommodations table into DB
- Create entity Accommodation
- Implement controllers for all endpoints
- Add permissions to Accommodation Controller
    - Only admin users can create/update/delete accommodations
    - All users (even those not authenticated) should be able to list accommodations
- Use JWT token authentication for users' service

### Implement CRUD for Users Service

- Add users table into DB
- Add user entity
- Add JWT support
- Implement all controllers endpoints

### Implement Booking List & Detail endpoint

- Add bookings table into DB
- Add booking entity
- Implement a get mapper with detailed booking info
- Implement list & detail endpoints
- Validate accommodation availability
- Attach the current user to the booking

### Add filtering for the Booking List endpoint

- Ensure all non-admins can only see their bookings
- Ensure bookings are available only for authenticated users
- Add the `status` parameter for filtering bookings by their status
- Add the `user_id` parameter for admin users, allowing them to see all users' bookings if not specified. If
  specified, show bookings only for the specific user.

### Pay attention to Accommodation availability

- Ensure an accommodation cannot be booked twice on the same date
- Ensure an accommodation cannot be canceled twice

### Implement the possibility of sending notifications

- Set up a Telegram chat for posting notifications
- Set up a Telegram bot for sending notifications (Each team member will have their own telegram bot)
- Explore the `sendMessage` function interface in the Telegram API
- Ensure all private data remains private and never enters the GitHub repository. (Use env variables in the .env file
  for all secret data)
- Create a separate service (Telegram Notification Service) which will implement a NotificationService interface for
  sending messages.
- Integrate sending notifications on new booking creation/cancellation, and accommodation creation/release, providing information about the booking in the message

### Implement a daily-based function for checking expired bookings

- The function should filter all bookings that are expired (return date is tomorrow or earlier for non-cancelled bookings), move bookings status EXPIRED, and send a notification to the Telegram chat for each released accommodation with detailed information
- This will be a scheduled task, you'll need to use a @Scheduled annotation.
- If no bookings are expired for that day, send a "No expired bookings today!" notification.

### Payments Endpoint

- Add Payment table into DB and Payment entity
- Create controllers for list and functional endpoints
- Ensure customers can only see their payments, while managers can see all of them

### Create your first Stripe Payment Session (manual)

- Refer to the Stripe documentation to understand how to work with payments
- Create an account on Stripe. Activating your Stripe account is not necessary; Use only test data. You should not
  work with real money in this project
- Try to create your first Stripe Session (you can use an example from the documentation) to understand how it
  works.

### Automate the process of creating Stripe Payment Sessions

- Ensure your Stripe secret keys are kept secret and not pushed to GitHub
- Implement a create payment session endpoint, which will receive only booking id to create a new Stripe Session for it.
  Calculate the total price of the booking and set it as the unit amount.
- When creating a session, provide the correct links to the success and cancel endpoints
- Use `UriComponentsBuilder` to build the URLs dynamically
- Create a Payment and store the session URL and session ID. Attach the booking to the Payment
- Leave the success and cancel URLs as default for now. They will be handled later
- The paymentResponseDto must be returned from this endpoint.

### Implement success and cancel URLs for Payment Service

- Refer to the tutorial to understand how to work with the success endpoint
- Create a success action that checks whether the Stripe session was successfully paid
- If the payment was successful, mark it as paid
- Create a cancel endpoint that informs the user that the payment can be made later (but the session is available
  for only 24 hours)

### Optional:

- Keep track of expired Stripe sessions
    - Add each-minute scheduled task for checking Stripe Session for expiration
    - If the session is expired - Payment should be also marked as EXPIRED (new status)
    - The user should be able to renew the Payment session (new endpoint)
- Do not allow users to create new books if at least one pending payment for the user
    - Before creating booking - simply check the number of pending payments
    - If at least one exists - forbid booking
- Send a notification to the telegram chat on each successful Payment with its details
    - If payment was paid - just send the notification to the telegram chat

### Advanced

- Deploy your app to the AWS

