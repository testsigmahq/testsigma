@echo on

echo "Stopping Testsigma Server"
SET WORKING_FOLDER1=%~dp0
SET WORKING_FOLDER=%WORKING_FOLDER1:~0,-1%
CD /D %WORKING_FOLDER%
SET /P PID= < "%WORKING_FOLDER%\bin\app.pid"
"%WORKING_FOLDER%\windows-kill" -SIGINT %PID%
