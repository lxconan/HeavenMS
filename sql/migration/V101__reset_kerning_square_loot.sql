DELETE
FROM temp_data
WHERE dropperid >= 4300006
  AND dropperid <= 4300013;

INSERT IGNORE INTO temp_data (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
VALUES (3400000, 1002098, 1, 1, 0, 1500),
       (3400000, 1002154, 1, 1, 0, 1500),
       (3400000, 1002170, 1, 1, 0, 1500),
       (3400000, 1002185, 1, 1, 0, 1500),
       (3400000, 1002628, 1, 1, 0, 1500),
       (3400000, 1072107, 1, 1, 0, 800),
       (3400000, 1072117, 1, 1, 0, 800),
       (3400000, 1072118, 1, 1, 0, 800),
       (3400000, 1072126, 1, 1, 0, 800),
       (3400000, 1072300, 1, 1, 0, 800),
       (3400000, 2000001, 1, 1, 0, 100000),
       (3400000, 2000003, 1, 1, 0, 100000),
       (3400000, 2000004, 1, 1, 0, 20000),
       (3400000, 2000006, 1, 1, 0, 100000),
       (3400000, 2022003, 1, 1, 0, 20000),
       (3400000, 2040004, 1, 1, 0, 300),
       (3400000, 2040504, 1, 1, 0, 300),
       (3400000, 2049208, 1, 1, 0, 300),
       (3400000, 2049210, 1, 1, 0, 300),
       (3400000, 4000539, 1, 1, 0, 600000),
       (3400000, 4004000, 1, 1, 0, 10000),
       (3400000, 4020007, 1, 1, 0, 9000),
       (3400001, 1002098, 1, 1, 0, 1500),
       (3400001, 1002154, 1, 1, 0, 1500),
       (3400001, 1002170, 1, 1, 0, 1500),
       (3400001, 1002185, 1, 1, 0, 1500),
       (3400001, 1002628, 1, 1, 0, 1500),
       (3400001, 1072107, 1, 1, 0, 800),
       (3400001, 1072117, 1, 1, 0, 800),
       (3400001, 1072118, 1, 1, 0, 800),
       (3400001, 1072126, 1, 1, 0, 800),
       (3400001, 1072300, 1, 1, 0, 800),
       (3400001, 2000001, 1, 1, 0, 100000),
       (3400001, 2000003, 1, 1, 0, 100000),
       (3400001, 2000004, 1, 1, 0, 20000),
       (3400001, 2000006, 1, 1, 0, 100000),
       (3400001, 2022003, 1, 1, 0, 20000),
       (3400001, 2040004, 1, 1, 0, 300),
       (3400001, 2040504, 1, 1, 0, 300),
       (3400001, 2049208, 1, 1, 0, 300),
       (3400001, 2049210, 1, 1, 0, 300),
       (3400001, 4000541, 1, 1, 0, 600000),
       (3400001, 4004000, 1, 1, 0, 10000),
       (3400001, 4020007, 1, 1, 0, 9000),
       (3400002, 1002098, 1, 1, 0, 1500),
       (3400002, 1002154, 1, 1, 0, 1500),
       (3400002, 1002170, 1, 1, 0, 1500),
       (3400002, 1002185, 1, 1, 0, 1500),
       (3400002, 1002628, 1, 1, 0, 1500),
       (3400002, 1072107, 1, 1, 0, 800),
       (3400002, 1072117, 1, 1, 0, 800),
       (3400002, 1072118, 1, 1, 0, 800),
       (3400002, 1072126, 1, 1, 0, 800),
       (3400002, 1072300, 1, 1, 0, 800),
       (3400002, 2000001, 1, 1, 0, 100000),
       (3400002, 2000003, 1, 1, 0, 100000),
       (3400002, 2000004, 1, 1, 0, 20000),
       (3400002, 2000006, 1, 1, 0, 100000),
       (3400002, 2022003, 1, 1, 0, 20000),
       (3400002, 2040004, 1, 1, 0, 300),
       (3400002, 2040504, 1, 1, 0, 300),
       (3400002, 2049208, 1, 1, 0, 300),
       (3400002, 2049210, 1, 1, 0, 300),
       (3400002, 4000540, 1, 1, 0, 600000),
       (3400002, 4004000, 1, 1, 0, 10000),
       (3400002, 4020007, 1, 1, 0, 9000),
       (3400003, 4032508, 1, 1, 2273, 999999),
       (3400004, 4000542, 1, 1, 0, 400000),
       (3400005, 4032508, 1, 1, 2273, 999999),
       (3400006, 4000543, 1, 1, 0, 400000),
       (3400008, 4000544, 1, 1, 0, 400000),
       (4300001, 1302009, 1, 1, 0, 700),
       (4300001, 1312007, 1, 1, 0, 700),
       (4300001, 1322016, 1, 1, 0, 700),
       (4300001, 1332011, 1, 1, 0, 500),
       (4300001, 1332031, 1, 1, 0, 500),
       (4300001, 1382019, 1, 1, 0, 700),
       (4300001, 1402007, 1, 1, 0, 700),
       (4300001, 1412005, 1, 1, 0, 700),
       (4300001, 1422007, 1, 1, 0, 700),
       (4300001, 1432005, 1, 1, 0, 500),
       (4300001, 1442009, 1, 1, 0, 700),
       (4300001, 1452007, 1, 1, 0, 500),
       (4300001, 1472014, 1, 1, 0, 500),
       (4300001, 1472015, 1, 1, 0, 500),
       (4300001, 1472016, 1, 1, 0, 500),
       (4300001, 1472017, 1, 1, 0, 500),
       (4300001, 1482006, 1, 1, 0, 500),
       (4300001, 1492006, 1, 1, 0, 500),
       (4300001, 2000001, 1, 1, 0, 100000),
       (4300001, 2000003, 1, 1, 0, 100000),
       (4300001, 2000004, 1, 1, 0, 20000),
       (4300001, 2000006, 1, 1, 0, 100000),
       (4300001, 2022003, 1, 1, 0, 20000),
       (4300001, 2040004, 1, 1, 0, 300),
       (4300001, 2040501, 1, 1, 0, 300),
       (4300001, 2040504, 1, 1, 0, 300),
       (4300001, 2040801, 1, 1, 0, 300),
       (4300001, 2041004, 1, 1, 0, 300),
       (4300001, 2041007, 1, 1, 0, 300),
       (4300001, 2049200, 1, 1, 0, 300),
       (4300001, 2049202, 1, 1, 0, 300),
       (4300001, 2049204, 1, 1, 0, 300),
       (4300001, 2049206, 1, 1, 0, 300),
       (4300001, 4000530, 1, 1, 0, 600000),
       (4300001, 4004000, 1, 1, 0, 10000),
       (4300001, 4020008, 1, 1, 0, 9000),
       (4300003, 1302009, 1, 1, 0, 700),
       (4300003, 1312007, 1, 1, 0, 700),
       (4300003, 1322016, 1, 1, 0, 700),
       (4300003, 1332011, 1, 1, 0, 500),
       (4300003, 1332031, 1, 1, 0, 500),
       (4300003, 1382019, 1, 1, 0, 700),
       (4300003, 1402007, 1, 1, 0, 700),
       (4300003, 1412005, 1, 1, 0, 700),
       (4300003, 1422007, 1, 1, 0, 700),
       (4300003, 1432005, 1, 1, 0, 500),
       (4300003, 1442009, 1, 1, 0, 700),
       (4300003, 1452007, 1, 1, 0, 500),
       (4300003, 1472014, 1, 1, 0, 500),
       (4300003, 1472015, 1, 1, 0, 500),
       (4300003, 1472016, 1, 1, 0, 500),
       (4300003, 1472017, 1, 1, 0, 500),
       (4300003, 1482006, 1, 1, 0, 500),
       (4300003, 1492006, 1, 1, 0, 500),
       (4300003, 2000001, 1, 1, 0, 100000),
       (4300003, 2000003, 1, 1, 0, 100000),
       (4300003, 2000004, 1, 1, 0, 20000),
       (4300003, 2000006, 1, 1, 0, 100000),
       (4300003, 2022003, 1, 1, 0, 20000),
       (4300003, 2040004, 1, 1, 0, 300),
       (4300003, 2040501, 1, 1, 0, 300),
       (4300003, 2040504, 1, 1, 0, 300),
       (4300003, 2040801, 1, 1, 0, 300),
       (4300003, 2041004, 1, 1, 0, 300),
       (4300003, 2041007, 1, 1, 0, 300),
       (4300003, 2049200, 1, 1, 0, 300),
       (4300003, 2049202, 1, 1, 0, 300),
       (4300003, 2049204, 1, 1, 0, 300),
       (4300003, 2049206, 1, 1, 0, 300),
       (4300003, 4000532, 1, 1, 0, 600000),
       (4300003, 4004000, 1, 1, 0, 10000),
       (4300003, 4020008, 1, 1, 0, 9000),
       (4300005, 1302009, 1, 1, 0, 700),
       (4300005, 1312007, 1, 1, 0, 700),
       (4300005, 1322016, 1, 1, 0, 700),
       (4300005, 1332011, 1, 1, 0, 500),
       (4300005, 1332031, 1, 1, 0, 500),
       (4300005, 1382019, 1, 1, 0, 700),
       (4300005, 1402007, 1, 1, 0, 700),
       (4300005, 1412005, 1, 1, 0, 700),
       (4300005, 1422007, 1, 1, 0, 700),
       (4300005, 1432005, 1, 1, 0, 500),
       (4300005, 1442009, 1, 1, 0, 700),
       (4300005, 1452007, 1, 1, 0, 500),
       (4300005, 1472014, 1, 1, 0, 500),
       (4300005, 1472015, 1, 1, 0, 500),
       (4300005, 1472016, 1, 1, 0, 500),
       (4300005, 1472017, 1, 1, 0, 500),
       (4300005, 1482006, 1, 1, 0, 500),
       (4300005, 1492006, 1, 1, 0, 500),
       (4300005, 2000001, 1, 1, 0, 100000),
       (4300005, 2000003, 1, 1, 0, 100000),
       (4300005, 2000004, 1, 1, 0, 20000),
       (4300005, 2000006, 1, 1, 0, 100000),
       (4300005, 2022003, 1, 1, 0, 20000),
       (4300005, 2040004, 1, 1, 0, 300),
       (4300005, 2040501, 1, 1, 0, 300),
       (4300005, 2040504, 1, 1, 0, 300),
       (4300005, 2040801, 1, 1, 0, 300),
       (4300005, 2041004, 1, 1, 0, 300),
       (4300005, 2041007, 1, 1, 0, 300),
       (4300005, 2049200, 1, 1, 0, 300),
       (4300005, 2049202, 1, 1, 0, 300),
       (4300005, 2049204, 1, 1, 0, 300),
       (4300005, 2049206, 1, 1, 0, 300),
       (4300005, 4000534, 1, 1, 0, 600000),
       (4300005, 4004000, 1, 1, 0, 10000),
       (4300005, 4020008, 1, 1, 0, 9000),
       (4300006, 1302009, 1, 1, 0, 700),
       (4300006, 1312007, 1, 1, 0, 700),
       (4300006, 1322016, 1, 1, 0, 700),
       (4300006, 1332011, 1, 1, 0, 500),
       (4300006, 1332031, 1, 1, 0, 500),
       (4300006, 1382019, 1, 1, 0, 700),
       (4300006, 1402007, 1, 1, 0, 700),
       (4300006, 1412005, 1, 1, 0, 700),
       (4300006, 1422007, 1, 1, 0, 700),
       (4300006, 1432005, 1, 1, 0, 500),
       (4300006, 1442009, 1, 1, 0, 700),
       (4300006, 1452007, 1, 1, 0, 500),
       (4300006, 1472014, 1, 1, 0, 500),
       (4300006, 1472015, 1, 1, 0, 500),
       (4300006, 1472016, 1, 1, 0, 500),
       (4300006, 1472017, 1, 1, 0, 500),
       (4300006, 1482006, 1, 1, 0, 500),
       (4300006, 1492006, 1, 1, 0, 500),
       (4300006, 2000001, 1, 1, 0, 100000),
       (4300006, 2000003, 1, 1, 0, 100000),
       (4300006, 2000004, 1, 1, 0, 20000),
       (4300006, 2000006, 1, 1, 0, 100000),
       (4300006, 2022003, 1, 1, 0, 20000),
       (4300006, 2040004, 1, 1, 0, 300),
       (4300006, 2040501, 1, 1, 0, 300),
       (4300006, 2040504, 1, 1, 0, 300),
       (4300006, 2040801, 1, 1, 0, 300),
       (4300006, 2041004, 1, 1, 0, 300),
       (4300006, 2041007, 1, 1, 0, 300),
       (4300006, 2049200, 1, 1, 0, 300),
       (4300006, 2049202, 1, 1, 0, 300),
       (4300006, 2049204, 1, 1, 0, 300),
       (4300006, 2049206, 1, 1, 0, 300),
       (4300006, 4000525, 1, 1, 0, 600000),
       (4300006, 4004000, 1, 1, 0, 10000),
       (4300006, 4020008, 1, 1, 0, 9000),
       (4300006, 4032506, 1, 1, 2277, 80000),
       (4300007, 1302009, 1, 1, 0, 700),
       (4300007, 1312007, 1, 1, 0, 700),
       (4300007, 1322016, 1, 1, 0, 700),
       (4300007, 1332011, 1, 1, 0, 500),
       (4300007, 1332031, 1, 1, 0, 500),
       (4300007, 1382019, 1, 1, 0, 700),
       (4300007, 1402007, 1, 1, 0, 700),
       (4300007, 1412005, 1, 1, 0, 700),
       (4300007, 1422007, 1, 1, 0, 700),
       (4300007, 1432005, 1, 1, 0, 500),
       (4300007, 1442009, 1, 1, 0, 700),
       (4300007, 1452007, 1, 1, 0, 500),
       (4300007, 1472014, 1, 1, 0, 500),
       (4300007, 1472015, 1, 1, 0, 500),
       (4300007, 1472016, 1, 1, 0, 500),
       (4300007, 1472017, 1, 1, 0, 500),
       (4300007, 1482006, 1, 1, 0, 500),
       (4300007, 1492006, 1, 1, 0, 500),
       (4300007, 2000001, 1, 1, 0, 100000),
       (4300007, 2000003, 1, 1, 0, 100000),
       (4300007, 2000004, 1, 1, 0, 20000),
       (4300007, 2000006, 1, 1, 0, 100000),
       (4300007, 2022003, 1, 1, 0, 20000),
       (4300007, 2040004, 1, 1, 0, 300),
       (4300007, 2040501, 1, 1, 0, 300),
       (4300007, 2040504, 1, 1, 0, 300),
       (4300007, 2040801, 1, 1, 0, 300),
       (4300007, 2041004, 1, 1, 0, 300),
       (4300007, 2041007, 1, 1, 0, 300),
       (4300007, 2049200, 1, 1, 0, 300),
       (4300007, 2049202, 1, 1, 0, 300),
       (4300007, 2049204, 1, 1, 0, 300),
       (4300007, 2049206, 1, 1, 0, 300),
       (4300007, 4000526, 1, 1, 0, 600000),
       (4300007, 4004000, 1, 1, 0, 10000),
       (4300007, 4020008, 1, 1, 0, 9000),
       (4300007, 4032506, 1, 1, 2277, 80000),
       (4300008, 1302009, 1, 1, 0, 700),
       (4300008, 1312007, 1, 1, 0, 700),
       (4300008, 1322016, 1, 1, 0, 700),
       (4300008, 1332011, 1, 1, 0, 500),
       (4300008, 1332031, 1, 1, 0, 500),
       (4300008, 1382019, 1, 1, 0, 700),
       (4300008, 1402007, 1, 1, 0, 700),
       (4300008, 1412005, 1, 1, 0, 700),
       (4300008, 1422007, 1, 1, 0, 700),
       (4300008, 1432005, 1, 1, 0, 500),
       (4300008, 1442009, 1, 1, 0, 700),
       (4300008, 1452007, 1, 1, 0, 500),
       (4300008, 1472014, 1, 1, 0, 500),
       (4300008, 1472015, 1, 1, 0, 500),
       (4300008, 1472016, 1, 1, 0, 500),
       (4300008, 1472017, 1, 1, 0, 500),
       (4300008, 1482006, 1, 1, 0, 500),
       (4300008, 1492006, 1, 1, 0, 500),
       (4300008, 2000001, 1, 1, 0, 100000),
       (4300008, 2000003, 1, 1, 0, 100000),
       (4300008, 2000004, 1, 1, 0, 20000),
       (4300008, 2000006, 1, 1, 0, 100000),
       (4300008, 2022003, 1, 1, 0, 20000),
       (4300008, 2040004, 1, 1, 0, 300),
       (4300008, 2040501, 1, 1, 0, 300),
       (4300008, 2040504, 1, 1, 0, 300),
       (4300008, 2040801, 1, 1, 0, 300),
       (4300008, 2041004, 1, 1, 0, 300),
       (4300008, 2041007, 1, 1, 0, 300),
       (4300008, 2049200, 1, 1, 0, 300),
       (4300008, 2049202, 1, 1, 0, 300),
       (4300008, 2049204, 1, 1, 0, 300),
       (4300008, 2049206, 1, 1, 0, 300),
       (4300008, 4000527, 1, 1, 0, 400000),
       (4300008, 4004000, 1, 1, 0, 10000),
       (4300008, 4020008, 1, 1, 0, 9000),
       (4300008, 4032506, 1, 1, 2277, 80000),
       (4300009, 1302009, 1, 1, 0, 700),
       (4300009, 1312007, 1, 1, 0, 700),
       (4300009, 1322016, 1, 1, 0, 700),
       (4300009, 1332011, 1, 1, 0, 500),
       (4300009, 1332031, 1, 1, 0, 500),
       (4300009, 1382019, 1, 1, 0, 700),
       (4300009, 1402007, 1, 1, 0, 700),
       (4300009, 1412005, 1, 1, 0, 700),
       (4300009, 1422007, 1, 1, 0, 700),
       (4300009, 1432005, 1, 1, 0, 500),
       (4300009, 1442009, 1, 1, 0, 700),
       (4300009, 1452007, 1, 1, 0, 500),
       (4300009, 1472014, 1, 1, 0, 500),
       (4300009, 1472015, 1, 1, 0, 500),
       (4300009, 1472016, 1, 1, 0, 500),
       (4300009, 1472017, 1, 1, 0, 500),
       (4300009, 1482006, 1, 1, 0, 500),
       (4300009, 1492006, 1, 1, 0, 500),
       (4300009, 2000001, 1, 1, 0, 100000),
       (4300009, 2000003, 1, 1, 0, 100000),
       (4300009, 2000004, 1, 1, 0, 20000),
       (4300009, 2000006, 1, 1, 0, 100000),
       (4300009, 2022003, 1, 1, 0, 20000),
       (4300009, 2040004, 1, 1, 0, 300),
       (4300009, 2040501, 1, 1, 0, 300),
       (4300009, 2040504, 1, 1, 0, 300),
       (4300009, 2040801, 1, 1, 0, 300),
       (4300009, 2041004, 1, 1, 0, 300),
       (4300009, 2041007, 1, 1, 0, 300),
       (4300009, 2049200, 1, 1, 0, 300),
       (4300009, 2049202, 1, 1, 0, 300),
       (4300009, 2049204, 1, 1, 0, 300),
       (4300009, 2049206, 1, 1, 0, 300),
       (4300009, 4000528, 1, 1, 0, 600000),
       (4300009, 4004000, 1, 1, 0, 10000),
       (4300009, 4020008, 1, 1, 0, 9000),
       (4300010, 1302009, 1, 1, 0, 700),
       (4300010, 1312007, 1, 1, 0, 700),
       (4300010, 1322016, 1, 1, 0, 700),
       (4300010, 1332011, 1, 1, 0, 500),
       (4300010, 1332031, 1, 1, 0, 500),
       (4300010, 1382019, 1, 1, 0, 700),
       (4300010, 1402007, 1, 1, 0, 700),
       (4300010, 1412005, 1, 1, 0, 700),
       (4300010, 1422007, 1, 1, 0, 700),
       (4300010, 1432005, 1, 1, 0, 500),
       (4300010, 1442009, 1, 1, 0, 700),
       (4300010, 1452007, 1, 1, 0, 500),
       (4300010, 1472014, 1, 1, 0, 500),
       (4300010, 1472015, 1, 1, 0, 500),
       (4300010, 1472016, 1, 1, 0, 500),
       (4300010, 1472017, 1, 1, 0, 500),
       (4300010, 1482006, 1, 1, 0, 500),
       (4300010, 1492006, 1, 1, 0, 500),
       (4300010, 2000001, 1, 1, 0, 100000),
       (4300010, 2000003, 1, 1, 0, 100000),
       (4300010, 2000004, 1, 1, 0, 20000),
       (4300010, 2000006, 1, 1, 0, 100000),
       (4300010, 2022003, 1, 1, 0, 20000),
       (4300010, 2040004, 1, 1, 0, 300),
       (4300010, 2040501, 1, 1, 0, 300),
       (4300010, 2040504, 1, 1, 0, 300),
       (4300010, 2040801, 1, 1, 0, 300),
       (4300010, 2041004, 1, 1, 0, 300),
       (4300010, 2041007, 1, 1, 0, 300),
       (4300010, 2049200, 1, 1, 0, 300),
       (4300010, 2049202, 1, 1, 0, 300),
       (4300010, 2049204, 1, 1, 0, 300),
       (4300010, 2049206, 1, 1, 0, 300),
       (4300010, 4000529, 1, 1, 0, 600000),
       (4300010, 4004000, 1, 1, 0, 10000),
       (4300010, 4020008, 1, 1, 0, 9000),
       (4300011, 1302009, 1, 1, 0, 700),
       (4300011, 1312007, 1, 1, 0, 700),
       (4300011, 1322016, 1, 1, 0, 700),
       (4300011, 1332011, 1, 1, 0, 500),
       (4300011, 1332031, 1, 1, 0, 500),
       (4300011, 1382019, 1, 1, 0, 700),
       (4300011, 1402007, 1, 1, 0, 700),
       (4300011, 1412005, 1, 1, 0, 700),
       (4300011, 1422007, 1, 1, 0, 700),
       (4300011, 1432005, 1, 1, 0, 500),
       (4300011, 1442009, 1, 1, 0, 700),
       (4300011, 1452007, 1, 1, 0, 500),
       (4300011, 1472014, 1, 1, 0, 500),
       (4300011, 1472015, 1, 1, 0, 500),
       (4300011, 1472016, 1, 1, 0, 500),
       (4300011, 1472017, 1, 1, 0, 500),
       (4300011, 1482006, 1, 1, 0, 500),
       (4300011, 1492006, 1, 1, 0, 500),
       (4300011, 2000001, 1, 1, 0, 100000),
       (4300011, 2000003, 1, 1, 0, 100000),
       (4300011, 2000004, 1, 1, 0, 20000),
       (4300011, 2000006, 1, 1, 0, 100000),
       (4300011, 2022003, 1, 1, 0, 20000),
       (4300011, 2040004, 1, 1, 0, 300),
       (4300011, 2040501, 1, 1, 0, 300),
       (4300011, 2040504, 1, 1, 0, 300),
       (4300011, 2040801, 1, 1, 0, 300),
       (4300011, 2041004, 1, 1, 0, 300),
       (4300011, 2041007, 1, 1, 0, 300),
       (4300011, 2049200, 1, 1, 0, 300),
       (4300011, 2049202, 1, 1, 0, 300),
       (4300011, 2049204, 1, 1, 0, 300),
       (4300011, 2049206, 1, 1, 0, 300),
       (4300011, 4000536, 1, 1, 0, 600000),
       (4300011, 4004000, 1, 1, 0, 10000),
       (4300011, 4020008, 1, 1, 0, 9000),
       (4300011, 4032509, 1, 1, 2286, 70000),
       (4300012, 1302009, 1, 1, 0, 700),
       (4300012, 1312007, 1, 1, 0, 700),
       (4300012, 1322016, 1, 1, 0, 700),
       (4300012, 1332011, 1, 1, 0, 500),
       (4300012, 1332031, 1, 1, 0, 500),
       (4300012, 1382019, 1, 1, 0, 700),
       (4300012, 1402007, 1, 1, 0, 700),
       (4300012, 1412005, 1, 1, 0, 700),
       (4300012, 1422007, 1, 1, 0, 700),
       (4300012, 1432005, 1, 1, 0, 500),
       (4300012, 1442009, 1, 1, 0, 700),
       (4300012, 1452007, 1, 1, 0, 500),
       (4300012, 1472014, 1, 1, 0, 500),
       (4300012, 1472015, 1, 1, 0, 500),
       (4300012, 1472016, 1, 1, 0, 500),
       (4300012, 1472017, 1, 1, 0, 500),
       (4300012, 1482006, 1, 1, 0, 500),
       (4300012, 1492006, 1, 1, 0, 500),
       (4300012, 2000001, 1, 1, 0, 100000),
       (4300012, 2000003, 1, 1, 0, 100000),
       (4300012, 2000004, 1, 1, 0, 20000),
       (4300012, 2000006, 1, 1, 0, 100000),
       (4300012, 2022003, 1, 1, 0, 20000),
       (4300012, 2040004, 1, 1, 0, 300),
       (4300012, 2040501, 1, 1, 0, 300),
       (4300012, 2040504, 1, 1, 0, 300),
       (4300012, 2040801, 1, 1, 0, 300),
       (4300012, 2041004, 1, 1, 0, 300),
       (4300012, 2041007, 1, 1, 0, 300),
       (4300012, 2049200, 1, 1, 0, 300),
       (4300012, 2049202, 1, 1, 0, 300),
       (4300012, 2049204, 1, 1, 0, 300),
       (4300012, 2049206, 1, 1, 0, 300),
       (4300012, 4000537, 1, 1, 0, 600000),
       (4300012, 4004000, 1, 1, 0, 10000),
       (4300012, 4020008, 1, 1, 0, 9000),
       (4300013, 1302009, 1, 1, 0, 7000),
       (4300013, 1312007, 1, 1, 0, 7000),
       (4300013, 1322016, 1, 1, 0, 7000),
       (4300013, 1332011, 1, 1, 0, 5000),
       (4300013, 1332031, 1, 1, 0, 5000),
       (4300013, 1382019, 1, 1, 0, 7000),
       (4300013, 1402007, 1, 1, 0, 7000),
       (4300013, 1412005, 1, 1, 0, 7000),
       (4300013, 1422007, 1, 1, 0, 7000),
       (4300013, 1432005, 1, 1, 0, 5000),
       (4300013, 1442009, 1, 1, 0, 7000),
       (4300013, 1452007, 1, 1, 0, 5000),
       (4300013, 1472014, 1, 1, 0, 5000),
       (4300013, 1472015, 1, 1, 0, 5000),
       (4300013, 1472016, 1, 1, 0, 5000),
       (4300013, 1472017, 1, 1, 0, 5000),
       (4300013, 1482006, 1, 1, 0, 5000),
       (4300013, 1492006, 1, 1, 0, 5000),
       (4300013, 2000001, 1, 1, 0, 1000000),
       (4300013, 2000003, 1, 1, 0, 1000000),
       (4300013, 2000004, 1, 1, 0, 999999),
       (4300013, 2000006, 1, 1, 0, 4999995),
       (4300013, 2022003, 1, 1, 0, 200000),
       (4300013, 2040004, 1, 1, 0, 3000),
       (4300013, 2040501, 1, 1, 0, 3000),
       (4300013, 2040504, 1, 1, 0, 3000),
       (4300013, 2040801, 1, 1, 0, 3000),
       (4300013, 2041004, 1, 1, 0, 3000),
       (4300013, 2041007, 1, 1, 0, 3000),
       (4300013, 2049200, 1, 1, 0, 3000),
       (4300013, 2049201, 1, 1, 0, 3000),
       (4300013, 2049202, 1, 1, 0, 3000),
       (4300013, 2049203, 1, 1, 0, 3000),
       (4300013, 2049204, 1, 1, 0, 3000),
       (4300013, 2049205, 1, 1, 0, 3000),
       (4300013, 2049206, 1, 1, 0, 3000),
       (4300013, 2049207, 1, 1, 0, 3000),
       (4300013, 4004000, 1, 1, 0, 100000),
       (4300013, 4020008, 1, 1, 0, 90000),
       (4300015, 4032509, 1, 1, 2286, 70000),
       (4300016, 4000537, 1, 1, 0, 400000),
       (3400000, 0, 50, 90, 0, 400000),
       (3400001, 0, 60, 90, 0, 400000),
       (3400002, 0, 70, 100, 0, 400000),
       (3400003, 0, 60, 70, 0, 400000),
       (3400004, 0, 60, 80, 0, 400000),
       (3400005, 0, 80, 85, 0, 400000),
       (3400006, 0, 80, 90, 0, 400000),
       (3400008, 0, 100, 110, 0, 400000),
       (4300001, 0, 100, 120, 0, 400000),
       (4300003, 0, 100, 120, 0, 400000),
       (4300005, 0, 100, 120, 0, 400000),
       (4300006, 0, 110, 120, 0, 400000),
       (4300007, 0, 110, 140, 0, 400000),
       (4300008, 0, 110, 140, 0, 400000),
       (4300009, 0, 100, 120, 0, 400000),
       (4300010, 0, 100, 110, 0, 400000),
       (4300011, 0, 110, 115, 0, 400000),
       (4300012, 0, 120, 140, 0, 400000),
       (4300013, 0, 500, 700, 0, 400000),
       (4300014, 0, 100, 110, 0, 400000),
       (4300015, 0, 110, 115, 0, 400000),
       (4300016, 0, 120, 140, 0, 400000),
       (4300017, 0, 540, 800, 0, 400000);
