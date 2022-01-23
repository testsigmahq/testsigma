@echo on

echo "Starting Testsigma Server"
SET WORKING_FOLDER1=%~dp0
SET WORKING_FOLDER=%WORKING_FOLDER1:~0,-1%
SET SERVER_WORKING_DIR=%WORKING_FOLDER%\TestsigmaServer
SET AGENT_WORKING_DIR=%WORKING_FOLDER%\TestsigmaAgent
CD /D %WORKING_FOLDER%\nginx

IF NOT EXIST %WORKING_FOLDER%\nginx\ssl mkdir %WORKING_FOLDER%\nginx\ssl
IF NOT EXIST %WORKING_FOLDER%\nginx\logs mkdir %WORKING_FOLDER%\nginx\logs

TYPE nul >> %WORKING_FOLDER%\nginx\logs\error.log
COPY /b %WORKING_FOLDER%\nginx\logs\error.log +,,

powershell -Command "(New-Object Net.WebClient).DownloadFile('https://s3.amazonaws.com/public-assets.testsigma.com/os_certificates/local_testsigmaos_com.key', '%WORKING_FOLDER%\nginx\ssl\local_testsigmaos_com.key')"
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://s3.amazonaws.com/public-assets.testsigma.com/os_certificates/local_testsigmaos_com.pem', '%WORKING_FOLDER%\nginx\ssl\local_testsigmaos_com.pem')"

"%WORKING_FOLDER%\nginx\nginx.exe" -c "%WORKING_FOLDER%\nginx\nginx.conf" -g "daemon off;"

