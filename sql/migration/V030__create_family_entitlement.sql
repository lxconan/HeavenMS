CREATE TABLE IF NOT EXISTS `family_entitlement`
(
    `id`            int(11)    NOT NULL AUTO_INCREMENT,
    `charid`        int(11)    NOT NULL,
    `entitlementid` int(11)    NOT NULL,
    `timestamp`     BIGINT(20) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    INDEX (charid)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
