# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table faculty_availability (
  id                            bigint auto_increment not null,
  faculty_id                    bigint,
  start_time                    datetime(6),
  end_time                      datetime(6),
  is_available                  tinyint(1) default 1 not null,
  created_time                  datetime(6),
  updated_time                  datetime(6),
  constraint pk_faculty_availability primary key (id)
);

create table ra_interview (
  id                            bigint auto_increment not null,
  rajob_application_id          bigint,
  faculty_id                   bigint,
  applicant_id                 bigint,
  rajob_id                     bigint,
  interview_date                datetime(6),
  interview_time               datetime(6),
  meeting_link                 varchar(500),
  location                     varchar(255),
  status                       varchar(50),
  student_response             varchar(50),
  notes                        text,
  created_time                 datetime(6),
  updated_time                 datetime(6),
  constraint pk_ra_interview primary key (id)
);

create table interview_notification (
  id                            bigint auto_increment not null,
  interview_id                  bigint,
  notification_type             varchar(50),
  sent_to                       bigint,
  sent_time                     datetime(6),
  email_subject                 varchar(255),
  email_body                    text,
  constraint pk_interview_notification primary key (id)
);

alter table faculty_availability add constraint fk_faculty_availability_faculty_id foreign key (faculty_id) references user (id) on delete restrict on update restrict;
create index ix_faculty_availability_faculty_id on faculty_availability (faculty_id);
create index ix_faculty_availability_start_time on faculty_availability (start_time);

alter table ra_interview add constraint fk_ra_interview_rajob_application_id foreign key (rajob_application_id) references rajob_application (id) on delete restrict on update restrict;
create index ix_ra_interview_rajob_application_id on ra_interview (rajob_application_id);

alter table ra_interview add constraint fk_ra_interview_faculty_id foreign key (faculty_id) references user (id) on delete restrict on update restrict;
create index ix_ra_interview_faculty_id on ra_interview (faculty_id);

alter table ra_interview add constraint fk_ra_interview_applicant_id foreign key (applicant_id) references user (id) on delete restrict on update restrict;
create index ix_ra_interview_applicant_id on ra_interview (applicant_id);

alter table ra_interview add constraint fk_ra_interview_rajob_id foreign key (rajob_id) references rajob (id) on delete restrict on update restrict;
create index ix_ra_interview_rajob_id on ra_interview (rajob_id);

create index ix_ra_interview_interview_time on ra_interview (interview_time);

alter table interview_notification add constraint fk_interview_notification_interview_id foreign key (interview_id) references ra_interview (id) on delete restrict on update restrict;
create index ix_interview_notification_interview_id on interview_notification (interview_id);

alter table interview_notification add constraint fk_interview_notification_sent_to foreign key (sent_to) references user (id) on delete restrict on update restrict;
create index ix_interview_notification_sent_to on interview_notification (sent_to);


# --- !Downs

alter table faculty_availability drop foreign key fk_faculty_availability_faculty_id;
drop index ix_faculty_availability_faculty_id on faculty_availability;
drop index ix_faculty_availability_start_time on faculty_availability;

alter table ra_interview drop foreign key fk_ra_interview_rajob_application_id;
drop index ix_ra_interview_rajob_application_id on ra_interview;

alter table ra_interview drop foreign key fk_ra_interview_faculty_id;
drop index ix_ra_interview_faculty_id on ra_interview;

alter table ra_interview drop foreign key fk_ra_interview_applicant_id;
drop index ix_ra_interview_applicant_id on ra_interview;

alter table ra_interview drop foreign key fk_ra_interview_rajob_id;
drop index ix_ra_interview_rajob_id on ra_interview;

drop index ix_ra_interview_interview_time on ra_interview;

alter table interview_notification drop foreign key fk_interview_notification_interview_id;
drop index ix_interview_notification_interview_id on interview_notification;

alter table interview_notification drop foreign key fk_interview_notification_sent_to;
drop index ix_interview_notification_sent_to on interview_notification;

drop table if exists faculty_availability;

drop table if exists ra_interview;

drop table if exists interview_notification;

