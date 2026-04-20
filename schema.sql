-- Pomona Transit System database schema
-- This script uses simple MySQL syntax and creates the tables needed
-- for the JDBC console application.
--
-- Assumptions about composite keys:
-- 1. A trip offering is uniquely identified by:
--    (TripNumber, Date, ScheduledStartTime)
--    This means the same trip can be offered on different dates or
--    at different start times.
-- 2. ActualTripStopInfo stores real stop data for one stop of one
--    specific trip offering, so its primary key is:
--    (TripNumber, Date, ScheduledStartTime, StopNumber)
-- 3. TripStopInfo stores which stops belong to a trip. Its primary key
--    is (TripNumber, StopNumber), assuming a stop appears only once
--    within the same trip.
-- 4. SequenceNumber should be unique within one trip so the stop order
--    is always clear.
-- 5. ActualTripStopInfo should reference both TripOffering and
--    TripStopInfo so a stop record cannot be inserted for a stop that
--    does not belong to that trip.

-- Drop child tables first to avoid foreign key errors.
DROP TABLE IF EXISTS ActualTripStopInfo;
DROP TABLE IF EXISTS TripStopInfo;
DROP TABLE IF EXISTS TripOffering;
DROP TABLE IF EXISTS Stop;
DROP TABLE IF EXISTS Bus;
DROP TABLE IF EXISTS Driver;
DROP TABLE IF EXISTS Trip;


CREATE TABLE Trip (
    TripNumber INT NOT NULL,
    StartLocationName VARCHAR(100) NOT NULL,
    DestinationName VARCHAR(100) NOT NULL,
    PRIMARY KEY (TripNumber)
);


CREATE TABLE Driver (
    DriverName VARCHAR(100) NOT NULL,
    DriverTelephoneNumber VARCHAR(20) NOT NULL,
    PRIMARY KEY (DriverName)
);


CREATE TABLE Bus (
    BusID INT NOT NULL,
    Model VARCHAR(50) NOT NULL,
    Year INT NOT NULL,
    PRIMARY KEY (BusID)
);


CREATE TABLE Stop (
    StopNumber INT NOT NULL,
    StopAddress VARCHAR(200) NOT NULL,
    PRIMARY KEY (StopNumber)
);


CREATE TABLE TripOffering (
    TripNumber INT NOT NULL,
    Date DATE NOT NULL,
    ScheduledStartTime TIME NOT NULL,
    ScheduledArrivalTime TIME NOT NULL,
    DriverName VARCHAR(100) NOT NULL,
    BusID INT NOT NULL,
    PRIMARY KEY (TripNumber, Date, ScheduledStartTime),
    CONSTRAINT fk_tripoffering_trip
        FOREIGN KEY (TripNumber)
        REFERENCES Trip (TripNumber),
    CONSTRAINT fk_tripoffering_driver
        FOREIGN KEY (DriverName)
        REFERENCES Driver (DriverName),
    CONSTRAINT fk_tripoffering_bus
        FOREIGN KEY (BusID)
        REFERENCES Bus (BusID)
);


CREATE TABLE TripStopInfo (
    TripNumber INT NOT NULL,
    StopNumber INT NOT NULL,
    SequenceNumber INT NOT NULL,
    DrivingTime TIME NOT NULL,
    PRIMARY KEY (TripNumber, StopNumber),
    UNIQUE (TripNumber, SequenceNumber),
    CONSTRAINT fk_tripstopinfo_trip
        FOREIGN KEY (TripNumber)
        REFERENCES Trip (TripNumber),
    CONSTRAINT fk_tripstopinfo_stop
        FOREIGN KEY (StopNumber)
        REFERENCES Stop (StopNumber)
);


CREATE TABLE ActualTripStopInfo (
    TripNumber INT NOT NULL,
    Date DATE NOT NULL,
    ScheduledStartTime TIME NOT NULL,
    StopNumber INT NOT NULL,
    ScheduledArrivalTime TIME NOT NULL,
    ActualStartTime TIME,
    ActualArrivalTime TIME,
    NumberOfPassengerIn INT NOT NULL DEFAULT 0,
    NumberOfPassengerOut INT NOT NULL DEFAULT 0,
    PRIMARY KEY (TripNumber, Date, ScheduledStartTime, StopNumber),
    CONSTRAINT fk_actualtripstopinfo_tripoffering
        FOREIGN KEY (TripNumber, Date, ScheduledStartTime)
        REFERENCES TripOffering (TripNumber, Date, ScheduledStartTime),
    CONSTRAINT fk_actualtripstopinfo_stop
        FOREIGN KEY (StopNumber)
        REFERENCES Stop (StopNumber),
    CONSTRAINT fk_actualtripstopinfo_tripstopinfo
        FOREIGN KEY (TripNumber, StopNumber)
        REFERENCES TripStopInfo (TripNumber, StopNumber)
);
