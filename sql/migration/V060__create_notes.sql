CREATE TABLE IF NOT EXISTS `notes`
(
    `id`        int(11)             NOT NULL AUTO_INCREMENT,
    `to`        varchar(13)         NOT NULL DEFAULT '',
    `from`      varchar(13)         NOT NULL DEFAULT '',
    `message`   text                NOT NULL,
    `timestamp` bigint(20) unsigned NOT NULL,
    `fame`      int(11)             NOT NULL DEFAULT '0',
    `deleted`   int(2)              NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
