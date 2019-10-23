CREATE TABLE IF NOT EXISTS `bbs_replies`
(
    `replyid`   int(10) unsigned    NOT NULL AUTO_INCREMENT,
    `threadid`  int(10) unsigned    NOT NULL,
    `postercid` int(10) unsigned    NOT NULL,
    `timestamp` bigint(20) unsigned NOT NULL,
    `content`   varchar(26)         NOT NULL DEFAULT '',
    PRIMARY KEY (`replyid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
