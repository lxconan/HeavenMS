CREATE TABLE IF NOT EXISTS `namechanges`
(
    `id`             int(11)     NOT NULL AUTO_INCREMENT,
    `characterid`    int(11)     NOT NULL,
    `old`            varchar(13) NOT NULL,
    `new`            varchar(13) NOT NULL,
    `requestTime`    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `completionTime` timestamp   NULL,
    PRIMARY KEY (`id`),
    INDEX (characterid)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
