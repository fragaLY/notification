CREATE SCHEMA IF NOT EXISTS notification;
SET search_path TO notification;

CREATE TABLE notification
(
  id                    uuid                  NOT NULL,
  sender                varchar(25)           NOT NULL,
  receiver              varchar(25)           NOT NULL,

  CONSTRAINT "pk_event_notification.notification" PRIMARY KEY (id)
);