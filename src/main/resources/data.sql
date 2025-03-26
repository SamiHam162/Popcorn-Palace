-- Insert sample movies
INSERT INTO movie (title, genre, duration, rating, release_year) VALUES
                                                                     ('The Avengers', 'Action', 143, 'PG-13', 2012),
                                                                     ('Inception', 'Sci-Fi', 148, 'PG-13', 2010),
                                                                     ('The Shawshank Redemption', 'Drama', 142, 'R', 1994),
                                                                     ('Pulp Fiction', 'Crime', 154, 'R', 1994),
                                                                     ('The Dark Knight', 'Action', 152, 'PG-13', 2008);

-- Insert sample showtimes
INSERT INTO showtime (movie_id, theater, start_time, end_time, price) VALUES
                                                                          (1, 'Theater 1', '2025-04-01 10:00:00', '2025-04-01 12:30:00', 12.50),
                                                                          (2, 'Theater 2', '2025-04-01 11:00:00', '2025-04-01 13:30:00', 14.00),
                                                                          (3, 'Theater 3', '2025-04-01 13:00:00', '2025-04-01 15:30:00', 10.00),
                                                                          (4, 'Theater 1', '2025-04-01 13:00:00', '2025-04-01 15:30:00', 11.50),
                                                                          (5, 'Theater 2', '2025-04-01 14:00:00', '2025-04-01 16:30:00', 13.00);

-- Insert sample bookings
INSERT INTO booking (id, showtime_id, user_id, seat_number) VALUES
                                                                ('d1a6423b-4469-4b00-8c5f-e3cfc42eacae', 1, '84438967-f68f-4fa0-b620-0f08217e76af', 1),
                                                                ('f2b7534c-5570-5b11-9d6f-f4dfd53f87bf', 1, '84438967-f68f-4fa0-b620-0f08217e76af', 2),
                                                                ('a3c8645d-6681-6c22-0e7g-g5ege64f98cg', 2, '95549078-g79g-5gb1-c731-1g19328e87bg', 5);
