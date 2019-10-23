CREATE TABLE IF NOT EXISTS `drop_data_global`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `continent`        tinyint(1) NOT NULL DEFAULT '-1',
    `itemid`           int(11)    NOT NULL DEFAULT '0',
    `minimum_quantity` int(11)    NOT NULL DEFAULT '1',
    `maximum_quantity` int(11)    NOT NULL DEFAULT '1',
    `questid`          int(11)    NOT NULL DEFAULT '0',
    `chance`           int(11)    NOT NULL DEFAULT '0',
    `comments`         varchar(45)         DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `mobid` (`continent`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  ROW_FORMAT = DYNAMIC
  AUTO_INCREMENT = 5
