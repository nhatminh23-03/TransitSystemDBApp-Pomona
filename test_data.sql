-- Sample data for the Pomona Transit System schema
-- Insert parent tables first, then child tables.

INSERT INTO Trip (TripNumber, StartLocationName, DestinationName) VALUES
(1001, 'Pomona Station', 'Claremont Depot'),
(1002, 'Pomona Station', 'Montclair Plaza'),
(1003, 'Cal Poly Pomona', 'Downtown Pomona'),
(1004, 'Fairplex', 'Cal Poly Pomona'),
(1005, 'Claremont Depot', 'Downtown Pomona');


INSERT INTO Driver (DriverName, DriverTelephoneNumber) VALUES
('Alice Johnson', '909-555-1001'),
('Brian Lee', '909-555-1002'),
('Carla Gomez', '909-555-1003'),
('David Kim', '909-555-1004'),
('Elena Martinez', '909-555-1005');


INSERT INTO Bus (BusID, Model, Year) VALUES
(201, 'Gillig Low Floor', 2018),
(202, 'New Flyer Xcelsior', 2020),
(203, 'Proterra ZX5', 2022),
(204, 'Blue Bird Vision', 2017),
(205, 'Nova Bus LFS', 2021);


INSERT INTO Stop (StopNumber, StopAddress) VALUES
(1, '100 W First St, Pomona, CA'),
(2, '200 E Holt Ave, Pomona, CA'),
(3, '3801 W Temple Ave, Pomona, CA'),
(4, '1101 W McKinley Ave, Pomona, CA'),
(5, '2058 N Mills Ave, Claremont, CA'),
(6, '5060 Montclair Plaza Ln, Montclair, CA'),
(7, '250 W Bonita Ave, San Dimas, CA'),
(8, '300 S Garey Ave, Pomona, CA');


INSERT INTO TripOffering
    (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID)
VALUES
(1001, '2026-04-20', '08:00:00', '08:35:00', 'Alice Johnson', 201),
(1001, '2026-04-20', '14:00:00', '14:35:00', 'Brian Lee', 202),
(1001, '2026-04-21', '09:00:00', '09:35:00', 'Carla Gomez', 203),
(1002, '2026-04-20', '07:30:00', '08:00:00', 'David Kim', 204),
(1002, '2026-04-21', '15:00:00', '15:30:00', 'Elena Martinez', 205),
(1003, '2026-04-20', '10:00:00', '10:25:00', 'Alice Johnson', 203),
(1003, '2026-04-22', '13:00:00', '13:25:00', 'Brian Lee', 201),
(1004, '2026-04-20', '11:00:00', '11:20:00', 'Carla Gomez', 204),
(1004, '2026-04-23', '16:00:00', '16:20:00', 'David Kim', 202),
(1005, '2026-04-21', '12:00:00', '12:40:00', 'Elena Martinez', 205),
(1005, '2026-04-24', '17:00:00', '17:40:00', 'Alice Johnson', 202);


INSERT INTO TripStopInfo (TripNumber, StopNumber, SequenceNumber, DrivingTime) VALUES
(1001, 1, 1, '00:00:00'),
(1001, 2, 2, '00:12:00'),
(1001, 5, 3, '00:35:00'),

(1002, 1, 1, '00:00:00'),
(1002, 7, 2, '00:15:00'),
(1002, 6, 3, '00:30:00'),

(1003, 3, 1, '00:00:00'),
(1003, 2, 2, '00:10:00'),
(1003, 8, 3, '00:25:00'),

(1004, 4, 1, '00:00:00'),
(1004, 2, 2, '00:08:00'),
(1004, 3, 3, '00:20:00'),

(1005, 5, 1, '00:00:00'),
(1005, 7, 2, '00:18:00'),
(1005, 8, 3, '00:40:00');


INSERT INTO ActualTripStopInfo
    (TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime,
     ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOfPassengerOut)
VALUES
(1001, '2026-04-20', '08:00:00', 1, '08:00:00', '08:02:00', '08:03:00', 10, 0),
(1001, '2026-04-20', '08:00:00', 2, '08:12:00', '08:13:00', '08:14:00', 4, 2),
(1001, '2026-04-20', '08:00:00', 5, '08:35:00', '08:34:00', '08:36:00', 1, 8),

(1003, '2026-04-20', '10:00:00', 3, '10:00:00', '10:01:00', '10:02:00', 12, 0),
(1003, '2026-04-20', '10:00:00', 2, '10:10:00', '10:11:00', '10:12:00', 3, 5),

(1004, '2026-04-20', '11:00:00', 4, '11:00:00', '11:00:00', '11:01:00', 7, 0),
(1004, '2026-04-20', '11:00:00', 2, '11:08:00', '11:09:00', '11:10:00', 2, 3);


-- ------------------------------------------------------------
-- Suggested rows to test each menu feature
-- ------------------------------------------------------------
--
-- 1. Display trip schedule
--    Use:
--    StartLocationName = 'Pomona Station'
--    DestinationName   = 'Claremont Depot'
--    Date              = '2026-04-20'
--    Expected matching offerings:
--    Trip 1001 at 08:00 and Trip 1001 at 14:00
--
--    Another good test:
--    StartLocationName = 'Cal Poly Pomona'
--    DestinationName   = 'Downtown Pomona'
--    Date              = '2026-04-20'
--    Expected matching offering:
--    Trip 1003 at 10:00
--
-- 2. Edit trip offering
--    Delete test:
--    Delete TripOffering row
--    (TripNumber=1005, Date='2026-04-24', ScheduledStartTime='17:00:00')
--
--    Change driver test:
--    Update TripOffering row
--    (TripNumber=1002, Date='2026-04-21', ScheduledStartTime='15:00:00')
--    to another valid driver such as 'Alice Johnson'
--
--    Change bus test:
--    Update the same row above to another valid bus such as BusID 201
--
--    Add test:
--    Insert a new offering for an existing trip, driver, and bus
--    for example TripNumber 1004 on a new date like '2026-04-25'
--
-- 3. Display the stops of a given trip
--    Use TripNumber = 1001
--    Stops appear in sequence:
--    1 -> 2 -> 5
--
--    Another good test:
--    TripNumber = 1003
--    Stops appear in sequence:
--    3 -> 2 -> 8
--
-- 4. Display the weekly schedule of a driver
--    Use DriverName = 'Alice Johnson'
--    Around week of '2026-04-20'
--    Alice has offerings on:
--    2026-04-20 at 08:00
--    2026-04-20 at 10:00
--    2026-04-24 at 17:00
--
--    Another good test:
--    DriverName = 'Carla Gomez'
--    She has offerings on 2026-04-21 and 2026-04-20
--
-- 5. Add a driver
--    Current driver names are:
--    Alice Johnson, Brian Lee, Carla Gomez, David Kim, Elena Martinez
--    Insert any new unique name not already in this list.
--
-- 6. Add a bus
--    Current bus IDs are:
--    201, 202, 203, 204, 205
--    Insert any new unique BusID, for example 206.
--
-- 7. Delete a bus
--    To avoid foreign key errors, first use a bus that is not assigned
--    in TripOffering, or add a new unused bus and then delete it.
--    Example workflow:
--    add BusID 206, then delete BusID 206.
--
-- 8. Record actual trip stop data
--    Good trip offerings to test with:
--    (1002, '2026-04-20', '07:30:00')
--    because it exists in TripOffering but has no actual stop rows yet.
--
--    Valid stops for Trip 1002 are:
--    StopNumber 1, 7, and 6
--
-- 9. Exit
--    Use menu option 9 to end the program.
