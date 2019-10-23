CREATE TABLE IF NOT EXISTS `accounts`
(
    `id`             int(11)      NOT NULL AUTO_INCREMENT,
    `name`           varchar(13)  NOT NULL DEFAULT '',
    `password`       varchar(128) NOT NULL DEFAULT '',
    `pin`            varchar(10)  NOT NULL DEFAULT '',
    `pic`            varchar(26)  NOT NULL DEFAULT '',
    `loggedin`       tinyint(4)   NOT NULL DEFAULT '0',
    `lastlogin`      timestamp    NULL     DEFAULT NULL,
    `createdat`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `birthday`       date         NOT NULL DEFAULT '0000-00-00',
    `banned`         tinyint(1)   NOT NULL DEFAULT '0',
    `banreason`      text,
    `macs`           tinytext,
    `nxCredit`       int(11)               DEFAULT NULL,
    `maplePoint`     int(11)               DEFAULT NULL,
    `nxPrepaid`      int(11)               DEFAULT NULL,
    `characterslots` tinyint(2)   NOT NULL DEFAULT '3',
    `gender`         tinyint(2)   NOT NULL DEFAULT '10',
    `tempban`        timestamp    NOT NULL DEFAULT '0000-00-00 00:00:00',
    `greason`        tinyint(4)   NOT NULL DEFAULT '0',
    `tos`            tinyint(1)   NOT NULL DEFAULT '0',
    `sitelogged`     text,
    `webadmin`       int(1)                DEFAULT '0',
    `nick`           varchar(20)           DEFAULT NULL,
    `mute`           int(1)                DEFAULT '0',
    `email`          varchar(45)           DEFAULT NULL,
    `ip`             text,
    `rewardpoints`   int(11)      NOT NULL DEFAULT '0',
    `votepoints`     int(11)      NOT NULL DEFAULT '0',
    `hwid`           varchar(12)  NOT NULL DEFAULT '',
    `language`       int(1)       NOT NULL DEFAULT '2',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`),
    KEY `ranking1` (`id`, `banned`),
    INDEX (id, name),
    INDEX (id, nxCredit, maplePoint, nxPrepaid)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
