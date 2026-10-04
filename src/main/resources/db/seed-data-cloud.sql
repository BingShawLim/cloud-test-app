-- Optional sample rows for the cloud screencast. No USE statement - run while
-- already connected to the target schema.
INSERT INTO contact (FIRST_NAME, LAST_NAME, EMAIL, PHONE, COMPANY) VALUES
  ('Ada',   'Lovelace', 'ada.lovelace@example.com', '512-555-0101', 'Analytical Engines LLC'),
  ('Grace', 'Hopper',   'grace.hopper@example.com', '512-555-0102', 'Naval Systems Group'),
  ('Alan',  'Turing',   'alan.turing@example.com',  '512-555-0103', 'Bletchley Consulting');
