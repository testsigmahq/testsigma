@echo on

echo "Starting Testsigma Server"
SET WORKING_FOLDER1=%~dp0
SET WORKING_FOLDER=%WORKING_FOLDER1:~0,-1%
CD /D %WORKING_FOLDER%
SET TS_DATA_DIR=%userprofile%\AppData\Roaming\Testsigma\Server
SET TS_ROOT_DIR=%WORKING_FOLDER%
SET MAIN_JAR_FILE=%WORKING_FOLDER%\testsigma-server.jar
SET LOGGING_LEVEL=INFO

if "%1" neq "" (
	SET TESTSIGMA_PLATFORM_URL=%1
)

IF EXIST %TS_DATA_DIR% (
    echo "Testsigma Data Directory already exists"
)

IF NOT EXIST %TS_DATA_DIR% (
    mkdir %TS_DATA_DIR%
)

IF EXIST %TS_ROOT_DIR%\authentication.properties (
    IF NOT EXIST %TS_DATA_DIR%\authentication.properties (
        echo %TS_ROOT_DIR%\authentication.properties
        echo %TS_DATA_DIR%\authentication.properties
        echo "Identified the misplaced authentication.properties. Copying to correct location"
        copy %TS_ROOT_DIR%\authentication.properties %TS_DATA_DIR%\authentication.properties

    )
)

"%TS_ROOT_DIR%\jre\bin\java" -Djavax.net.ssl.trustStoreType=WINDOWS-ROOT -DTS_DATA_DIR="%TS_DATA_DIR%" -Dlogging.level.="%LOGGING_LEVEL%" -cp "%WORKING_FOLDER%\lib\*;%MAIN_JAR_FILE%" com.testsigma.TestsigmaWebApplication
