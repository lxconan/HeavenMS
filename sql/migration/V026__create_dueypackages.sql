CREATE TABLE IF NOT EXISTS `dueypackages`
(
    `PackageId`  int(10) unsigned NOT NULL AUTO_INCREMENT,
    `ReceiverId` int(10) unsigned NOT NULL,
    `SenderName` varchar(13)      NOT NULL,
    `Mesos`      int(10) unsigned          DEFAULT '0',
    `TimeStamp`  timestamp        NOT NULL DEFAULT '2015-01-01 05:00:00',
    `Message`    varchar(200)     NULL,
    `Checked`    tinyint(1) unsigned       DEFAULT '1',
    `Type`       tinyint(1) unsigned       DEFAULT '0',
    PRIMARY KEY (`PackageId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
