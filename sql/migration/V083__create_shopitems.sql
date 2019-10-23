CREATE TABLE IF NOT EXISTS `shopitems`
(
    `shopitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `shopid`     int(10) unsigned NOT NULL,
    `itemid`     int(11)          NOT NULL,
    `price`      int(11)          NOT NULL,
    `pitch`      int(11)          NOT NULL DEFAULT '0',
    `position`   int(11)          NOT NULL COMMENT 'sort is an arbitrary field designed to give leeway when modifying shops. The lowest number is 104 and it increments by 4 for each item to allow decent space for swapping/inserting/removing items.',
    PRIMARY KEY (`shopitemid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 20047
