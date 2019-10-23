CREATE TABLE IF NOT EXISTS `reports`
(
    `id`          int(10) unsigned NOT NULL AUTO_INCREMENT,
    `reporttime`  timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `reporterid`  int(11)          NOT NULL,
    `victimid`    int(11)          NOT NULL,
    `reason`      tinyint(4)       NOT NULL,
    `chatlog`     text             NOT NULL,
    `description` text             NOT NULL, # correct field name, thanks resinate
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  AUTO_INCREMENT = 1
