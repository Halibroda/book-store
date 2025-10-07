INSERT INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
VALUES ('1990-05-15', 'john.doe@email.com', 'John Doe', '{bcrypt}$2a$12$EBBcoORuQ3SIFV/b0gTAhOsL2T9vi/hAtxeSRVu4g3w0bnAFTn7NW', '555-123-4567'),
       ('1985-09-20', 'jane.smith@email.com', 'Jane Smith', '{bcrypt}$2a$12$2r.foz.KLL9B9pE2gggWiOeGsyY1ioL2hb9YJ1sujjZggJ8ZTn4kW', '555-987-6543'),
       ('1978-03-08', 'bob.jones@email.com', 'Bob Jones', '{bcrypt}$2a$12$lLQlmLZocd2QcKIUfCAfkuJtw98vIhO.9id6Sp404f5ICBudTWlRK', '555-321-6789'),
       ('1982-11-25', 'alice.white@email.com', 'Alice White', '{bcrypt}$2a$12$neukyzsL5BWcB4uF4R1LH.v7C3eQYCr6dvADkvyHwwHxcvqMJG5WW', '555-876-5432'),
       ('1995-07-12', 'mike.wilson@email.com', 'Mike Wilson', '{bcrypt}$2a$12$mNBjlnDPa8I.yV/T0WkYROksTNzq5D85BolQHgGSriHEdPT5wlUla', '555-234-5678'),
       ('1989-01-30', 'sara.brown@email.com', 'Sara Brown', '{bcrypt}$2a$12$3vXcnXaOTm15qQGe7EygOu8vH4pzRu8IbejVnzy27oNMZDH8674Z2', '555-876-5433'),
       ('1975-06-18', 'tom.jenkins@email.com', 'Tom Jenkins', '{bcrypt}$2a$12$/s70BIqUeQXx65Br4WzTcOSWtH8cMKtCgu9M3.gXRMXcsNEfpxVpe', '555-345-6789'),
       ('1987-12-04', 'lisa.taylor@email.com', 'Lisa Taylor', '{bcrypt}$2a$12$86PhAzA448Ps.EtrnQHbeet0Yv0KtdLBCjUb0ohUBWsoD57R0sabu', '555-789-0123'),
       ('1992-08-22', 'david.wright@email.com', 'David Wright', '{bcrypt}$2a$12$XjKV2QkWikgwLkCG7yA15u9Do9cg5kfpsk75/1eOqRpyl/x6EOyeG', '555-456-7890'),
       ('1980-04-10', 'emily.harris@email.com', 'Emily Harris', '{bcrypt}$2a$12$42KqCYqVj8L2j0URfL3mhuCQkQD4NNUmqLsVO6P67iZ1qAlQpsvJS', '555-098-7654');

INSERT INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
VALUES (1000.00, 'client1@example.com', 'Medelyn Wright', '{bcrypt}$2a$12$mbPewfWSG3CujFFiBqf.6ec47JHvlAzCKp6ar3tKZZbF/oUkXV.YS'),
       (1500.50, 'client2@example.com', 'Landon Phillips', '{bcrypt}$2a$12$VJQQWdW3cm0QXdsX0RyBAOwOuCwOoGtUgeAs6.mhh2lA5F2/fcauC'),
       (800.75, 'client3@example.com', 'Harmony Mason', '{bcrypt}$2a$12$WfMxgpWlYksjm4od9QZmsugBDP5slsY.PwlkdZmr1Dm/AiJgws7Da'),
       (1200.25, 'client4@example.com', 'Archer Harper', '{bcrypt}$2a$12$rhh2WuSR6.cWrHbqeXovhuzhx8ldUaEcDSXAtbhcDxeFvnqe8DvzS'),
       (900.80, 'client5@example.com', 'Kira Jacobs', '{bcrypt}$2a$12$FoBsuw7tQnDl8UY7oXarOeBILk35F2Ex0mZlBbRdvXnPE2iRJ2l2S'),
       (1100.60, 'client6@example.com', 'Maximus Kelly', '{bcrypt}$2a$12$dKb4IlBPpqqAo7Ru47ADTOclZq7XGtX9ys/73rgD5xp71B7HoCBH2'),
       (1300.45, 'client7@example.com', 'Sierra Mitchell', '{bcrypt}$2a$12$yEbygoDPetcucjat/XxMGOQ9p.jY5oYLr5RXbq.BOkVxykUfevI0W'),
       (950.30, 'client8@example.com', 'Quinton Saunders', '{bcrypt}$2a$12$Ion9XhHJd9.FRp37EKCzBeZVyMFfYhGRMrJXo2LB/TxN0msPCbwgm'),
       (1050.90, 'client9@example.com', 'Amina Clarke', '{bcrypt}$2a$12$UD4Iz9.t8bm0CoAAy10ik.Z14vjkM8n7kg20ubbOmnIMiE7hKtrI2'),
       (880.20, 'client10@example.com', 'Bryson Chavez', '{bcrypt}$2a$12$7yLjqhDqd5eWd.iWjoJGq.dMGOTcNrF5LdpKVKcibNoKCzFdMJwcS');

INSERT INTO BOOKS (name, genre, age_group, price, publication_year, author, number_of_pages, characteristics,description, language)
VALUES ('The Hidden Treasure', 'Adventure', 'ADULT', 24.99, '2018-05-15', 'Emily White', 400, 'Mysterious journey','An enthralling adventure of discovery', 'ENGLISH'),
       ('Echoes of Eternity', 'Fantasy', 'TEEN', 16.50, '2011-01-15', 'Daniel Black', 350, 'Magical realms', 'A spellbinding tale of magic and destiny', 'ENGLISH'),
       ('Whispers in the Shadows', 'Mystery', 'ADULT', 29.95, '2018-08-11', 'Sophia Green', 450, 'Intriguing suspense','A gripping mystery that keeps you guessing', 'ENGLISH'),
       ('The Starlight Sonata', 'Romance', 'ADULT', 21.75, '2011-05-15', 'Michael Rose', 320, 'Heartwarming love story','A beautiful journey of love and passion', 'ENGLISH'),
       ('Beyond the Horizon', 'Science Fiction', 'CHILD', 18.99, '2004-05-15', 'Alex Carter', 280,'Interstellar adventure', 'An epic sci-fi adventure beyond the stars', 'ENGLISH'),
       ('Dancing with Shadows', 'Thriller', 'ADULT', 26.50, '2015-05-15', 'Olivia Smith', 380, 'Suspenseful twists','A thrilling tale of danger and intrigue', 'ENGLISH'),
       ('Voices in the Wind', 'Historical Fiction', 'ADULT', 32.00, '2017-05-15', 'William Turner', 500,'Rich historical setting', 'A compelling journey through time', 'ENGLISH'),
       ('Serenade of Souls', 'Fantasy', 'TEEN', 15.99, '2013-05-15', 'Isabella Reed', 330, 'Enchanting realms','A magical fantasy filled with wonder', 'ENGLISH'),
       ('Silent Whispers', 'Mystery', 'ADULT', 27.50, '2021-05-15', 'Benjamin Hall', 420, 'Intricate detective work','A mystery that keeps you on the edge', 'ENGLISH'),
       ('Whirlwind Romance', 'Romance', 'OTHER', 23.25, '2022-05-15', 'Emma Turner', 360, 'Passionate love affair','A romance that sweeps you off your feet', 'ENGLISH');
