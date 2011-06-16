::::: Before running any sql-tools application:::::::::::::::::::::::::::::::::::::::::

1 - Unzip the sql-tools.jar into a folder on your local drive.
2 - Make sure your JAVA_HOME environment variable is set to a Java 1.6 JDK or JRE directory.
3 - Modify the sql-tools.property file as needed:
	- Change the "jdbc" properties to reference an Oracle or MS SQL Alineo database.
	- Change the "com.med.sql-tools.targetSchemas" and "com.med.sql-tools.excludeTargetSchemas" 
	  properties to configure the schemas to be examined.

::::: Generating database reports ::::::::::::::::::::::::::::::::::::::::::::::::::::::

The dbreport tool reports over the tables, columns, keys, indices and triggers for each schema 
included in the report. Reports against two databases can be compared to determine whether the 
databases are structurally equivalent.

From a MS Windows command prompt window, run dbreport.bat specifying the location of the 
sql-tools.property file and optionally an output file. For example:
	dbreport sql-tools.properties my-report.txt

From a Unix/Linux terminal window, run dbreport.sh specifying the location of the 
sql-tools.property file and optionally an output file. For example:
	dbreport.sh sql-tools.properties my-report.txt

