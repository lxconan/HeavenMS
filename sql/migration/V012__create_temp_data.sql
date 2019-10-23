CREATE TABLE IF NOT EXISTS `temp_data`
(
    `dropperid`        int(11) NOT NULL,
    `itemid`           int(11) NOT NULL DEFAULT '0',
    `minimum_quantity` int(11) NOT NULL DEFAULT '1',
    `maximum_quantity` int(11) NOT NULL DEFAULT '1',
    `questid`          int(11) NOT NULL DEFAULT '0',
    `chance`           int(11) NOT NULL DEFAULT '0',
    PRIMARY KEY (`dropperid`, `itemid`),
    KEY `mobid` (`dropperid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 0
