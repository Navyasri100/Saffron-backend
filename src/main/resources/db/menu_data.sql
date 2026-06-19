-- ============================================================
--  Saffron & Soul — Menu Seed Data
--  Run once against your PostgreSQL database:
--    psql -U <user> -d <database> -f menu_data.sql
--  Safe to re-run: INSERT ... ON CONFLICT DO NOTHING
-- ============================================================

-- ── CATEGORIES ──────────────────────────────────────────────
INSERT INTO categories (name, description) VALUES
  ('Appetizers',           'Light starters to begin your culinary journey'),
  ('Seafood Starters',     'Fresh catches from the sea, prepared to perfection'),
  ('Main Course',          'Rich and aromatic main dishes'),
  ('Breads',               'Freshly baked naans, kulchas, and artisan breads from the tandoor'),
  ('Rice Bowls',           'Fragrant rice bowls — comforting, wholesome, and full of flavour'),
  ('Salads & Bowls',       'Fresh, healthy salads and nourishing bowls'),
  ('Pizzas',               'Stone-baked pizzas with Indian and classic toppings'),
  ('Pastas',               'Al dente pastas in rich, house-made sauces'),
  ('Burgers',              'Juicy burgers with bold flavors'),
  ('Desserts',             'Sweet treats to end your meal'),
  ('Ice Creams & Sundaes', 'Chilled frozen delights and indulgent sundaes'),
  ('Beverages',            'Refreshing drinks to complement your meal')
ON CONFLICT (name) DO NOTHING;

-- ── MENU ITEMS ──────────────────────────────────────────────
-- image_url is a shared placeholder; update per-item in the admin panel as needed.

INSERT INTO menu_items (name, description, price, image_url, category_id, is_available)
SELECT name, description, price,
       'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&h=500&fit=crop',
       cat_id, true
FROM (VALUES
  -- APPETIZERS — VEG
  ('Paneer Tikka',          'Marinated cottage cheese cubes grilled to perfection in the tandoor',            220.0, (SELECT id FROM categories WHERE name = 'Appetizers')),
  ('Hara Bhara Kebab',      'Pan-grilled patties of spinach, peas, and paneer with a herby punch',            180.0, (SELECT id FROM categories WHERE name = 'Appetizers')),
  ('Dahi Ke Kebab',         'Creamy hung-curd kebabs with subtle spices and a melt-in-mouth texture',         190.0, (SELECT id FROM categories WHERE name = 'Appetizers')),
  ('Crispy Corn',           'Golden fried sweet corn kernels tossed with peppers and spices',                 160.0, (SELECT id FROM categories WHERE name = 'Appetizers')),
  -- APPETIZERS — NON-VEG
  ('Chicken 65',            'Fiery, crispy chicken bites marinated in spices with a tangy finish',            250.0, (SELECT id FROM categories WHERE name = 'Appetizers')),
  ('Tandoori Chicken (Half)','Half chicken marinated in spiced yogurt, charred in the tandoor',              320.0, (SELECT id FROM categories WHERE name = 'Appetizers')),
  ('Mutton Seekh Kebab',    'Hand-rolled minced mutton skewers grilled over charcoal',                        340.0, (SELECT id FROM categories WHERE name = 'Appetizers')),
  ('Chicken Malai Tikka',   'Tender chicken in a creamy, mildly spiced marinade — grilled till golden',       280.0, (SELECT id FROM categories WHERE name = 'Appetizers')),

  -- SEAFOOD STARTERS
  ('Fish Amritsari',        'Tender river fish in an ajwain-spiced batter, deep fried golden',                320.0, (SELECT id FROM categories WHERE name = 'Seafood Starters')),
  ('Prawn Koliwada',        'Coastal-style battered and fried prawns tossed with lime and chilli',            380.0, (SELECT id FROM categories WHERE name = 'Seafood Starters')),
  ('Tandoori Prawn',        'Jumbo prawns marinated in spiced yogurt and charred in the tandoor',             420.0, (SELECT id FROM categories WHERE name = 'Seafood Starters')),
  ('Fish Tikka',            'Boneless fish cubes marinated in bold spices and grilled to perfection',         300.0, (SELECT id FROM categories WHERE name = 'Seafood Starters')),

  -- MAIN COURSE — VEG
  ('Paneer Butter Masala',  'Cottage cheese cubes in a rich, buttery tomato-cream sauce',                     280.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Dal Makhani',           'Whole black lentils slow-cooked overnight with butter and cream',                220.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Palak Paneer',          'Cottage cheese cubes in a velvety, slow-cooked spinach gravy',                   260.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Matar Paneer',          'Fresh peas and paneer simmered in a mildly spiced onion-tomato gravy',           240.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Rajma Masala',          'Red kidney beans slow-cooked in a rich, spiced tomato gravy',                    210.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Baingan Bharta',        'Wood-fire roasted eggplant mashed with onion, tomatoes, and spices',             220.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  -- MAIN COURSE — NON-VEG
  ('Butter Chicken',        'Tender chicken in a luscious, buttery tomato sauce — the nation''s favourite',   320.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Chicken Tikka Masala',  'Grilled chicken tikka simmered in a creamy, spiced tomato gravy',               340.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Mutton Rogan Josh',     'Tender mutton slow-braised in Kashmiri aromatics and whole spices',              420.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Prawn Masala',          'Juicy prawns tossed in a fiery coastal tomato-onion masala',                     450.0, (SELECT id FROM categories WHERE name = 'Main Course')),
  ('Chicken Chettinad',     'Bold, aromatic Chettinad-spiced chicken curry — fiery and fragrant',             360.0, (SELECT id FROM categories WHERE name = 'Main Course')),

  -- SALADS & BOWLS — VEG
  ('Greek Salad Bowl',      'Crisp greens, olives, tomatoes, cucumber, and feta in a herb dressing',          220.0, (SELECT id FROM categories WHERE name = 'Salads & Bowls')),
  ('Quinoa Veggie Bowl',    'Protein-rich quinoa with roasted vegetables and a tahini drizzle',               280.0, (SELECT id FROM categories WHERE name = 'Salads & Bowls')),
  ('Paneer Power Bowl',     'Grilled paneer over a bed of greens, chickpeas, and zesty dressing',             260.0, (SELECT id FROM categories WHERE name = 'Salads & Bowls')),
  -- SALADS & BOWLS — NON-VEG
  ('Grilled Chicken Bowl',  'Sliced grilled chicken breast over mixed greens with a light vinaigrette',       320.0, (SELECT id FROM categories WHERE name = 'Salads & Bowls')),
  ('Prawn Avocado Bowl',    'Sautéed prawns with creamy avocado, greens, and a citrus dressing',              380.0, (SELECT id FROM categories WHERE name = 'Salads & Bowls')),
  ('Tandoori Chicken Salad','Marinated tandoori chicken strips over a fresh garden salad',                    300.0, (SELECT id FROM categories WHERE name = 'Salads & Bowls')),

  -- PIZZAS — VEG
  ('Paneer Tikka Pizza',    'Stone-baked pizza topped with marinated paneer tikka and peppers',               380.0, (SELECT id FROM categories WHERE name = 'Pizzas')),
  ('Margherita',            'Classic stone-baked pizza with tomato sauce, fresh mozzarella, and basil',       280.0, (SELECT id FROM categories WHERE name = 'Pizzas')),
  ('Farm Fresh Veggie Pizza','Loaded with seasonal vegetables, olives, and a herbed tomato base',             320.0, (SELECT id FROM categories WHERE name = 'Pizzas')),
  -- PIZZAS — NON-VEG
  ('Chicken Tikka Pizza',   'Stone-baked pizza topped with spiced chicken tikka and onions',                  420.0, (SELECT id FROM categories WHERE name = 'Pizzas')),
  ('BBQ Chicken Pizza',     'Smoky BBQ sauce, grilled chicken, and caramelized onions on a crispy base',      400.0, (SELECT id FROM categories WHERE name = 'Pizzas')),
  ('Prawn & Pesto Pizza',   'Sautéed prawns with house-made pesto and sun-dried tomatoes',                    460.0, (SELECT id FROM categories WHERE name = 'Pizzas')),

  -- PASTAS — VEG
  ('Penne Arrabbiata',      'Penne in a fiery garlic-tomato sauce with fresh basil and chilli flakes',        280.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  ('Spaghetti Aglio e Olio','Classic spaghetti tossed with garlic-infused olive oil, parsley, and chilli',    260.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  ('Pesto Fusilli',         'Spiral pasta tossed in house-made basil pesto with sun-dried tomatoes and pine nuts', 300.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  ('Creamy Mushroom Pasta', 'Fettuccine in a rich garlic-cream sauce with wild mushrooms and parmesan',       320.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  ('Pink Sauce Penne',      'Penne in a silky blend of tomato and cream sauce with herbs',                    290.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  ('Mac & Cheese',          'Elbow macaroni baked in a four-cheese sauce with a golden breadcrumb crust',     280.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  -- PASTAS — NON-VEG
  ('Chicken Bolognese',     'Slow-cooked minced chicken ragù over spaghetti with parmesan',                   360.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  ('Chicken Alfredo',       'Grilled chicken strips in a silky parmesan-cream sauce over fettuccine',         380.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  ('Prawn Linguine',        'Sautéed prawns in a white wine, garlic, and cherry tomato sauce over linguine',  420.0, (SELECT id FROM categories WHERE name = 'Pastas')),
  ('Spicy Chicken Penne',   'Penne with grilled chicken in a spiced nduja-style tomato sauce',                360.0, (SELECT id FROM categories WHERE name = 'Pastas')),

  -- BURGERS — VEG
  ('Crispy Paneer Burger',  'Crunchy fried paneer patty with slaw, pickles, and sriracha mayo',               220.0, (SELECT id FROM categories WHERE name = 'Burgers')),
  ('Spicy Aloo Tikki Burger','Spiced potato tikki in a toasted bun with mint chutney and onions',             180.0, (SELECT id FROM categories WHERE name = 'Burgers')),
  -- BURGERS — NON-VEG
  ('Chicken Tikka Burger',  'Juicy tandoori chicken tikka patty with coleslaw and garlic mayo',               260.0, (SELECT id FROM categories WHERE name = 'Burgers')),
  ('Smoky BBQ Chicken Burger','Grilled chicken with smoky BBQ glaze, cheddar, and pickled jalapeños',         280.0, (SELECT id FROM categories WHERE name = 'Burgers')),
  ('Prawn Po Boy',          'Crispy fried prawns in a toasted roll with remoulade and lettuce',               320.0, (SELECT id FROM categories WHERE name = 'Burgers')),

  -- BREADS
  ('Butter Naan',           'Soft tandoor-baked leavened bread brushed with melted butter',                    60.0, (SELECT id FROM categories WHERE name = 'Breads')),
  ('Garlic Naan',           'Naan topped with minced garlic and fresh coriander, baked in the tandoor',        70.0, (SELECT id FROM categories WHERE name = 'Breads')),
  ('Peshwari Naan',         'Sweet naan stuffed with coconut, almond, and sultanas — a Punjabi classic',       90.0, (SELECT id FROM categories WHERE name = 'Breads')),
  ('Cheese Naan',           'Tandoor-baked naan oozing with molten cheese and herbs',                         100.0, (SELECT id FROM categories WHERE name = 'Breads')),
  ('Paneer Kulcha',         'Leavened bread stuffed with spiced cottage cheese, baked crisp',                 110.0, (SELECT id FROM categories WHERE name = 'Breads')),
  ('Aloo Kulcha',           'Soft tandoor bread filled with a spiced mashed potato mixture',                   90.0, (SELECT id FROM categories WHERE name = 'Breads')),
  ('Laccha Paratha',        'Multi-layered whole-wheat flatbread with a flaky, buttery finish',                70.0, (SELECT id FROM categories WHERE name = 'Breads')),
  ('Missi Roti',            'Gram-flour flatbread spiced with ajwain and onion — rustic and wholesome',        60.0, (SELECT id FROM categories WHERE name = 'Breads')),

  -- RICE BOWLS — VEG
  ('Vegetable Dum Rice',    'Fragrant basmati layered with seasonal vegetables and dum-cooked with saffron',  260.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),
  ('Paneer Dum Rice',       'Slow-cooked basmati rice with spiced paneer, caramelised onions, and saffron',   300.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),
  ('Mushroom Rice Bowl',    'Stir-fried wild mushrooms over steamed basmati with truffle butter',              280.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),
  ('Curd Rice',             'Cooling south-Indian style tempered curd rice garnished with pomegranate',        180.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),
  ('Jeera Rice',            'Fragrant basmati tempered with cumin, ghee, and a hint of bay leaf',             150.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),
  -- RICE BOWLS — NON-VEG
  ('Chicken Dum Rice',      'Succulent chicken layered with aromatic basmati and slow-cooked on dum',         340.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),
  ('Mutton Dum Rice',       'Tender mutton pieces slow-cooked with saffron-infused basmati on the dum',       420.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),
  ('Prawn Rice Bowl',       'Spiced coastal prawn curry served over steamed basmati with raita',              450.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),
  ('Egg Rice Bowl',         'Masala scrambled eggs over cumin-tempered rice with pickle on the side',         220.0, (SELECT id FROM categories WHERE name = 'Rice Bowls')),

  -- DESSERTS
  ('Gulab Jamun',           'Melt-in-your-mouth milk-solid dumplings in rose-cardamom syrup',                 120.0, (SELECT id FROM categories WHERE name = 'Desserts')),
  ('Rasmalai',              'Soft chenna patties soaked in saffron-flavoured chilled cream',                  150.0, (SELECT id FROM categories WHERE name = 'Desserts')),
  ('Gajar Halwa',           'Slow-cooked carrot pudding with ghee, milk, and cardamom',                       130.0, (SELECT id FROM categories WHERE name = 'Desserts')),
  ('Shahi Tukda',           'Fried bread soaked in condensed milk rabri and garnished with silver leaf',       160.0, (SELECT id FROM categories WHERE name = 'Desserts')),
  ('Phirni',                'Set rice-flour pudding scented with rose water, served chilled in earthen bowls', 140.0, (SELECT id FROM categories WHERE name = 'Desserts')),

  -- ICE CREAMS & SUNDAES
  ('Mango Kulfi',           'Dense, creamy frozen dessert infused with fresh Alphonso mango',                  130.0, (SELECT id FROM categories WHERE name = 'Ice Creams & Sundaes')),
  ('Kesar Pista Kulfi',     'Traditional saffron and pistachio kulfi — rich, aromatic, and indulgent',         140.0, (SELECT id FROM categories WHERE name = 'Ice Creams & Sundaes')),
  ('Chocolate Sundae',      'Scoops of chocolate ice cream with hot fudge, whipped cream, and sprinkles',      180.0, (SELECT id FROM categories WHERE name = 'Ice Creams & Sundaes')),
  ('Brownie Sundae',        'Warm chocolate brownie topped with vanilla ice cream and caramel drizzle',         220.0, (SELECT id FROM categories WHERE name = 'Ice Creams & Sundaes')),
  ('Strawberry Sundae',     'Fresh strawberry ice cream layered with berry compote and cream',                  160.0, (SELECT id FROM categories WHERE name = 'Ice Creams & Sundaes')),

  -- BEVERAGES
  ('Mango Lassi',           'Thick, creamy yogurt blended with ripe Alphonso mangoes',                         110.0, (SELECT id FROM categories WHERE name = 'Beverages')),
  ('Sweet Lassi',           'Chilled whisked yogurt sweetened with sugar and a hint of rose water',             90.0, (SELECT id FROM categories WHERE name = 'Beverages')),
  ('Masala Chai',           'Robust CTC tea brewed with ginger, cardamom, and spices — served hot',             60.0, (SELECT id FROM categories WHERE name = 'Beverages')),
  ('Rose Milk',             'Chilled whole milk stirred with fragrant rose syrup and a pinch of cardamom',     100.0, (SELECT id FROM categories WHERE name = 'Beverages')),
  ('Jaljeera',              'Tangy, cumin-spiced sparkling cooler — the perfect appetite opener',                70.0, (SELECT id FROM categories WHERE name = 'Beverages')),
  ('Tender Coconut Water',  'Freshly cracked tender coconut — naturally hydrating and pure',                    120.0, (SELECT id FROM categories WHERE name = 'Beverages')),
  ('Mineral Water (500ml)', 'Chilled packaged mineral water — Bisleri / Kinley',                                30.0, (SELECT id FROM categories WHERE name = 'Beverages')),
  ('Sparkling Water (500ml)','Chilled carbonated mineral water — refreshing and clean',                         60.0, (SELECT id FROM categories WHERE name = 'Beverages'))
) AS v(name, description, price, cat_id)
WHERE NOT EXISTS (SELECT 1 FROM menu_items WHERE menu_items.name = v.name);

-- ── TAGS ────────────────────────────────────────────────────
-- Joins item names to their IDs, skips duplicates with WHERE NOT EXISTS

INSERT INTO menu_item_tags (menu_item_id, tag)
SELECT m.id, t.tag
FROM (VALUES
  -- APPETIZERS
  ('Paneer Tikka',            'vegetarian'),
  ('Paneer Tikka',            'bestseller'),
  ('Hara Bhara Kebab',        'vegetarian'),
  ('Dahi Ke Kebab',           'vegetarian'),
  ('Dahi Ke Kebab',           'signature'),
  ('Crispy Corn',             'vegetarian'),
  ('Crispy Corn',             'new'),
  ('Chicken 65',              'non-vegetarian'),
  ('Chicken 65',              'bestseller'),
  ('Tandoori Chicken (Half)', 'non-vegetarian'),
  ('Tandoori Chicken (Half)', 'signature'),
  ('Mutton Seekh Kebab',      'non-vegetarian'),
  ('Mutton Seekh Kebab',      'spicy'),
  ('Chicken Malai Tikka',     'non-vegetarian'),
  -- SEAFOOD STARTERS
  ('Fish Amritsari',          'non-vegetarian'),
  ('Fish Amritsari',          'bestseller'),
  ('Prawn Koliwada',          'non-vegetarian'),
  ('Prawn Koliwada',          'signature'),
  ('Tandoori Prawn',          'non-vegetarian'),
  ('Tandoori Prawn',          'premium'),
  ('Fish Tikka',              'non-vegetarian'),
  -- MAIN COURSE
  ('Paneer Butter Masala',    'vegetarian'),
  ('Paneer Butter Masala',    'bestseller'),
  ('Dal Makhani',             'vegetarian'),
  ('Dal Makhani',             'signature'),
  ('Palak Paneer',            'vegetarian'),
  ('Matar Paneer',            'vegetarian'),
  ('Rajma Masala',            'vegetarian'),
  ('Rajma Masala',            'healthy'),
  ('Baingan Bharta',          'vegetarian'),
  ('Butter Chicken',          'non-vegetarian'),
  ('Butter Chicken',          'bestseller'),
  ('Chicken Tikka Masala',    'non-vegetarian'),
  ('Chicken Tikka Masala',    'signature'),
  ('Mutton Rogan Josh',       'non-vegetarian'),
  ('Mutton Rogan Josh',       'spicy'),
  ('Prawn Masala',            'non-vegetarian'),
  ('Prawn Masala',            'premium'),
  ('Chicken Chettinad',       'non-vegetarian'),
  ('Chicken Chettinad',       'spicy'),
  -- SALADS & BOWLS
  ('Greek Salad Bowl',        'vegetarian'),
  ('Greek Salad Bowl',        'healthy'),
  ('Quinoa Veggie Bowl',      'vegetarian'),
  ('Quinoa Veggie Bowl',      'healthy'),
  ('Quinoa Veggie Bowl',      'new'),
  ('Paneer Power Bowl',       'vegetarian'),
  ('Paneer Power Bowl',       'healthy'),
  ('Paneer Power Bowl',       'signature'),
  ('Grilled Chicken Bowl',    'non-vegetarian'),
  ('Grilled Chicken Bowl',    'healthy'),
  ('Grilled Chicken Bowl',    'bestseller'),
  ('Prawn Avocado Bowl',      'non-vegetarian'),
  ('Prawn Avocado Bowl',      'healthy'),
  ('Prawn Avocado Bowl',      'new'),
  ('Tandoori Chicken Salad',  'non-vegetarian'),
  ('Tandoori Chicken Salad',  'healthy'),
  -- PIZZAS
  ('Paneer Tikka Pizza',      'vegetarian'),
  ('Paneer Tikka Pizza',      'bestseller'),
  ('Margherita',              'vegetarian'),
  ('Farm Fresh Veggie Pizza', 'vegetarian'),
  ('Chicken Tikka Pizza',     'non-vegetarian'),
  ('Chicken Tikka Pizza',     'bestseller'),
  ('BBQ Chicken Pizza',       'non-vegetarian'),
  ('Prawn & Pesto Pizza',     'non-vegetarian'),
  ('Prawn & Pesto Pizza',     'premium'),
  -- PASTAS
  ('Penne Arrabbiata',        'vegetarian'),
  ('Penne Arrabbiata',        'bestseller'),
  ('Spaghetti Aglio e Olio',  'vegetarian'),
  ('Pesto Fusilli',           'vegetarian'),
  ('Pesto Fusilli',           'signature'),
  ('Creamy Mushroom Pasta',   'vegetarian'),
  ('Creamy Mushroom Pasta',   'bestseller'),
  ('Pink Sauce Penne',        'vegetarian'),
  ('Pink Sauce Penne',        'new'),
  ('Mac & Cheese',            'vegetarian'),
  ('Chicken Bolognese',       'non-vegetarian'),
  ('Chicken Bolognese',       'bestseller'),
  ('Chicken Alfredo',         'non-vegetarian'),
  ('Chicken Alfredo',         'signature'),
  ('Prawn Linguine',          'non-vegetarian'),
  ('Prawn Linguine',          'premium'),
  ('Spicy Chicken Penne',     'non-vegetarian'),
  ('Spicy Chicken Penne',     'spicy'),
  -- BURGERS
  ('Crispy Paneer Burger',     'vegetarian'),
  ('Crispy Paneer Burger',     'bestseller'),
  ('Spicy Aloo Tikki Burger',  'vegetarian'),
  ('Chicken Tikka Burger',     'non-vegetarian'),
  ('Chicken Tikka Burger',     'bestseller'),
  ('Smoky BBQ Chicken Burger', 'non-vegetarian'),
  ('Smoky BBQ Chicken Burger', 'signature'),
  ('Prawn Po Boy',             'non-vegetarian'),
  ('Prawn Po Boy',             'new'),
  -- BREADS
  ('Butter Naan',              'vegetarian'),
  ('Butter Naan',              'bestseller'),
  ('Garlic Naan',              'vegetarian'),
  ('Garlic Naan',              'bestseller'),
  ('Peshwari Naan',            'vegetarian'),
  ('Peshwari Naan',            'signature'),
  ('Cheese Naan',              'vegetarian'),
  ('Cheese Naan',              'new'),
  ('Paneer Kulcha',            'vegetarian'),
  ('Paneer Kulcha',            'signature'),
  ('Aloo Kulcha',              'vegetarian'),
  ('Laccha Paratha',           'vegetarian'),
  ('Missi Roti',               'vegetarian'),
  ('Missi Roti',               'healthy'),
  -- RICE BOWLS
  ('Vegetable Dum Rice',       'vegetarian'),
  ('Vegetable Dum Rice',       'bestseller'),
  ('Paneer Dum Rice',          'vegetarian'),
  ('Paneer Dum Rice',          'signature'),
  ('Mushroom Rice Bowl',       'vegetarian'),
  ('Mushroom Rice Bowl',       'new'),
  ('Curd Rice',                'vegetarian'),
  ('Curd Rice',                'healthy'),
  ('Jeera Rice',               'vegetarian'),
  ('Chicken Dum Rice',         'non-vegetarian'),
  ('Chicken Dum Rice',         'bestseller'),
  ('Mutton Dum Rice',          'non-vegetarian'),
  ('Mutton Dum Rice',          'signature'),
  ('Prawn Rice Bowl',          'non-vegetarian'),
  ('Prawn Rice Bowl',          'premium'),
  ('Egg Rice Bowl',            'non-vegetarian'),
  -- DESSERTS
  ('Gulab Jamun',              'vegetarian'),
  ('Gulab Jamun',              'bestseller'),
  ('Rasmalai',                 'vegetarian'),
  ('Rasmalai',                 'signature'),
  ('Gajar Halwa',              'vegetarian'),
  ('Gajar Halwa',              'traditional'),
  ('Shahi Tukda',              'vegetarian'),
  ('Phirni',                   'vegetarian'),
  -- ICE CREAMS & SUNDAES
  ('Mango Kulfi',              'vegetarian'),
  ('Mango Kulfi',              'bestseller'),
  ('Kesar Pista Kulfi',        'vegetarian'),
  ('Kesar Pista Kulfi',        'signature'),
  ('Chocolate Sundae',         'vegetarian'),
  ('Chocolate Sundae',         'bestseller'),
  ('Brownie Sundae',           'vegetarian'),
  ('Brownie Sundae',           'new'),
  ('Strawberry Sundae',        'vegetarian'),
  -- BEVERAGES
  ('Mango Lassi',              'vegetarian'),
  ('Mango Lassi',              'bestseller'),
  ('Sweet Lassi',              'vegetarian'),
  ('Masala Chai',              'vegetarian'),
  ('Masala Chai',              'traditional'),
  ('Rose Milk',                'vegetarian'),
  ('Rose Milk',                'signature'),
  ('Jaljeera',                 'vegetarian'),
  ('Jaljeera',                 'healthy'),
  ('Tender Coconut Water',     'vegetarian'),
  ('Tender Coconut Water',     'healthy'),
  ('Mineral Water (500ml)',    'vegetarian'),
  ('Sparkling Water (500ml)',  'vegetarian')
) AS t(item_name, tag)
JOIN menu_items m ON m.name = t.item_name
WHERE NOT EXISTS (
  SELECT 1 FROM menu_item_tags
  WHERE menu_item_id = m.id AND tag = t.tag
);
