# Pomona Transit System

A Java 17 Swing desktop application for a database systems lab. The app uses JDBC and MySQL to manage trips, trip offerings, drivers, buses, stops, and actual trip stop records for a Pomona Transit System.

## Tech Stack

- Java 17
- Swing
- JDBC
- MySQL
- VS Code friendly project structure

## Project Structure

```text
Lab4/
├── README.md
├── schema.sql
├── test_data.sql
├── docs/
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── src/
│   └── pomonatransit/
│       ├── Main.java
│       ├── db/
│       ├── gui/
│       └── service/
└── .vscode/
```

## Features

The desktop GUI supports all required lab transactions:

1. Display trip schedules by start location, destination, and date
2. Manage trip offerings
3. Display the stops of a given trip
4. Display the weekly schedule of a driver
5. Add a driver
6. Add a bus
7. Delete a bus
8. Record actual trip stop data

## Setup

### 1. Create the database

```sql
CREATE DATABASE pomona_transit_system;
USE pomona_transit_system;
```

### 2. Load the schema and sample data

Run the files in this order:

1. `schema.sql`
2. `test_data.sql`

From a terminal:

```bash
mysql -u root -p pomona_transit_system < schema.sql
mysql -u root -p pomona_transit_system < test_data.sql
```

### 3. Configure the database connection

Edit `src/pomonatransit/db/DBConnection.java` and update:

- `URL`
- `USERNAME`
- `PASSWORD`

Example:

```java
private static final String URL = "jdbc:mysql://localhost:3306/pomona_transit_system";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 4. Make sure the MySQL JDBC driver is available

The project is already set up to load jars from `lib/`. If you replace the connector version later, keep the jar inside that folder.

## Compile and Run

### macOS or Linux

```bash
javac -cp "lib/*:src" src/pomonatransit/Main.java src/pomonatransit/db/DBConnection.java src/pomonatransit/service/*.java src/pomonatransit/gui/*.java src/pomonatransit/gui/screens/*.java
java -cp "lib/*:src" pomonatransit.Main
```

### Windows Command Prompt

```bat
javac -cp "lib/*;src" src/pomonatransit/Main.java src/pomonatransit/db/DBConnection.java src/pomonatransit/service/*.java src/pomonatransit/gui/*.java src/pomonatransit/gui/screens/*.java
java -cp "lib/*;src" pomonatransit.Main
```

## Running in VS Code

1. Open the project folder.
2. Install the Java Extension Pack if needed.
3. Confirm `.vscode/settings.json` contains:

```json
{
  "java.project.referencedLibraries": [
    "lib/**/*.jar"
  ]
}
```

4. Run `src/pomonatransit/Main.java`.

## Sample Test Inputs

### Trip Schedule

- StartLocationName: `Pomona Station`
- DestinationName: `Claremont Depot`
- Date: `2026-04-20`

### Trip Offerings

- Delete trip offering:
  - `TripNumber`: `1005`
  - `Date`: `2026-04-24`
  - `ScheduledStartTime`: `17:00:00`
- Change driver:
  - `TripNumber`: `1002`
  - `Date`: `2026-04-21`
  - `ScheduledStartTime`: `15:00:00`
  - `DriverName`: `Alice Johnson`
- Change bus:
  - `TripNumber`: `1002`
  - `Date`: `2026-04-21`
  - `ScheduledStartTime`: `15:00:00`
  - `BusID`: `201`

### Trip Stops

- `TripNumber`: `1001`

### Driver Weekly Schedule

- `DriverName`: `Alice Johnson`
- `Start Date`: `2026-04-20`

### Add Driver

- `DriverName`: `Maria Lopez`
- `DriverTelephoneNumber`: `909-555-2001`

### Add Bus

- `BusID`: `206`
- `Model`: `Gillig BRT`
- `Year`: `2023`

### Delete Bus

- `BusID`: `206`

### Actual Trip Stop Data

- `TripNumber`: `1002`
- `Date`: `2026-04-20`
- `ScheduledStartTime`: `07:30:00`
- `StopNumber`: `1`
- `ScheduledArrivalTime`: `07:30:00`
- `ActualStartTime`: `07:31:00`
- `ActualArrivalTime`: `07:32:00`
- `NumberOfPassengerIn`: `8`
- `NumberOfPassengerOut`: `0`

## Notes

- Date format: `YYYY-MM-DD`
- Time format: `HH:MM:SS`
- Some delete actions may be blocked when related records already exist, which is expected because of foreign key constraints.
- The GUI shows database connection status in the header when the app starts.

## GitHub Push Checklist

- Update the database password in `DBConnection.java` before running locally.
- Do not commit personal passwords or machine-specific secrets.
- Keep compiled `.class` files out of the repo.
- Commit `schema.sql`, `test_data.sql`, source code, and documentation.
