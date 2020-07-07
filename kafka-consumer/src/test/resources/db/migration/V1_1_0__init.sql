CREATE SCHEMA IF NOT EXISTS NOTIFICATION;
SET search_path TO NOTIFICATION;

CREATE TABLE NOTIFICATION
(
  id                    uuid                NOT NULL,
  sender                varchar(25)         NOT NULL,
  receiver              varchar(25)         NOT NULL,

  CONSTRAINT "pk_notification.notification" PRIMARY KEY (id)
);