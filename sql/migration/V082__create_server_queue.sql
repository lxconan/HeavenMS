CREATE TABLE IF NOT EXISTS `server_queue`
(
    `id`          int(11)      NOT NULL AUTO_INCREMENT,
    `accountid`   int(11)      NOT NULL DEFAULT '0',
    `characterid` int(11)      NOT NULL DEFAULT '0',
    `type`        tinyint(2)   NOT NULL DEFAULT '0',
    `value`       int(10)      NOT NULL DEFAULT '0',
    `message`     varchar(128) NOT NULL,
    `createTime`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  AUTO_INCREMENT = 1
