
IF   EXISTS (SELECT * FROM sys.tables WHERE object_id 
= OBJECT_ID('CWQI.REVIEW'))
DROP table CWQI.REVIEW;
IF   EXISTS (SELECT * FROM sys.schemas WHERE name 
='BATCH')
DROP schema BATCH;



