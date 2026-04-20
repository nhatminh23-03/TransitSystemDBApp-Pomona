# Pomona Transit System Initial Skeleton Plan

## Summary
Build a small Java console application organized by responsibility: `model` for table-shaped data classes, `db` for JDBC connection/config, `service` for SQL operations, and `ui` for menus and console input/output. This first step will create only the project skeleton and placeholder methods so the project is easy to understand and ready for feature-by-feature implementation later.

## Project Structure
Use this layout:

```text
pomona-transit-system/
├── README.md
├── schema.sql
├── test_data.sql
├── docs/
│   └── planning_phase_documentation.md
├── lib/
│   └── mysql-connector-j.jar
├── src/
│   └── pomonatransit/
│       ├── Main.java
│       ├── db/
│       │   ├── DBConnection.java
│       │   └── DBConfig.java
│       ├── model/
│       │   ├── Trip.java
│       │   ├── TripOffering.java
│       │   ├── Bus.java
│       │   ├── Driver.java
│       │   ├── Stop.java
│       │   ├── TripStopInfo.java
│       │   └── ActualTripStopInfo.java
│       ├── service/
│       │   ├── TripService.java
│       │   ├── TripOfferingService.java
│       │   ├── DriverService.java
│       │   ├── BusService.java
│       │   └── ActualTripStopInfoService.java
│       └── ui/
│           ├── MainMenu.java
│           ├── InputHelper.java
│           └── ConsolePrinter.java
└── output/
    └── sample_test_output.txt
```

## Classes To Create
`Main.java`
Starts the program, creates the menu object, and launches the main loop.

`db/DBConfig.java`
Stores database URL, username, and password in one simple place.

`db/DBConnection.java`
Creates and returns JDBC `Connection` objects. This keeps connection code out of menu and service classes.

`model/Trip.java`
Represents a row from `Trip`.

`model/TripOffering.java`
Represents a row from `TripOffering`, including composite-key fields.

`model/Bus.java`
Represents a row from `Bus`.

`model/Driver.java`
Represents a row from `Driver`.

`model/Stop.java`
Represents a row from `Stop`.

`model/TripStopInfo.java`
Represents a row from `TripStopInfo`.

`model/ActualTripStopInfo.java`
Represents a row from `ActualTripStopInfo`.

`service/TripService.java`
Handles trip-related read features:
display trip schedule and display stops for a trip.

`service/TripOfferingService.java`
Handles add, delete, update-driver, and update-bus actions for trip offerings.

`service/DriverService.java`
Handles adding drivers and displaying a driver’s weekly schedule.

`service/BusService.java`
Handles adding and deleting buses.

`service/ActualTripStopInfoService.java`
Handles inserting actual stop records.

`ui/MainMenu.java`
Shows the text menu, reads the user’s choice, and calls the correct service method.

`ui/InputHelper.java`
Wraps `Scanner` usage for safe, simple console input methods.

`ui/ConsolePrinter.java`
Keeps table-like output and status messages consistent and easy to read.

## Implementation Changes
The initial skeleton should include:
- Package declarations that match the folder structure.
- Simple fields, constructors, getters/setters, and `toString()` methods for all model classes.
- `DBConnection` with a reusable `getConnection()` method using JDBC.
- Service classes with method stubs and comments for each required lab feature.
- `MainMenu` with all 9 menu options wired to placeholder service calls.
- Clear comments explaining why each class exists and how the flow works.
- Prepared-statement usage shown in service method skeletons where user input will later be used, but without full SQL feature implementation yet.
- No frameworks, no ORM, no DAO layer unless later required by the instructor.

## Public Interfaces / Expected Methods
The skeleton should expose these starter methods so later implementation is straightforward:

- `DBConnection.getConnection()`
- `MainMenu.start()`
- `TripService.displayTripSchedule(...)`
- `TripService.displayTripStops(...)`
- `TripOfferingService.addTripOffering(...)`
- `TripOfferingService.deleteTripOffering(...)`
- `TripOfferingService.updateDriver(...)`
- `TripOfferingService.updateBus(...)`
- `DriverService.addDriver(...)`
- `DriverService.displayWeeklySchedule(...)`
- `BusService.addBus(...)`
- `BusService.deleteBus(...)`
- `ActualTripStopInfoService.recordActualTripStop(...)`

## Test Plan
For this skeleton step, verify:
- The project compiles with all packages and imports in place.
- The app starts and shows the full menu.
- Choosing each menu option reaches the correct placeholder method without crashing.
- Database connection code is isolated in `db` classes and not mixed into UI code.
- No SQL built from string concatenation with user input.
- The package split remains beginner-friendly and matches the lab requirement.

## Assumptions
- Use plain Java with a simple `lib/` JDBC driver setup rather than Maven or Gradle for the initial lab-friendly version.
- Use package root `pomonatransit` so the code is organized and avoids default-package issues.
- Keep model classes simple POJOs without advanced design patterns.
- Full SQL logic and real CRUD behavior will be added in the next step after the skeleton is approved.
