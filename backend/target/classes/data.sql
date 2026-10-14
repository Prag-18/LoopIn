-- Pre-populate initial records if tables are empty

INSERT INTO item_requests (item_name, category, requester_name, requester_email, start_date, end_date, trust_radius, status, note)
SELECT 'TI-84 Graphing Calculator', 'Academics', 'Alex Morgan', 'alex.m@campus.edu', CURRENT_DATE + INTERVAL 1 DAY, CURRENT_DATE + INTERVAL 5 DAY, 1, 'OPEN', 'Needed for Calculus III midterm exam'
WHERE NOT EXISTS (SELECT 1 FROM item_requests WHERE item_name = 'TI-84 Graphing Calculator');

INSERT INTO item_requests (item_name, category, requester_name, requester_email, start_date, end_date, trust_radius, status, note)
SELECT 'Cotton White Lab Coat', 'Academics', 'Priya Sharma', 'priya.s@campus.ac.in', CURRENT_DATE + INTERVAL 2 DAY, CURRENT_DATE + INTERVAL 4 DAY, 2, 'OPEN', 'Organic Chemistry Lab on Thursday'
WHERE NOT EXISTS (SELECT 1 FROM item_requests WHERE item_name = 'Cotton White Lab Coat');

INSERT INTO item_requests (item_name, category, requester_name, requester_email, start_date, end_date, trust_radius, status, note)
SELECT 'Sony Alpha Camera with Tripod', 'Electronics', 'David Kim', 'david.k@campus.edu', CURRENT_DATE + INTERVAL 3 DAY, CURRENT_DATE + INTERVAL 6 DAY, 3, 'OPEN', 'Weekend film festival project'
WHERE NOT EXISTS (SELECT 1 FROM item_requests WHERE item_name = 'Sony Alpha Camera with Tripod');

INSERT INTO item_listings (title, category, owner_name, owner_email, min_trust_degree, available, condition_state)
SELECT 'Texas Instruments TI-84 Plus CE', 'Academics', 'Emma Watson', 'emma.w@campus.edu', 1, TRUE, 'EXCELLENT'
WHERE NOT EXISTS (SELECT 1 FROM item_listings WHERE title = 'Texas Instruments TI-84 Plus CE');

INSERT INTO item_listings (title, category, owner_name, owner_email, min_trust_degree, available, condition_state)
SELECT 'White Lab Coat - 100% Cotton (Unisex M)', 'Academics', 'Rahul Patel', 'rahul.p@campus.ac.in', 2, TRUE, 'GOOD'
WHERE NOT EXISTS (SELECT 1 FROM item_listings WHERE title = 'White Lab Coat - 100% Cotton (Unisex M)');

INSERT INTO item_listings (title, category, owner_name, owner_email, min_trust_degree, available, condition_state)
SELECT 'Sony A6400 Mirrorless Camera & 50mm Lens', 'Electronics', 'Sophia Chen', 'sophia.c@campus.edu', 3, TRUE, 'EXCELLENT'
WHERE NOT EXISTS (SELECT 1 FROM item_listings WHERE title = 'Sony A6400 Mirrorless Camera & 50mm Lens');

INSERT INTO item_listings (title, category, owner_name, owner_email, min_trust_degree, available, condition_state)
SELECT 'Anker USB-C to HDMI 4K Adapter', 'Electronics', 'Liam Davis', 'liam.d@campus.edu', 2, TRUE, 'GOOD'
WHERE NOT EXISTS (SELECT 1 FROM item_listings WHERE title = 'Anker USB-C to HDMI 4K Adapter');
