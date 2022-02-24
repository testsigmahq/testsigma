import {Component, OnInit} from '@angular/core';
import {GetStartedFeatureType} from "../../enums/get-started-feature-type.enum";
import {GetStartedFeatureModel} from "../../models/get-started-feature.model";
import {GetStartedTopicModel} from "../../models/get-started-topic.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import { Router } from '@angular/router';
import {Workspace} from "../../models/workspace.model";
import {TestPlanType} from "../../enums/execution-type.enum";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {TestCaseService} from "../../services/test-case.service";
import {TestSuiteService} from "../../services/test-suite.service";
import {TestDataService} from "../../services/test-data.service";
import {UserPreferenceService} from "../../services/user-preference.service";
import * as moment from "moment";
import {BaseComponent} from "../../shared/components/base.component";
import {DryTestPlanService} from "../../services/dry-test-plan.service";
import {TestPlanService} from "../../services/test-plan.service";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {ExecutionTriggeredType} from "../../enums/triggered-type.enum";

@Component({
  selector: 'app-get-started-base',
  template: ``,
  styles: []
})
export class GetStartedBaseComponent extends BaseComponent implements OnInit {
  public getStarted: GetStartedFeatureModel[] = [
    {
      featureType: GetStartedFeatureType.WebFeatures,
      featureTopics: [
        {
          titleKey: "get_started.overview",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/web/overview.mp4",
          descriptionKey: "get_started.overview_description",
          articleLink: "https://testsigma.com/docs/test-cases/create-steps-recorder/web-apps/overview/",
          countKey: "testCasesCount",
          navigateToTry: "cases"
        },
        {
          titleKey: "get_started.create_automated_test",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/web/create-testcase.mp4",
          descriptionKey: "get_started.create_automated_test_description",
          articleLink: "https://testsigma.com/docs/test-cases/create-steps-recorder/web-apps/overview/",
          countKey: "testCasesCount",
          navigateToTry: "cases/create"
        },
        {
          titleKey: "get_started.run_tests",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/web/run-testcase.mp4",
          descriptionKey: "get_started.run_tests_description",
          articleLink: "https://testsigma.com/tutorials/getting-started/automate-web-applications/",
          countKey: "dryTestCaseCount",
          navigateToTry: "cases"
        },
        {
          titleKey: "get_started.data_driven",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/web/data-driven-testing.mp4",
          descriptionKey: "get_started.data_driven_description",
          articleLink: "https://testsigma.com/tutorials/test-cases/data-driven-testing/",
          countKey: "testDataCount",
          navigateToTry: "data/new"
        },
        {
          titleKey: "get_started.cross_browser",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/web/cross-browser-testing.mp4",
          descriptionKey: "get_started.cross_browser_description",
          articleLink: "https://testsigma.com/docs/test-management/test-plans/",
          countKey: "crossExecutionCount",
          navigateToTry: "plans/new"
        },
        {
          titleKey: "get_started.manage_suites",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/web/create-test-suite.mp4",
          descriptionKey: "get_started.manage_suites_description",
          articleLink: "https://testsigma.com/docs/test-management/test-suites/",
          countKey: "testSuiteCount",
          navigateToTry: "suites/new"
        },
        {
          titleKey: "get_started.schedule_plans",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/web/schedule-testplan.mp4",
          descriptionKey: "get_started.schedule_plans_description",
          articleLink: "https://testsigma.com/docs/test-management/test-plans/schedule-plans/",
          countKey: "executionCount",
          navigateToTry: "plans"
        },
        {
          titleKey: "get_started.ci/cd_integration",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/web/continuous-integration.mp4",
          descriptionKey: "get_started.ci/cd_integration_description",
          articleLink: "https://testsigma.com/docs/continuous-integration/jenkins/",
          countKey: "ciCdCount",
          navigateToTry: "plans"
        },
        {
          titleKey: "get_started.local_test_plans",
          source: "",
          descriptionKey: "get_started.local_test_plans_description",
          articleLink: "https://support.testsigma.com/support/solutions/articles/32000019920-how-to-execute-web-application-tests-in-hybrid-model-",
          countKey: "hybridExecutionCount",
          navigateToTry: "plans/new"
        }]
    },
    {
      featureType: GetStartedFeatureType.MobileWebFeatures,
      featureTopics: [
        {
          titleKey: "get_started.overview",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/mobile-web/overview.mp4",
          descriptionKey: "get_started.overview_description",
          articleLink: "https://testsigma.com/docs/test-cases/create-steps-recorder/web-apps/overview/",
          countKey: "testCasesCount",
          navigateToTry: "cases"
        },
        {
          titleKey: "get_started.create_automated_test",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/mobile-web/create-testcase.mp4",
          descriptionKey: "get_started.create_automated_test_description",
          articleLink: "https://testsigma.com/docs/test-cases/create-steps-recorder/web-apps/overview/",
          countKey: "testCasesCount",
          navigateToTry: "cases/create"
        },
        {
          titleKey: "get_started.run_tests",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/mobile-web/run-testcase.mp4",
          descriptionKey: "get_started.run_tests_description",
          articleLink: "https://testsigma.com/tutorials/getting-started/automate-web-applications/",
          countKey: "dryTestCaseCount",
          navigateToTry: "cases"
        },
        {
          titleKey: "get_started.data_driven",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/mobile-web/data-driven-testing.mp4",
          descriptionKey: "get_started.data_driven_description",
          articleLink: "https://testsigma.com/tutorials/test-cases/data-driven-testing/",
          countKey: "testDataCount",
          navigateToTry: "data/new"
        },
        {
          titleKey: "get_started.cross_device",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/mobile-web/cross-device-testing.mp4",
          descriptionKey: "get_started.cross_device_description",
          articleLink: "https://testsigma.com/docs/test-management/test-plans/",
          countKey: "crossExecutionCount",
          navigateToTry: "plans/new"
        },
        {
          titleKey: "get_started.manage_suites",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/mobile-web/create-test-suite.mp4",
          descriptionKey: "get_started.manage_suites_description",
          articleLink: "https://testsigma.com/docs/test-management/test-suites/",
          countKey: "testSuiteCount",
          navigateToTry: "suites/new"
        },
        {
          titleKey: "get_started.schedule_plans",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/mobile-web/schedule-testplan.mp4",
          descriptionKey: "get_started.schedule_plans_description",
          articleLink: "https://testsigma.com/docs/test-management/test-plans/schedule-plans/",
          countKey: "executionCount",
          navigateToTry: "plans"
        },
        {
          titleKey: "get_started.ci/cd_integration",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/mobile-web/continuous-integration.mp4",
          descriptionKey: "get_started.ci/cd_integration_description",
          articleLink: "https://testsigma.com/docs/continuous-integration/jenkins/",
          countKey: "ciCdCount",
          navigateToTry: "plans"
        },
        {
          titleKey: "get_started.local_test_plans",
          source: "",
          descriptionKey: "get_started.local_test_plans_description",
          articleLink: "https://support.testsigma.com/support/solutions/articles/32000023451-how-to-execute-android-web-application-tests-in-hybrid-model-",
          countKey: "hybridExecutionCount",
          navigateToTry: "plans/new"
        }]
    },
    {
      featureType: GetStartedFeatureType.AndroidFeatures,
      featureTopics: [
        {
          titleKey: "get_started.overview",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/android/overview.mp4",
          descriptionKey: "get_started.overview_description",
          articleLink: "https://testsigma.com/docs/test-cases/create-steps-recorder/android-apps/",
          countKey: "testCasesCount",
          navigateToTry: "cases"
        },
        {
          titleKey: "get_started.create_automated_test",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/android/create-testcase.mp4",
          descriptionKey: "get_started.create_automated_test_description",
          articleLink: "https://testsigma.com/docs/test-cases/create-steps-recorder/android-apps/",
          countKey: "testCasesCount",
          navigateToTry: "cases/create"
        },
        {
          titleKey: "get_started.run_tests",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/android/run-testcase.mp4",
          descriptionKey: "get_started.run_tests_description",
          articleLink: "https://testsigma.com/tutorials/getting-started/automate-web-applications/",
          countKey: "dryTestCaseCount",
          navigateToTry: "cases"
        },
        {
          titleKey: "get_started.data_driven",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/android/data-driven-testing.mp4",
          descriptionKey: "get_started.data_driven_description",
          articleLink: "https://testsigma.com/tutorials/test-cases/data-driven-testing/",
          countKey: "testDataCount",
          navigateToTry: "data/new"
        },
        {
          titleKey: "get_started.cross_device",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/android/cross-device-testing.mp4",
          descriptionKey: "get_started.cross_device_description",
          articleLink: "https://testsigma.com/docs/test-management/test-plans/",
          countKey: "crossExecutionCount",
          navigateToTry: "plans/new"
        },
        {
          titleKey: "get_started.manage_suites",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/android/create-test-suite.mp4",
          descriptionKey: "get_started.manage_suites_description",
          articleLink: "https://testsigma.com/docs/test-management/test-suites/",
          countKey: "testSuiteCount",
          navigateToTry: "suites/new"
        },
        {
          titleKey: "get_started.schedule_plans",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/android/schedule-testplan.mp4",
          descriptionKey: "get_started.schedule_plans_description",
          articleLink: "https://testsigma.com/docs/test-management/test-plans/schedule-plans/",
          countKey: "executionCount",
          navigateToTry: "plans"
        },
        {
          titleKey: "get_started.ci/cd_integration",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/android/continuous-integration.mp4",
          descriptionKey: "get_started.ci/cd_integration_description",
          articleLink: "https://testsigma.com/docs/continuous-integration/jenkins/",
          countKey: "ciCdCount",
          navigateToTry: "plans"
        },
        {
          titleKey: "get_started.local_test_plans",
          source: "",
          descriptionKey: "get_started.local_test_plans_description",
          articleLink: "https://support.testsigma.com/support/solutions/articles/32000023286-how-to-execute-native-android-application-tests-in-hybrid-model-",
          countKey: "hybridExecutionCount",
          navigateToTry: "plans/new"
        }]
    },
    {
      featureType: GetStartedFeatureType.iosFeatures,
      featureTopics: [
        {
          titleKey: "get_started.overview",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/ios/overview.mp4",
          descriptionKey: "get_started.overview_description",
          articleLink: "https://testsigma.com/docs/test-cases/create-steps-recorder/ios-apps/",
          countKey: "testCasesCount",
          navigateToTry: "cases"
        },
        {
          titleKey: "get_started.create_automated_test",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/ios/create-testcase.mp4",
          descriptionKey: "get_started.create_automated_test_description",
          articleLink: "https://testsigma.com/docs/test-cases/create-steps-recorder/ios-apps/",
          countKey: "testCasesCount",
          navigateToTry: "cases/create"
        },
        {
          titleKey: "get_started.run_tests",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/ios/run-testcase.mp4",
          descriptionKey: "get_started.run_tests_description",
          articleLink: "https://testsigma.com/tutorials/getting-started/automate-web-applications/",
          countKey: "dryTestCaseCount",
          navigateToTry: "cases"
        },
        {
          titleKey: "get_started.data_driven",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/ios/data-driven-testing.mp4",
          descriptionKey: "get_started.data_driven_description",
          articleLink: "https://testsigma.com/tutorials/test-cases/data-driven-testing/",
          countKey: "testDataCount",
          navigateToTry: "data/new"
        },
        {
          titleKey: "get_started.cross_device",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/ios/cross-device-testing.mp4",
          descriptionKey: "get_started.cross_device_description",
          articleLink: "https://testsigma.com/docs/test-management/test-plans/",
          countKey: "crossExecutionCount",
          navigateToTry: "plans/new"
        },
        {
          titleKey: "get_started.manage_suites",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/ios/create-test-suite.mp4",
          descriptionKey: "get_started.manage_suites_description",
          articleLink: "https://testsigma.com/docs/test-management/test-suites/",
          countKey: "testSuiteCount",
          navigateToTry: "suites/new"
        },
        {
          titleKey: "get_started.schedule_plans",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/ios/schedule-testplan.mp4",
          descriptionKey: "get_started.schedule_plans_description",
          articleLink: "https://testsigma.com/docs/test-management/test-plans/schedule-plans/",
          countKey: "executionCount",
          navigateToTry: "plans"
        },
        {
          titleKey: "get_started.ci/cd_integration",
          source: "https://s3.amazonaws.com/assets.testsigma.com/videos/get-started/ios/continuous-integration.mp4",
          descriptionKey: "get_started.ci/cd_integration_description",
          articleLink: "https://testsigma.com/docs/continuous-integration/jenkins/",
          countKey: "ciCdCount",
          navigateToTry: "plans"
        },
        {
          titleKey: "get_started.local_test_plans",
          source: "",
          descriptionKey: "get_started.local_test_plans_description",
          articleLink: "https://support.testsigma.com/support/solutions/articles/32000023616-how-to-execute-native-ios-application-tests-in-hybrid-model-",
          countKey: "hybridExecutionCount",
          navigateToTry: "plans/new"
        }]
    }];
  public getStartedFeatureTypes = GetStartedFeatureType;
  public selectedFeatureType: GetStartedFeatureType = GetStartedFeatureType.WebFeatures;
  public topics: GetStartedTopicModel[];
  public selectedTopic: GetStartedTopicModel;
  public getStartedCounts: any = [];
  public versionId: String;
  public userCreatedAt: Date;

  constructor(
    public authGuard: AuthenticationGuard,
    public router: Router,
    public testCaseService: TestCaseService,
    public dryTestPlanService: DryTestPlanService,
    public testSuiteService: TestSuiteService,
    public testPlanService: TestPlanService,
    public testDataService: TestDataService,
    public userPreferenceService: UserPreferenceService,
    public testPlanResult: TestPlanResultService
  ) {
    super(authGuard);
  }

  ngOnInit(): void {
    this.fetchUserCreatedTime();
  }

  ngOnChanges() {
    this.fetchUserCreatedTime();
  }


  switchFeature(appTypeKey) {
    this.selectedFeatureType = appTypeKey;
    this.getStarted.find(feature => {
      if (feature.featureType == appTypeKey) {
        this.topics = feature.featureTopics;
        this.selectedTopic = this.topics[0];
        return;
      }
    })
  }

  changeTopic(topic: any) {
    if(this.selectedTopic == topic) return;
    this.selectedTopic = topic;
  }

  openArticle() {
    window.open(this.selectedTopic.articleLink, '_blank');
  }

  tryIt(navigateTo, versionId?:number| String) {
    versionId = versionId ? versionId : this.versionId;
    if(navigateTo.includes('/'))
      this.router.navigate(['/td', versionId, navigateTo.split('/')[0], navigateTo.split('/')[1]]);
    else
      this.router.navigate(['/td', versionId, navigateTo]);
  }

  isCompleted(topic: GetStartedTopicModel) {
    return this.getStartedCounts[topic.countKey] > 0;
  }

  openFreshChat() {
    //@ts-ignore
    window.fcWidget.open();
  }

  setTopicValue(app: Workspace) {
    let appTypeKey = GetStartedFeatureType.WebFeatures;
    if (app.isMobileWeb)
      appTypeKey = GetStartedFeatureType.MobileWebFeatures;
    else if (app.isAndroidNative)
      appTypeKey = GetStartedFeatureType.AndroidFeatures;
    else if (app.isIosNative)
      appTypeKey = GetStartedFeatureType.iosFeatures;

    this.switchFeature(appTypeKey)
  }

  private fetchGetStartedStatuses(user) {
    this.fetchTestCasesCount(user);
    this.fetchDryTestCaseCount(user);
    this.fetchTestDataCount(user);
    this.fetchCrossExecutionCount(user);
    this.fetchTestSuiteCount(user);
    this.fetchExecutionCount(user);
    this.fetchLocalExecutionCount(user);
    this.fetchAllApiKeys();
  }

  private fetchTestCasesCount(user) {
    this.testCaseService.findAll("createdDate>" + moment(this.userCreatedAt).format("YYYY-MM-DD")).subscribe(
      res => this.getStartedCounts["testCasesCount"] = res.content.length
    );
  }

  private fetchDryTestCaseCount(user) {
    if (this.userCreatedAt)
      this.dryTestPlanService.findAll("createdDate>" + this.userCreatedAt ).subscribe(
      res => this.getStartedCounts["dryTestCaseCount"] = res.content.length
    );
  }

  private fetchTestDataCount(user) {
    if (this.userCreatedAt)
      this.testDataService.findAll("createdDate>" + this.userCreatedAt).subscribe(
      res => this.getStartedCounts["testDataCount"] = res.content.length
    );
  }

  private fetchCrossExecutionCount(user) {
    if (this.userCreatedAt)
      this.testPlanService.findAll("testPlanType:" + TestPlanType.CROSS_BROWSER.toString() + ",createdDate>" + this.userCreatedAt).subscribe(
      res => this.getStartedCounts["crossExecutionCount"] = res.content.length
    );
  }

  private fetchTestSuiteCount(user) {
    if (this.userCreatedAt)
      this.testSuiteService.findAll("createdDate>" + this.userCreatedAt).subscribe(
      res => this.getStartedCounts["testSuiteCount"] = res.content.length
    );
  }

  private fetchExecutionCount(user) {
    if (this.userCreatedAt)
      this.testPlanService.findAll("createdDate>" + this.userCreatedAt).subscribe(
      res => this.getStartedCounts["executionCount"] = res.content.length
    );
  }

  private fetchLocalExecutionCount(user) {
    if (this.userCreatedAt)
      this.testPlanService.findAll("testPlanLabType:" + TestPlanLabType.Hybrid.toString() + ",createdDate>" + this.userCreatedAt).subscribe(
      res => this.getStartedCounts["hybridExecutionCount"] = res.content.length
    );
  }

  private fetchAllApiKeys() {
    if (this.userCreatedAt)
      this.testPlanResult.findAll("triggeredType:"+ExecutionTriggeredType.API).subscribe(
      res => {
        this.getStartedCounts["ciCdCount"] = res.content.length;
      }
    )
  }

  private fetchUserCreatedTime(){
    this.userPreferenceService.show().subscribe(res => {
      if(res.createdAt)
        this.userCreatedAt = res.createdAt;
      this.fetchGetStartedStatuses(this.authGuard?.session?.user);
    });
  }
}
