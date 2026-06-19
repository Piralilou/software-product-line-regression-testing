@echo off
setlocal

set EVOSUITE_JAR=evosuite-1.1.0.jar
set CLASSPATH=bin/
set PACKAGE=kwicdeltaj.prod1.kwic

set CLASSES=FileBasedStorageManager IndexManager IndexStorage Main SaveFile StopWordManager StringStorage WordShift

for %%C in (%CLASSES%) do (
    echo Running EvoSuite for %PACKAGE%.%%C
    java -jar %EVOSUITE_JAR% -class %PACKAGE%.%%C -projectCP %CLASSPATH%
)

endlocal
pause