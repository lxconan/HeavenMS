CREATE TABLE IF NOT EXISTS `alliance`
(
    `id`       int(10) unsigned NOT NULL AUTO_INCREMENT,
    `name`     varchar(13)      NOT NULL,
    `capacity` int(10) unsigned NOT NULL DEFAULT '2',
    `notice`   varchar(20)      NOT NULL DEFAULT '',

    `rank1`    varchar(11)      NOT NULL DEFAULT 'Master',
    `rank2`    varchar(11)      NOT NULL DEFAULT 'Jr. Master',
    `rank3`    varchar(11)      NOT NULL DEFAULT 'Member',
    `rank4`    varchar(11)      NOT NULL DEFAULT 'Member',
    `rank5`    varchar(11)      NOT NULL DEFAULT 'Member',
    PRIMARY KEY (`id`),
    INDEX (name)
) ENGINE = MyISAM
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
