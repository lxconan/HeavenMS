CREATE TABLE IF NOT EXISTS `eventstats`
(
    `characterid` int(11) unsigned NOT NULL,
    `name`        varchar(11)      NOT NULL DEFAULT '0' COMMENT '0',
    `info`        int(11)          NOT NULL,
    PRIMARY KEY (`characterid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
