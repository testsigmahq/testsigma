@echo on

echo "Starting Testsigma Server"
SET WORKING_FOLDER1=%~dp0
SET WORKING_FOLDER=%WORKING_FOLDER1:~0,-1%
SET SERVER_WORKING_DIR=%WORKING_FOLDER%\TestsigmaServer
SET AGENT_WORKING_DIR=%WORKING_FOLDER%\TestsigmaAgent
CD /D %WORKING_FOLDER%\nginx
START cmd /c "%WORKING_FOLDER%\nginx\nginx.exe -s quit"
