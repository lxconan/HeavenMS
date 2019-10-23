CREATE TABLE IF NOT EXISTS `worldtransfers`
(
    `id`             int(11)    NOT NULL AUTO_INCREMENT,
    `characterid`    int(11)    NOT NULL,
    `from`           tinyint(3) NOT NULL,
    `to`             tinyint(3) NOT NULL,
    `requestTime`    timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `completionTime` timestamp  NULL,
    PRIMARY KEY (`id`),
    INDEX (characterid)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
