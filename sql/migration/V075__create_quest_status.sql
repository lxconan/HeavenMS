CREATE TABLE IF NOT EXISTS `queststatus`
(
    `queststatusid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `characterid`   int(11)          NOT NULL DEFAULT '0',
    `quest`         int(11)          NOT NULL DEFAULT '0',
    `status`        int(11)          NOT NULL DEFAULT '0',
    `time`          int(11)          NOT NULL DEFAULT '0',
    `expires`       bigint(20)       NOT NULL DEFAULT '0',
    `forfeited`     int(11)          NOT NULL DEFAULT '0',
    `completed`     int(11)          NOT NULL DEFAULT '0',
    `info`          tinyint(3)       NOT NULL DEFAULT '0',
    PRIMARY KEY (`queststatusid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
