CREATE TABLE IF NOT EXISTS `pets`
(
    `petid`     int(11) unsigned NOT NULL AUTO_INCREMENT,
    `name`      varchar(13)               DEFAULT NULL,
    `level`     int(10) unsigned NOT NULL,
    `closeness` int(10) unsigned NOT NULL,
    `fullness`  int(10) unsigned NOT NULL,
    `summoned`  tinyint(1)       NOT NULL DEFAULT '0',
    `flag`      int(10) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY (`petid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1;

ALTER TABLE `inventoryitems` ADD CONSTRAINT `fk_itempetid` FOREIGN KEY (`petid`) REFERENCES `pets` (`petid`) ON DELETE SET NULL
