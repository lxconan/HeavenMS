CREATE TABLE IF NOT EXISTS `makercreatedata`
(
    `id`              tinyint(3) unsigned NOT NULL,
    `itemid`          int(11)             NOT NULL,
    `req_level`       tinyint(3) unsigned NOT NULL,
    `req_maker_level` tinyint(3) unsigned NOT NULL,
    `req_meso`        int(11)             NOT NULL,
    `req_item`        int(11)             NOT NULL,
    `req_equip`       int(11)             NOT NULL,
    `catalyst`        int(11)             NOT NULL,
    `quantity`        smallint(6)         NOT NULL,
    `tuc`             tinyint(3)          NOT NULL,
    PRIMARY KEY (`id`, `itemid`)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
