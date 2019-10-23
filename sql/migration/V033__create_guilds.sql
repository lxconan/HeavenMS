CREATE TABLE IF NOT EXISTS `guilds`
(
    `guildid`     int(10) unsigned     NOT NULL AUTO_INCREMENT,
    `leader`      int(10) unsigned     NOT NULL DEFAULT '0',
    `GP`          int(10) unsigned     NOT NULL DEFAULT '0',
    `logo`        int(10) unsigned              DEFAULT NULL,
    `logoColor`   smallint(5) unsigned NOT NULL DEFAULT '0',
    `name`        varchar(45)          NOT NULL,
    `rank1title`  varchar(45)          NOT NULL DEFAULT 'Master',
    `rank2title`  varchar(45)          NOT NULL DEFAULT 'Jr. Master',
    `rank3title`  varchar(45)          NOT NULL DEFAULT 'Member',
    `rank4title`  varchar(45)          NOT NULL DEFAULT 'Member',
    `rank5title`  varchar(45)          NOT NULL DEFAULT 'Member',
    `capacity`    int(10) unsigned     NOT NULL DEFAULT '10',
    `logoBG`      int(10) unsigned              DEFAULT NULL,
    `logoBGColor` smallint(5) unsigned NOT NULL DEFAULT '0',
    `notice`      varchar(101)                  DEFAULT NULL,
    `signature`   int(11)              NOT NULL DEFAULT '0',
    `allianceId`  int(11) unsigned     NOT NULL DEFAULT '0',
    PRIMARY KEY (`guildid`),
    INDEX (guildid, name)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
