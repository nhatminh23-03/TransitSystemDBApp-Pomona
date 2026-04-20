# Pomona Transit System - Planning Phase Documentation

## 1. Project Overview
The **Pomona Transit System** application is a Java-based database program designed to manage trip schedules, trip offerings, bus assignments, driver assignments, stop information, and actual trip stop records. The system will use **JDBC** to connect a Java application to a relational database and support the required transit management transactions described in the lab assignment.

This project focuses on building a practical database application that allows users to view schedules, update trip offerings, manage buses and drivers, and record actual trip data.

## 2. Purpose of the App
The purpose of this application is to provide a simple transit management system that can:
- Store and manage route and trip information.
- Maintain scheduled and actual transit operation data.
- Allow database updates for drivers, buses, and trip offerings.
- Provide query features for schedules, stops, and weekly driver assignments.
- Demonstrate the use of **relational database design** and **JDBC programming** in a real-world style application.

## 3. Problem Statement
Transit administrators need a system to organize route schedules and operational records efficiently. Manually tracking trips, buses, drivers, and stop activity is error-prone and time-consuming. This application solves that problem by storing the data in a structured relational database and providing a Java interface for performing the required operations.

## 4. Scope
The application will support the following major areas:
- Trip and route management
- Trip offering management
- Stop and trip stop lookup
- Driver schedule lookup
- Bus and driver maintenance
- Actual trip data recording

The project will be implemented as a **menu-driven console application** to keep the system simple, focused, and aligned with the lab requirements.

## 5. Functional Requirements
Based on the assignment, the system must support the following functions:

### FR-1: Display trip schedule
The system shall display the schedule of all trips for a given:
- `StartLocationName`
- `DestinationName`
- `Date`

The output shall include:
- `ScheduledStartTime`
- `ScheduledArrivalTime`
- `DriverName`
- `BusID`

### FR-2: Edit trip offerings
The system shall allow users to edit the `TripOffering` table by performing the following actions:
- Delete a trip offering by `TripNumber`, `Date`, and `ScheduledStartTime`
- Add one or more trip offerings
- Change the driver for a given trip offering
- Change the bus for a given trip offering

### FR-3: Display the stops of a given trip
The system shall display all stop information for a given trip using the `TripStopInfo` table.

### FR-4: Display the weekly schedule of a driver
The system shall display the weekly trip schedule of a given driver for a specified date.

### FR-5: Add a driver
The system shall allow a new driver to be inserted into the `Driver` table.

### FR-6: Add a bus
The system shall allow a new bus to be inserted into the `Bus` table.

### FR-7: Delete a bus
The system shall allow a bus to be deleted from the `Bus` table.

### FR-8: Record actual trip stop data
The system shall insert actual trip stop information into the `ActualTripStopInfo` table for a given trip offering.

## 6. Non-Functional Requirements
- **Usability:** The application should provide a clear text-based menu and readable output.
- **Maintainability:** The code should be organized into logical classes and packages.
- **Reliability:** SQL operations should use proper primary keys and foreign keys to maintain data integrity.
- **Performance:** Queries should return results quickly for the expected small academic dataset.
- **Reusability:** Database connection logic should be reusable across multiple service classes.
- **Portability:** The system should run on any machine with Java, MySQL, and the JDBC driver installed.

## 7. Proposed Tech Stack

### Programming Language
- **Java 17**

### Database
- **MySQL Community Server**

### Database Access
- **JDBC (MySQL Connector/J)**

### Development Environment
- **IntelliJ IDEA Community Edition**

### Database Design / Query Tool
- **MySQL Workbench**

### Version Control
- **Git** and **GitHub** (optional but recommended)

## 8. Why This Tech Stack
- **Java** is required because the lab specifically asks for a JDBC-based solution.
- **JDBC** allows direct SQL execution from Java.
- **MySQL** is a reliable relational database that is commonly used in database classes.
- **MySQL Workbench** makes it easier to design tables, run test SQL, and inspect data.
- **IntelliJ IDEA Community** provides a free and beginner-friendly Java IDE.

## 9. Application Type
This application will be implemented as a **console-based CRUD application** with a menu system.

Example main menu:
1. Display trip schedule
2. Edit trip offering
3. Display trip stops
4. Display weekly driver schedule
5. Add driver
6. Add bus
7. Delete bus
8. Record actual trip stop data
9. Exit

## 10. Database Tables
The application will use the following tables from the assignment:

- `Trip(TripNumber, StartLocationName, DestinationName)`
- `TripOffering(TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID)`
- `Bus(BusID, Model, Year)`
- `Driver(DriverName, DriverTelephoneNumber)`
- `Stop(StopNumber, StopAddress)`
- `ActualTripStopInfo(TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOfPassengerOut)`
- `TripStopInfo(TripNumber, StopNumber, SequenceNumber, DrivingTime)`

## 11. Suggested Primary Keys and Foreign Keys

### Primary Keys
- `Trip`: `TripNumber`
- `Bus`: `BusID`
- `Driver`: `DriverName`
- `Stop`: `StopNumber`
- `TripOffering`: composite key (`TripNumber`, `Date`, `ScheduledStartTime`)
- `TripStopInfo`: composite key (`TripNumber`, `StopNumber`)
- `ActualTripStopInfo`: composite key (`TripNumber`, `Date`, `ScheduledStartTime`, `StopNumber`)

### Foreign Keys
- `TripOffering.TripNumber` -> `Trip.TripNumber`
- `TripOffering.DriverName` -> `Driver.DriverName`
- `TripOffering.BusID` -> `Bus.BusID`
- `TripStopInfo.TripNumber` -> `Trip.TripNumber`
- `TripStopInfo.StopNumber` -> `Stop.StopNumber`
- `ActualTripStopInfo.(TripNumber, Date, ScheduledStartTime)` -> `TripOffering.(TripNumber, Date, ScheduledStartTime)`
- `ActualTripStopInfo.StopNumber` -> `Stop.StopNumber`

## 12. Folder Structure
A clean project structure could look like this:

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
│   ├── Main.java
│   ├── db/
│   │   └── DBConnection.java
│   ├── model/
│   │   ├── Trip.java
│   │   ├── TripOffering.java
│   │   ├── Bus.java
│   │   ├── Driver.java
│   │   ├── Stop.java
│   │   ├── TripStopInfo.java
│   │   └── ActualTripStopInfo.java
│   ├── service/
│   │   ├── TripService.java
│   │   ├── TripOfferingService.java
│   │   ├── DriverService.java
│   │   ├── BusService.java
│   │   └── StopService.java
│   └── util/
│       └── InputHelper.java
└── output/
    └── sample_test_output.txt
```

## 13. Responsibilities of Each Folder
- `docs/` stores project documentation.
- `lib/` stores external libraries such as the JDBC connector.
- `src/db/` contains database connection code.
- `src/model/` contains Java classes representing database tables.
- `src/service/` contains business logic and SQL operations.
- `src/util/` contains helper classes for input and formatting.
- `output/` stores test run results and sample program output.

## 14. Main Modules

### DBConnection Module
Responsible for:
- Creating the JDBC connection
- Managing database URL, username, and password

### TripService Module
Responsible for:
- Displaying schedules
- Displaying trip stops

### TripOfferingService Module
Responsible for:
- Adding trip offerings
- Deleting trip offerings
- Updating assigned drivers
- Updating assigned buses

### DriverService Module
Responsible for:
- Adding drivers
- Displaying weekly schedules

### BusService Module
Responsible for:
- Adding buses
- Deleting buses

### ActualTripStopInfo Module
Responsible for:
- Recording actual stop data for trip offerings

## 15. User Flow
1. User launches the program.
2. The system displays the main menu.
3. The user selects a transaction.
4. The system asks for the required input values.
5. The system executes the SQL query through JDBC.
6. The result is displayed or the database is updated.
7. The user returns to the menu or exits the program.

## 16. Development Plan

### Phase 1: Planning
- Review assignment requirements
- Define tables, keys, and relationships
- Choose the tech stack
- Design folder structure

### Phase 2: Database Design
- Create the schema in MySQL
- Add primary and foreign key constraints
- Insert sample test data

### Phase 3: Java Setup
- Create the Java project
- Add JDBC driver
- Test database connection

### Phase 4: Feature Implementation
- Implement each required transaction one by one
- Test each query directly in MySQL first
- Then integrate it into Java

### Phase 5: Testing and Documentation
- Test all menu options with sample data
- Capture output screenshots or console logs
- Prepare final printed submission materials

## 17. Testing Plan
The application will be tested using sample data for each required transaction:
- Schedule lookup test
- Trip offering add/delete/update tests
- Trip stop display test
- Weekly driver schedule test
- Add driver test
- Add bus test
- Delete bus test
- Record actual trip stop info test

Each test should include:
- Input values
- Expected result
- Actual output

## 18. Risks and Challenges
- Managing composite primary keys correctly
- Maintaining referential integrity across related tables
- Handling date and time values properly in Java and SQL
- Preventing invalid updates or deletions
- Keeping SQL queries organized and readable

## 19. Future Improvements
If this application were expanded beyond the lab, possible improvements could include:
- A graphical user interface
- Search filters and reporting
- Authentication for admins and dispatchers
- Validation for duplicate or invalid records
- Automatic report export to text or PDF

## 20. Conclusion
The Pomona Transit System project is a practical database application that demonstrates core database concepts, JDBC integration, SQL query design, and Java program structure. This planning document defines the system purpose, requirements, technology choices, project organization, and development steps needed to complete the lab successfully.

## 21. Assignment Reference
This planning document is based on the uploaded lab instructions, which require implementing the Pomona Transit System with JDBC and support for schedule display, trip offering updates, stop lookup, driver schedule lookup, bus and driver maintenance, and actual trip stop recording. fileciteturn2file0
