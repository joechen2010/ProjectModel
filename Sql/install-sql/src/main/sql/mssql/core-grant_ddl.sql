--USE master; removing this so logins are added to current db being created 10/18/2010
IF  EXISTS (SELECT * FROM sys.server_principals WHERE name = N'ALINEO')
DROP LOGIN ALINEO;
CREATE LOGIN ALINEO WITH PASSWORD = 'P@ssw0rd';
IF  EXISTS (SELECT * FROM sys.database_principals WHERE name = N'ALINEO')
DROP USER ALINEO;
CREATE USER ALINEO FOR LOGIN ALINEO WITH DEFAULT_SCHEMA = alineo;
EXEC sp_addrolemember 'db_datawriter', 'ALINEO';
EXEC sp_addrolemember 'db_datareader', 'ALINEO';

--USE master;
IF  EXISTS (SELECT * FROM sys.server_principals WHERE name = N'ALINEOADMIN')
DROP LOGIN ALINEOADMIN;
CREATE LOGIN ALINEOADMIN WITH PASSWORD = 'P@ssw0rd';
IF  EXISTS (SELECT * FROM sys.database_principals WHERE name = N'ALINEOADMIN')
DROP USER ALINEOADMIN;
CREATE USER ALINEOADMIN FOR LOGIN ALINEOADMIN WITH DEFAULT_SCHEMA = alineo;
EXEC sp_addrolemember 'db_owner', 'ALINEOADMIN';




