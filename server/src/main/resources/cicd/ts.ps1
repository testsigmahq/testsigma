

##########################################################################################################################################################
#
#TESTSIGMA_API_KEY-->API key generated under Testsigma App-->Configuration-->API Keys
#TESTSIGMA_TEST_PLAN_ID--> Testsigma Testplan ID, U can get this ID from Testsigma_app-->Test Plans--><TEST_PLAN_NAME>-->CI/CD Integration
#MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT-->Maximum time the script will wait for TEST Plan execution to complete. The sctript will exit if the Maximum time
#is exceeded, however the Test Plan will continue to run. You can check test results by logging to Testsigma.
#REPORT_FILE_PATH-->File path to save report Ex: <DIR_PATH>/report.xml, ./report.xml
#$RUN_TIME_PARAMS=@{key1="$env:value1";key2="value2"}
##########################################################################################################################################################

<# START USER INPUTS..#>


$TESTSIGMA_API_KEY="gdhdfhgfhgdfdgfdgkd.dehdeuhiuehduehipdeh"
$TESTSIGMA_TEST_PLAN_ID="190"
$REPORT_FILE_PATH="./junit-report.xml"
$MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT=180
$RUN_TIME_PARAMS=@{}
<# END USER INPUTS #>

$TESTSIGMA_TEST_PLAN_REST_URL="https://app.testsigma.com/api/v1/test_plan_results"
$TESTSIGMA_JUNIT_REPORT_URL="https://app.testsigma.com/api/v1/reports/junit"

$POLL_INTERVAL_FOR_RUN_STATUS=5
$NO_OF_POLLS=($MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT/$POLL_INTERVAL_FOR_RUN_STATUS)
$SLEEP_TIME=($POLL_INTERVAL_FOR_RUN_STATUS * 60)
$global:LOG_CONTENT=""
$global:APP_URL=""
$global:EXECUTION_STATUS=-1
$RUN_ID=""
$global:IS_TEST_RUN_COMPLETED=-1
$PSDefaultParameterValues['Invoke-RestMethod:SkipHeaderValidation'] = $true
$PSDefaultParameterValues['Invoke-WebRequest:SkipHeaderValidation'] = $true
$base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("{0}" -f $TESTSIGMA_API_KEY)))
function get_status{
    $global:RUN_RESPONSE=Invoke-RestMethod  $status_URL -Method GET -Headers @{Authorization=("Bearer {0}" -f $TESTSIGMA_API_KEY);'Accept'='application/json'} -ContentType "application/json"

    $global:EXECUTION_STATUS=$RUN_RESPONSE.status
    $global:APP_URL=$RUN_RESPONSE.app_url
    Write-Host "Execution Status: $EXECUTION_STATUS"

}
function checkTestPlanRunStatus{
  $global:IS_TEST_RUN_COMPLETED=0
  for($i=0; $i -le $NO_OF_POLLS;$i++){
    get_status
    Write-Host "Execution Status before going for wait: $EXECUTION_STATUS ,Status_message:"($RUN_RESPONSE.message)
    if ($EXECUTION_STATUS -eq "STATUS_IN_PROGRESS"){
      Write-Host "Sleep/Wait for $SLEEP_TIME seconds before next poll....."
      sleep $SLEEP_TIME
    }else{
      $global:IS_TEST_RUN_COMPLETED=1
      Write-Host "Automated Tests Execution completed...`nTotal script execution time:$(($i)*$SLEEP_TIME/60) minutes"
      break
    }
  }
}

function saveFinalResponseToAFile{
  if ($IS_TEST_RUN_COMPLETED -eq 0){
      $global:LOG_CONTENT="Wait time exceeded specified maximum time(MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT). Please visit below URL for Test Plan Run status.$APP_URL"
     Write-Host "LogContent:$LOG_CONTENT nResponse content:"($RUN_RESPONSE | ConvertTo-Json -Compress)

	 }
	 else{
	 Write-Host "Fetching reports:$TESTSIGMA_JUNIT_REPORT_URL/$RUN_ID"
	 $REPORT_DATA=Invoke-RestMethod  $TESTSIGMA_JUNIT_REPORT_URL/$RUN_ID -Method GET -Headers @{Authorization=("Bearer {0}" -f $TESTSIGMA_API_KEY);'Accept'='application/xml'} -ContentType "application/json"
	 Write-Host "report data: $REPORT_DATA"

	# Add-Content -Path $REPORT_FILE_PATH -Value ($REPORT_DATA)
	$REPORT_DATA.OuterXml | Out-File $REPORT_FILE_PATH
		}
    Write-Host "Reports File::$REPORT_FILE_PATH"

}

Write-Host "No of polls: $NO_OF_POLLS"
Write-Host "Polling Interval:$SLEEP_TIME"
Write-Host "Junit report file path: $REPORT_FILE_PATH"

$REQUEST_BODY_TABLE=@{executionId="$TESTSIGMA_TEST_PLAN_ID"}
$REQUEST_BODY_TABLE.Add("runtimeData",$RUN_TIME_PARAMS)

$REQUEST_BODY = $REQUEST_BODY_TABLE | ConvertTo-Json -Compress
Write-Host "Json payload" $REQUEST_BODY
try{
$TRIGGER_RESPONSE=Invoke-RestMethod -Method POST -Headers @{Authorization=("Bearer {0}" -f $TESTSIGMA_API_KEY);'Accept'='application/json'} -ContentType 'application/json' -Body $REQUEST_BODY -uri $TESTSIGMA_TEST_PLAN_REST_URL
}catch{

 Write-Host "Code:" $_.Exception.Response.StatusCode.value__
 Write-Host "Description:" $_.Exception.Response.StatusDescription
 Write-Host "Error encountered in executing a test plan. Please check if the test plan is already in running state."
 exit 1
}

$RUN_ID=$TRIGGER_RESPONSE.id
Write-Host "Execution triggered RunID: $RUN_ID"
$status_URL = "$TESTSIGMA_TEST_PLAN_REST_URL/$RUN_ID"
Write-Host  $status_URL

checkTestPlanRunStatus
saveFinalResponseToAFile



