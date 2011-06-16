CREATE TRIGGER UpdateTimestampAfterInsert AFTER INSERT ON REVIEW.REVW_OUTCOME REFERENCING NEW ROW AS newRow UPDATE REVIEW.REVW_OUTCOME SET LAST_MODIFIED_TIMESTAMP = current_timestamp WHERE ID = newRow.ID;
CREATE TRIGGER UpdateTimestampAfterInsert AFTER INSERT ON PARTYROLE.ALTERNATEID REFERENCING NEW ROW AS newRow UPDATE PARTYROLE.ALTERNATEID SET LAST_MODIFIED_TIMESTAMP = current_timestamp WHERE ID = newRow.ID;
CREATE TRIGGER UpdateTimestampAfterUpdate AFTER UPDATE OF PARTYROLEID, ALTID ON PARTYROLE.ALTERNATEID REFERENCING NEW ROW AS newRow UPDATE PARTYROLE.ALTERNATEID SET LAST_MODIFIED_TIMESTAMP = current_timestamp WHERE ID = newRow.ID;
CREATE TRIGGER UpdateTimestampAfterInsert AFTER INSERT ON RELMGMT.ADDRESS REFERENCING NEW ROW AS newRow UPDATE RELMGMT.ADDRESS SET LAST_MODIFIED_TIMESTAMP = current_timestamp WHERE ID = newRow.ID;
CREATE TRIGGER UpdateTimestampAfterUpdate AFTER UPDATE OF CONTACT_MECHANISM_PID,GEO_REGION_PID,LINE_ONE,LINE_TWO,LINE_THREE,CITY,STATE_OR_PROVINCE,POSTAL_CODE,COUNTRY ON RELMGMT.ADDRESS REFERENCING NEW ROW AS newRow UPDATE RELMGMT.ADDRESS SET LAST_MODIFIED_TIMESTAMP = current_timestamp WHERE ID = newRow.ID;

--------------------------------------------------------------------------------
-- D:\aa\jeisenst_Alineo_Model\Jaguar\Sql\sql-tools\..\alineo-install-sql\..\..\Alineo\database-version\migrate_1_0_2A.dml
--------------------------------------------------------------------------------
insert into CONFIG.DB_VERSION (DB_VERSION_MAJOR, DB_VERSION_MINOR, DB_VERSION_FIX, DB_VERSION_PATCH) values (1, 0, 2, 1);
--------------------------------------------------------------------------------
-- D:\aa\jeisenst_Alineo_Model\Jaguar\Sql\sql-tools\..\alineo-install-sql\..\..\TreatmentAuthorization\treatment-auth\src\main\database\DDL\Review\migrate_1_0_2A-1.ddl
--------------------------------------------------------------------------------
DROP TRIGGER UpdateTimestampAfterInsert FROM REVIEW.REVW_OUTCOME;
CREATE TRIGGER UpdateTimestampAfterInsert1 AFTER INSERT ON REVIEW.REVW_OUTCOME REFERENCING NEW ROW AS newRow UPDATE REVIEW.REVW_OUTCOME SET LAST_MODIFIED_TIMESTAMP = current_timestamp WHERE ID = newRow.ID;
CREATE TRIGGER UpdateTimestampAfterInsert2 AFTER INSERT ON REVIEW.REVW_OUTCOME REFERENCING NEW ROW AS newRow UPDATE REVIEW.CUSTOM_FIELD SET LAST_MODIFIED_TIMESTAMP = current_timestamp WHERE PARENT_OBJECT_ID = newRow.RQST_ITEM_DETAIL_PID;
CREATE TRIGGER UpdateTimestampAfterInsert3 AFTER INSERT ON REVIEW.REVW_OUTCOME REFERENCING NEW ROW AS newRow UPDATE REVIEW.CUSTOM_FIELD_SEL SET LAST_MODIFIED_TIMESTAMP = current_timestamp WHERE SELECT_FIELD_ID = ( SELECT PID FROM REVIEW.CUSTOM_FIELD WHERE PARENT_OBJECT_ID = newRow.RQST_ITEM_DETAIL_PID );
