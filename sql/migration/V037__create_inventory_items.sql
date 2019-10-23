CREATE TABLE IF NOT EXISTS `inventoryitems`
(
    `inventoryitemid` int(10) unsigned    NOT NULL AUTO_INCREMENT,
    `type`            tinyint(3) unsigned NOT NULL,
    `characterid`     int(11)                      DEFAULT NULL,
    `accountid`       int(11)                      DEFAULT NULL,
    `itemid`          int(11)             NOT NULL DEFAULT '0',
    `inventorytype`   int(11)             NOT NULL DEFAULT '0',
    `position`        int(11)             NOT NULL DEFAULT '0',
    `quantity`        int(11)             NOT NULL DEFAULT '0',
    `owner`           tinytext            NOT NULL,
    `petid`           int(11) unsigned UNIQUE      DEFAULT NULL,
    `flag`            int(11)             NOT NULL,
    `expiration`      bigint(20)          NOT NULL DEFAULT '-1',
    `giftFrom`        varchar(26)         NOT NULL,
    PRIMARY KEY (`inventoryitemid`),
    KEY `CHARID` (`characterid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
