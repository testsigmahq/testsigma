@echo on

echo "Starting Testsigma Agent"
SET INSTALLATION_FOLDER1=%~dp0
SET INSTALLATION_FOLDER=%INSTALLATION_FOLDER1:~0,-1%
CD /D %INSTALLATION_FOLDER%
SET TS_DATA_DIR=%userprofile%\AppData\Roaming\Testsigma\Agent
SET TS_ROOT_DIR=%INSTALLATION_FOLDER%
SET TS_AGENT_JAR_PATH=%INSTALLATION_FOLDER%
SET MAIN_JAR_FILE=%INSTALLATION_FOLDER%\agent-launcher.jar
SET LOGGING_LEVEL=INFO

IF EXIST %TS_DATA_DIR% (
    echo "Testsigma Data Directory already exists"
)

IF NOT EXIST %TS_DATA_DIR% (
    mkdir %TS_DATA_DIR%
)

"%TS_ROOT_DIR%\jre\bin\java" -Djavax.net.ssl.trustStoreType=WINDOWS-ROOT -DTS_DATA_DIR="%TS_DATA_DIR%" -DTS_ROOT_DIR="%TS_ROOT_DIR%" -DTS_AGENT_JAR="%TS_AGENT_JAR_PATH%" -Dlogging.level.="%LOGGING_LEVEL%" -cp "%MAIN_JAR_FILE%" com.testsigma.agent.launcher.Application start
