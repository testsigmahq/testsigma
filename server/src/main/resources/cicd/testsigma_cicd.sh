#!/bin/bash

##########################################################################################################################################################
#
#TESTSIGMA_API_KEY-->API key generated under Testsigma App-->Configuration-->API Keys
#TESTSIGMA_TEST_PLAN_ID--> Testsigma Testplan ID, U can get this ID from Testsigma_app-->Test Plans--><TEST_PLAN_NAME>-->CI/CD Integration
#MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT-->Maximum time the script will wait for TEST Plan execution to complete. The sctript will exit if the Maximum time
#is exceeded, however the Test Plan will continue to run. You can check test results by logging to Testsigma.
#REPORT_FILE_PATH-->File path to save report Ex: <DIR_PATH>/report.xml, ./report.xml

##########################################################################################################################################################
#********START USER_INPUTS ********

#TESTSIGMA_API_KEY=<API_KEY>
#TESTSIGMA_TEST_PLAN_ID=<TEST_PLAN_ID>
MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT=180
REPORT_FILE_PATH=./junit-report.xml

#********END USER_INPUTS***********
TESTSIGMA_TEST_PLAN_REST_URL=https://app.testsigma.com/api/v1/test_plan_results #<RUN_URL>  #http://app.testsigma.com/rest/test_plan_results
TESTSIGMA_JUNIT_REPORT_URL=https://app.testsigma.com/api/v1/reports/junit


##Read arguments
for i in "$@"
do
case $i in
    -a=*|--apikey=*)
    TESTSIGMA_API_KEY="${i#*=}"
    shift
    ;;
    -i=*|--testplanid=*)
    TESTSIGMA_TEST_PLAN_ID="${i#*=}"
    shift
    ;;
    -t=*|--maxtimeinmins=*)
    MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT="${i#*=}"
    shift
    ;;
   -r=*|--reportfilepath=*)
    REPORT_FILE_PATH="${i#*=}"
    shift
    ;;
    -d=*|--runtimedata=*)
    RUNTIME_DATA_INPUT="${i#*=}"
    shift
    ;;
    -b=*|--buildno=*)
    BUILD_NO="${i#*=}"
    shift
    ;;
   -h|--help)
    echo "Arguments \n[-a | --apikey]=<TESTSIGMA_API_KEY>"
     echo "[-i | --testplanid]=<TESTSIGMA_TEST_PLAN_ID>"
      echo "[-t | --maxtimeinmins=<MAX_WAIT_TIME_IN_MINS>"
       echo "[-r | reportfilepath] =<JUNIT_REPORT_FILE_PATH>"
       echo "[-d | runtimedata] =<OPTIONAL COMMA SEPARATED KEY VALUE PAIRS>"
       echo "[-b | buildno] =<BUILD_NO_IF_ANY>"

       printf "Ex:\n bash testsigma_cicd.sh --apikey=YSWfniLEWYK7aLrS-FhYUD1kO0MQu9renQ0p-oyCXMlQ --testplanid=230 --maxtimeinmins=180 --reportfilepath=./junit-report.xml \n\n"
       printf "Ex: With Runtimedata parameters\n bash testsigma_cicd.sh --apikey=YSWfniLEWYK7aLrS-FhYUD1kO0MQu9renQ0p-oyCXMlQ --testplanid=230 --maxtimeinmins=180
       --reportfilepath=./junit-report.xml --runtimedata=\"buildurl=http://test1.url.com,data1=testdata\" --buildno=773\n\n"

    shift
    exit 1
    ;;
esac
done

          # unknown option


echo "APIKey=$TESTSIGMA_API_KEY"
echo "reportfilepath=$REPORT_FILE_PATH"
get_status(){

    RUN_RESPONSE=$(curl -H "Authorization:Bearer $TESTSIGMA_API_KEY" --silent --write-out "HTTPSTATUS:%{http_code}" -X GET $TESTSIGMA_TEST_PLAN_REST_URL/$RUN_ID)

    #echo "Test execution status: $RUN_RESPONSE "
    # extract the body
    RUN_BODY=$(echo $RUN_RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    # extract the status
    RUN_STATUS=$(echo $RUN_RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
    echo "Response_Status: $RUN_STATUS"
    # print the body
    echo "Run Status..."
  #  echo "$RUN_BODY"
    EXECUTION_STATUS=$(echo $RUN_BODY | getJsonValue status)

    echo "Execution Status: $EXECUTION_STATUS"


}
function checkTestPlanRunStatus(){
  IS_TEST_RUN_COMPLETED=0
  for((i=0; i<= NO_OF_POLLS;i++))
  do
    get_status
    if [ $EXECUTION_STATUS == 'STATUS_IN_PROGRESS' ]
     then
      echo "Sleep/Wait for $SLEEP_TIME seconds before next poll....."
      sleep $SLEEP_TIME

    else
      IS_TEST_RUN_COMPLETED=1
      echo "Automated Tests Execution completed...\n Job execution time:$(((i+1)*SLEEP_TIME/60)) minutes"
      break
    fi
  done


}
function saveFinalResponseToAFile(){
  if [ $IS_TEST_RUN_COMPLETED -eq 0 ]
     then
      LOG_CONTENT="Wait time exceeded specified maximum time(MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT). Please log-in to Testsigma to check Test Plan run status.
      You can visit the URL specified in \"app_url\" JSON/Junit report For landing in Test Plan run page directly.
      Ex: \"app_url\":\"https://app.testsigma.com/ui/td/runs/<RUN_ID>\""
      echo "$LOG_CONTENT"
      exit 1
    fi

  echo "sending request for Junit report"
  curl -H "Authorization:Bearer $TESTSIGMA_API_KEY" \
       -H "Accept: application/xml" \
       -H "content-type:application/json" \
       -X GET $TESTSIGMA_JUNIT_REPORT_URL/$RUN_ID --output $REPORT_FILE_PATH

  echo "$LOG_CONTENT \n $RUN_BODY"
  echo "Reports file::$REPORT_FILE_PATH"


}
function getJsonValue() {
json_key=$1
awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/\042'$json_key'\042/){print $(i+1)}}}' | tr -d '"'
}
function populateRuntimeData() {
  IFS=',' read -r -a VARIABLES <<< "$RUNTIME_DATA_INPUT"
RUN_TIME_DATA='"runtimeData":{'
DATA_VALUES=
for element in "${VARIABLES[@]}"
do
    DATA_VALUES=$DATA_VALUES","
    IFS='=' read -r -a VARIABLE_VALUES <<< "$element"
    DATA_VALUES="$DATA_VALUES"'"'"${VARIABLE_VALUES[0]}"'":"'"${VARIABLE_VALUES[1]}"'"'

done
DATA_VALUES="${DATA_VALUES:1}"
RUN_TIME_DATA=$RUN_TIME_DATA$DATA_VALUES"}"
  }


function populateBuildNo(){
if [ -z "$BUILD_NO" ]
then
  echo ""
else
     BUILD_DATA='"buildNo":'$BUILD_NO
fi
}
function populateJsonPayload(){
  JSON_DATA='{"executionId":'$TESTSIGMA_TEST_PLAN_ID
  populateRuntimeData
  populateBuildNo
  if [ -z "$BUILD_DATA" ]
then
    JSON_DATA=$JSON_DATA,$RUN_TIME_DATA"}"

else
     JSON_DATA=$JSON_DATA,$RUN_TIME_DATA,$BUILD_DATA"}"

fi
#JSON_DATA="'"$JSON_DATA"'"
}

echo "************Testsigma:Start executing automated tests ... ************"

##########GLOBAL variables####################################################


POLL_INTERVAL_FOR_RUN_STATUS=5
NO_OF_POLLS=$((MAX_WAIT_TIME_FOR_SCRIPT_TO_EXIT/POLL_INTERVAL_FOR_RUN_STATUS))
SLEEP_TIME=$((POLL_INTERVAL_FOR_RUN_STATUS * 60))
LOG_CONTENT=""
##############################################################################
populateJsonPayload
echo "InputData="$JSON_DATA

echo "NO of polls $NO_OF_POLLS"
# store the whole response with the status at the and
HTTP_RESPONSE=$(curl -H "Authorization:Bearer $TESTSIGMA_API_KEY" \
                      -H "Accept: application/json" \
                      -H "content-type:application/json" \
                      --silent --write-out "HTTPSTATUS:%{http_code}" \
                      -d $JSON_DATA -X POST $TESTSIGMA_TEST_PLAN_REST_URL )
# extract the body
echo "HTTP_RESPONSE=$HTTP_RESPONSE \n"
RUN_ID=$(echo $HTTP_RESPONSE | getJsonValue id)
# extract the status
HTTP_STATUS=$(echo $HTTP_RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

# print the body
echo "Run_ID:$RUN_ID"

# example using the status
if [ ! $HTTP_STATUS -eq 200  ]; then

  echo "Failed to executed automated tests!!"
  echo "Error [HTTP status: $HTTP_STATUS]"
  exit 1 #Exit with a failure.
else
  checkTestPlanRunStatus
  saveFinalResponseToAFile

fi

echo "Final response:" "$RUN_BODY"
echo "************Testsigma:Completed executing automated tests ... ************"
