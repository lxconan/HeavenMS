CREATE TABLE IF NOT EXISTS `questrequirements`
(
    `questrequirementid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `questid`            int(11)          NOT NULL DEFAULT '0',
    `status`             int(11)          NOT NULL DEFAULT '0',
    `data`               blob             NOT NULL,
    PRIMARY KEY (`questrequirementid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
