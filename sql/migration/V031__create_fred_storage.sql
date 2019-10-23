CREATE TABLE IF NOT EXISTS `fredstorage`
(
    `id`        int(10) unsigned NOT NULL AUTO_INCREMENT,
    `cid`       int(10) unsigned NOT NULL,
    `daynotes`  int(4) unsigned  NOT NULL,
    `timestamp` timestamp        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `cid_2` (`cid`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
