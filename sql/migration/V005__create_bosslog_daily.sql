CREATE TABLE IF NOT EXISTS `bosslog_daily`
(
    `id`          int(11)                                                   NOT NULL AUTO_INCREMENT,
    `characterid` int(11)                                                   NOT NULL,
    `bosstype`    enum ('ZAKUM','HORNTAIL','PINKBEAN','SCARGA','PAPULATUS') NOT NULL,
    `attempttime` timestamp                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
