CREATE TABLE IF NOT EXISTS `newyear`
(
    `id`              int(10) unsigned    NOT NULL AUTO_INCREMENT,
    `senderid`        int(10)             NOT NULL DEFAULT '-1',
    `sendername`      varchar(13)                  DEFAULT '',
    `receiverid`      int(10)             NOT NULL DEFAULT '-1',
    `receivername`    varchar(13)                  DEFAULT '',
    `message`         varchar(120)                 DEFAULT '',
    `senderdiscard`   tinyint(1)          NOT NULL DEFAULT '0',
    `receiverdiscard` tinyint(1)          NOT NULL DEFAULT '0',
    `received`        tinyint(1)          NOT NULL DEFAULT '0',
    `timesent`        bigint(20) unsigned NOT NULL,
    `timereceived`    bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
