-- Remove dependent booking records first to avoid foreign key conflicts
DELETE FROM stays;
-- Remove room records after stays are deleted
DELETE FROM rooms;
-- Remove hotel records last after related rooms are deleted
DELETE FROM hotels;