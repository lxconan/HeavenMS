CREATE TABLE IF NOT EXISTS `playernpcs`
(
    `id`           int(11)          NOT NULL AUTO_INCREMENT,
    `name`         varchar(13)      NOT NULL,
    `hair`         int(11)          NOT NULL,
    `face`         int(11)          NOT NULL,
    `skin`         int(11)          NOT NULL,
    `gender`       int(11)          NOT NULL DEFAULT '0',
    `x`            int(11)          NOT NULL,
    `cy`           int(11)          NOT NULL DEFAULT '0',
    `world`        int(11)          NOT NULL DEFAULT '0',
    `map`          int(11)          NOT NULL DEFAULT '0',
    `dir`          int(11)          NOT NULL DEFAULT '0',
    `scriptid`     int(10) unsigned NOT NULL DEFAULT '0',
    `fh`           int(11)          NOT NULL DEFAULT '0',
    `rx0`          int(11)          NOT NULL DEFAULT '0',
    `rx1`          int(11)          NOT NULL DEFAULT '0',
    `worldrank`    int(11)          NOT NULL DEFAULT '0',
    `overallrank`  int(11)          NOT NULL DEFAULT '0',
    `worldjobrank` int(11)          NOT NULL DEFAULT '0',
    `job`          int(11)          NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 2147000000
