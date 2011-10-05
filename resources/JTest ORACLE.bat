@echo on
cd D:\ANACONDA\Alineo\Alineo
D:
cmd /k "mvn install -Dorg.apache.maven.user-settings=F:/job/global/local/settings.xml  -Ddatabase=LOCAL:MSSQL"
 
