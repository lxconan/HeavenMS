CREATE TABLE IF NOT EXISTS `bbs_threads`
(
    `threadid`      int(10) unsigned     NOT NULL AUTO_INCREMENT,
    `postercid`     int(10) unsigned     NOT NULL,
    `name`          varchar(26)          NOT NULL DEFAULT '',
    `timestamp`     bigint(20) unsigned  NOT NULL,
    `icon`          smallint(5) unsigned NOT NULL,
    `replycount`    smallint(5) unsigned NOT NULL DEFAULT '0',
    `startpost`     text                 NOT NULL,
    `guildid`       int(10) unsigned     NOT NULL,
    `localthreadid` int(10) unsigned     NOT NULL,
    PRIMARY KEY (`threadid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
