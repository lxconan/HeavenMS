CREATE TABLE IF NOT EXISTS `nxcode`
(
    `id`         int(11)             NOT NULL AUTO_INCREMENT,
    `code`       varchar(17)         NOT NULL UNIQUE,
    `retriever`  varchar(13)                  DEFAULT NULL,
    `expiration` bigint(20) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  AUTO_INCREMENT = 1
