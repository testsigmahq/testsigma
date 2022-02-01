/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";

@Injectable()
export class UrlConstantsService {
  public environmentUrl = `${environment.apiUrl}`;
  public apiBase = this.environmentUrl;
  public sessionUrl = this.apiBase + '/sessions';
  public agentsUrl = this.apiBase + '/settings/agents';
  public uploadsUrl = this.apiBase + "/uploads";
  public elementURL = this.apiBase + "/elements";
  public elementScreenNameURL = this.apiBase + "/elements_screen_name";
  public workspaceVersionsURL = this.apiBase + "/workspace_versions";
  public attachmentsUrl = this.apiBase + '/attachments';
  public environmentsUrl = this.apiBase + '/environments';
  public mobileInspectionsUrl = this.apiBase + '/mobile_inspections';
  public testPlansUrl = this.apiBase + '/test_plans';
  public testDevicesUrl = this.apiBase + '/test_devices';
  public testPlanResultsUrl = this.apiBase + '/test_plan_results';
  public testDeviceResultsUrl = this.apiBase + '/test_device_results';
  public testSuiteResultsUrl = this.apiBase + '/test_suite_results';
  public testCaseResultsUrl = this.apiBase + '/test_case_results';
  public testCaseDataDrivenResultsUrl = this.apiBase + '/test_case_data_driven_results';
  public testStepResultsUrl = this.apiBase + '/test_step_results';
  public naturalTextActionsUrl = this.apiBase + '/natural_text_actions';
  public testCasesUrl = this.apiBase + '/test_cases';
  public dryTestPlansUrl = this.apiBase + '/dry_test_plans';
  public userPreferencesUrl = this.apiBase + '/user_preferences';
  public projectsUrl = this.apiBase + '/new/projects';
  public workspacesUrl = this.apiBase + '/workspaces';
  public testCaseTagsUrl = this.apiBase + '/testcase_tags';
  public testSuiteTagsUrl = this.apiBase + '/testsuite_tags';
  public elementTagsUrl = this.apiBase + "/element_tags"
  public screenshotComparisonsUrl = this.apiBase + '/screenshot_comparisons';
  public testStepScreenshotsUrl = this.apiBase + '/test_step_screenshots';
  public testCasePrioritiesUrl = this.apiBase + '/test_case_priorities';
  public testCaseTypesUrl = this.apiBase + '/test_case_types';
  public integrationsUrl = this.apiBase + "/settings/integrations";
  public externalMappingsUrl = this.apiBase + "/external_mappings";
  public dataProfileUrl = this.apiBase + '/test_data';
  public elementFilterUrl = this.apiBase + '/element_filters';
  public testCaseFilterUrl = this.apiBase + '/test_case_filters';
  public stepGroupFilterUrl = this.apiBase + '/step_group_filters';
  public scheduledPlanUrl = this.apiBase + '/schedule_test_plans';
  public testStepsUlr = this.apiBase + '/test_steps';
  public suggestionEngineUrl = this.apiBase + "/suggestion_results";
  public testDataFunctionsUrl = this.apiBase + "/default_data_generators";
  public testSuitesUrl= this.apiBase+"/test_suites";
  public adhocRunConfigurationsUrl = this.apiBase+"/adhoc_run_configurations";
  public loginUrl = '/login' ;
  public logoutUrl = '/logout' ;
  public iosSettingUrl = this.apiBase+ '/settings/provisioning_profiles';
  public kibbutzUrl= this.apiBase+'/kibbutz';
  public addonUrl= this.apiBase+'/addons';
  public cloudMobileInspectionsUrl = this.apiBase+ '/driver_sessions';
  public testsigmaOSConfigURL = this.apiBase+ '/testsigma_os_config';
  public storageConfigURL = this.apiBase+ '/storage_config';
  public authConfigURL = this.apiBase+ '/auth_config';
  public serverURL = this.apiBase+ '/servers';
  public osServerDetailsURL = this.apiBase + '/os_server_details';

  public backupUrl = this.apiBase+ "/settings/backups" ;
  public backupXmlUrl = this.apiBase+ "/settings/backups/xml" ;

  public onboardingURL = this.apiBase + '/onboarding';

  public agentDownloadTagUrl = this.agentsUrl + "/download_tag";
  constructor() {
  }
}
