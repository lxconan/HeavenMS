CREATE TABLE IF NOT EXISTS `ipbans`
(
    `ipbanid` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `ip`      varchar(40)      NOT NULL DEFAULT '',
    `aid`     varchar(40)               DEFAULT NULL,
    PRIMARY KEY (`ipbanid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
