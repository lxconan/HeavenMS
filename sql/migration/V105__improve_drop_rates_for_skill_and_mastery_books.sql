UPDATE IGNORE temp_data
SET chance=1000
WHERE itemid >= 2280000
  and itemid < 2300000
  and chance < 1000;
