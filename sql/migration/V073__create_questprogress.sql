CREATE TABLE IF NOT EXISTS `questprogress`
(
    `id`            int(10) unsigned                                           NOT NULL AUTO_INCREMENT,
    `characterid`   int(11)                                                    NOT NULL,
    `queststatusid` int(10) unsigned                                           NOT NULL DEFAULT '0',
    `progressid`    int(11)                                                    NOT NULL DEFAULT '0',
    `progress`      varchar(15) CHARACTER SET latin1 COLLATE latin1_german1_ci NOT NULL DEFAULT '',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
