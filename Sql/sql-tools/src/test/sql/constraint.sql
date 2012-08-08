ALTER TABLE CODES.AUDIT_ACTN ADD CONSTRAINT  PK_AUDIT_ACTN PRIMARY KEY (AUDIT_ACTN_CODE);
ALTER TABLE CODES.AUDIT_TYPE ADD CONSTRAINT  AK1_AUDIT_TYPE UNIQUE (AUDIT_TYPE_DESC);
ALTER TABLE RELMGMT.PERSONROLE ADD CONSTRAINT FK_PARTYROLE_PERSONROLE FOREIGN KEY (PARTYROLEID) REFERENCES RELMGMT.PARTYROLE (PARTYROLEID);
ALTER TABLE AUDITS.CLIENT_AL ADD (CONSTRAINT  FK_CLIENT_AL_CLIENT FOREIGN KEY (CLIENT_CODE) REFERENCES CODES.CLIENT(CLIENT_CODE));
ALTER TABLE CODES.SCRTY_CRIT_EXPRSN ADD CONSTRAINT  AK1_SCRTY_CRIT_EXPRSN UNIQUE (SCRTY_CRIT_EXPRSN_VAL);

alter table  TASKLIST.TASK drop foreign key OWNS;
alter table  USR.USER_ELEC_ADDR drop foreign key FK_USER_ELEC_ADDR_USER;
ALTER TABLE REVIEW.REVW_OUTCOME DROP CONSTRAINT FK_REVW_OUTCOME_TX_RQST;
ALTER TABLE USR.USER_ACCOUNT DROP PRIMARY KEY;
ALTER TABLE ACTIVITY.ACTIVITY DROP FOREIGN KEY FK_ACTIVITY_USER;

ALTER TABLE CODES.TRANS_SRC ADD (LAST_MODIFIED_TIMESTAMP TIMESTAMP);		-- add column without using reserved word
ALTER TABLE REVIEW.AUTOMATEDREVIEW DROP COLUMN GROUP;				-- drop column
ALTER TABLE SCRTY.USER_ROLE_GRANT ALTER UIDENT NOT NULL;			-- alter column nullability

CREATE TABLE PARTYROLE.PROVIDER ( PARTYROLEID INTEGER NOT NULL, NATIONALPROVID  VARCHAR2(30), MCOPROVID  VARCHAR2(30)  NOT NULL, TYPE  VARCHAR2(20)  NOT NULL, PRIMSPECIALTY  VARCHAR2(20) NULL, RATING  VARCHAR2(20)  NULL, ISACTIVE NUMBER(1)   DEFAULT  1 NOT NULL, LAST_MODIFIED_TIMESTAMP TIMESTAMP , CONSTRAINT PK_PROVIDER PRIMARY KEY (PARTYROLEID) );