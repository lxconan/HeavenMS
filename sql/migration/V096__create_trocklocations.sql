CREATE TABLE IF NOT EXISTS `trocklocations`
(
    `trockid`     int(11) NOT NULL AUTO_INCREMENT,
    `characterid` int(11) NOT NULL,
    `mapid`       int(11) NOT NULL,
    `vip`         int(2)  NOT NULL,
    PRIMARY KEY (`trockid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1