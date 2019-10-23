CREATE TABLE IF NOT EXISTS `cooldowns`
(
    `id`        int(11)             NOT NULL AUTO_INCREMENT,
    `charid`    int(11)             NOT NULL,
    `SkillID`   int(11)             NOT NULL,
    `length`    bigint(20) unsigned NOT NULL,
    `StartTime` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
