CREATE TABLE IF NOT EXISTS `dueyitems`
(
    `id`              int(10) unsigned NOT NULL AUTO_INCREMENT,
    `PackageId`       int(10) unsigned NOT NULL DEFAULT '0',
    `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `INVENTORYITEMID` (`inventoryitemid`),
    KEY `PackageId` (`PackageId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
