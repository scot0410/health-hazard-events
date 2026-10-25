# health-hazard-events

https://www.fsis.usda.gov/fsis/api/recall/v/1Your 
Category: This API covers Meat, Poultry, and Egg products.
Data Type: Live, real-time JSON event feed. [1] (https://www.fsis.usda.gov/science-data/developer-resources/recall-api), [2] (https://play.google.com/store/apps/details?id=com.centerforrecallsafety.recallsentry), [3] (https://datadashboard.fda.gov/oii/cd/recalls.htm), [4] (https://www.fsis.usda.gov/science-data/developer-resources/recall-api)


https://www.fda.gov/about-fda/contact-fda/stay-informed/rss-feeds/food-safety-recalls/rss.xml
https://www.fda.gov/about-fda/contact-fda/stay-informed/rss-feeds/recalls/rss.xml
Category: The specific /food-safety-recalls/rss.xml subpath handles Food and Pet Food managed by the FDA, 
the parent directory provides access to the master feed /recalls/rss.xml. 
The master feed provides live real-time XML data on all FDA-regulated items, which includes:General Food & Produce (vegetables, dairy, seafood, etc.)Human & Animal DrugsCosmeticsMedical Devices & Tobacco

Data Type: Live, real-time news stream (limited to the most recent 10–15 records).

https://fda.gov
https://api.fda.gov/food/enforcement.json/?search={search}
Category: Gets historic info related to food recalls.
This is the official openFDA database. It tracks the comprehensive historical archive of food recalls dating back decades. It updates weekly and has a processing lag, making it perfect for seeding a database but unsuitable for real-time alerts. 
(Note: If you want historical Drug or Cosmetic data, you change /food/ in the URL path to /drug/ or /cosmetic/)