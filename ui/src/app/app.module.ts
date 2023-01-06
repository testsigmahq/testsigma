import {BrowserModule, Title} from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {AppRoutingModule} from 'app/app-routing.module';
import {AppComponent} from 'app/app.component';
import {SharedModule} from 'app/shared/shared.module';
import {LeftNavComponent} from "app/components/webcomponents/left-nav.component";
import {ElementsListComponent} from './components/elements/list.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RunListComponent} from './components/webcomponents/run-list.component';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {environment} from "../environments/environment";
import {SimpleNotificationsModule} from 'angular2-notifications';
import {TestDevelopmentComponent} from './components/test-development.component';
import {TestCasesListComponent} from "./components/cases/list.component";
import {ResultsListComponent} from './components/results/list.component';
import {LabEnvironmentsInfoComponent} from './components/webcomponents/lab-environments-info.component';
import {RunDetailsComponent} from "./components/results/run-details.component";
import {TestMachineResultDetailsComponent} from './components/results/test-machine-result-details.component';
import {SuiteResultDetailsComponent} from './components/results/suite-result-details.component';
import {TestCaseResultDetailsComponent} from './components/results/test-case-result-details.component';
import {TestCaseResultsComponent} from './components/webcomponents/test-case-results.component';
import {TestSuiteResultsComponent} from './components/webcomponents/test-suite-results.component';
import {TestMachineResultsComponent} from './components/webcomponents/test-machine-results.component';
import {RunDetailsQuickInfoComponent} from './components/webcomponents/run-details-quick-info.component';
import {ResultDonutChartComponent} from './components/webcomponents/result-donut-chart.component';
import {TestCaseResultSummaryComponent} from './components/webcomponents/test-case-result-summary.component';
import {TestCaseDataDrivenResultsComponent} from './components/webcomponents/test-case-data-driven-results.component';
import {TestStepResultsComponent} from './components/webcomponents/test-step-results.component';
import {TestStepResultDetailsComponent} from './components/results/test-step-result-details.component';
import {TestCaseResultListComponent} from './components/webcomponents/test-case-result-list.component';
import {TestStepResultFilterComponent} from './components/webcomponents/test-step-result-filter.component';
import {RunListFilterComponent} from './components/webcomponents/run-list-filter.component';
import {RunListInfoComponent} from './components/webcomponents/run-list-info.component';
import {TestCaseResultFilterComponent} from './components/webcomponents/test-case-result-filter.component';
import {LabEnvironmentScreenShortInfoComponent} from './components/webcomponents/lab-environment-screen-short-info.component';
import {TestMachineDetailsQuickInfoComponent} from './components/webcomponents/test-machine-details-quick-info.component';
import {TestSuiteDetailsQuickInfoComponent} from './components/webcomponents/test-suite-details-quick-info.component';
import {TestSuiteResultFilterComponent} from './components/webcomponents/test-suite-result-filter.component';
import {TestMachineResultFilterComponent} from './components/webcomponents/test-machine-result-filter.component';
import {ResultStatusLabelInfoComponent} from './components/webcomponents/result-status-label-info.component';
import {ResultAttachmentComponent} from './components/webcomponents/result-attachment.component';
import {TestStepResultListItemComponent} from './components/webcomponents/test-step-result-list-item.component';
import {ActionStepResultDetailsComponent} from './components/webcomponents/action-step-result-details.component';
import {RestStepResultDetailsComponent} from './components/webcomponents/rest-step-result-details.component';
import {MatMenuModule} from "@angular/material/menu";
import {ElementFormComponent} from './components/webcomponents/element-form.component';
import {DetailsHeaderComponent as ElementDetailsHeaderComponent} from './components/elements/details-header.component';
import {ElementAttributeDetailsComponent} from './components/webcomponents/element-attribute-details.component';
import {NgxJsonViewerModule} from "ngx-json-viewer";
import {RunListBarChartComponent} from './components/webcomponents/run-list-bar-chart.component';
import {ScreenShortOverlayComponent} from './components/webcomponents/screen-short-overlay.component';
import {RunDetailsBarChartComponent} from './components/webcomponents/run-details-bar-chart.component';
import {TestMachineTestCaseResultsComponent} from './components/webcomponents/test-machine-test-case-results.component';
import {TestMachineTestSuiteResultsComponent} from './components/webcomponents/test-machine-test-suite-results.component';
import {TestSuiteTestCaseResultsComponent} from './components/webcomponents/test-suite-test-case-results.component';
import {VisualComparisonListComponent} from './components/webcomponents/visual-comparison-list.component';
import {TestMachineResultDetailsPaginationComponent} from './components/webcomponents/test-machine-result-details-pagination.component';
import {TestSuiteResultDetailsPaginationComponent} from './components/webcomponents/test-suite-result-details-pagination.component';
import {TestCaseResultDetailsPaginationComponent} from './components/webcomponents/test-case-result-details-pagination.component';
import {MatButtonModule} from "@angular/material/button";
import {ElementAutoHealingListComponent} from './components/webcomponents/element-auto-healing-list.component';
import {SafeUrlPipe} from './pipe/safe-url.pipe';
import {TdRedirectComponent} from './components/td-redirect.component';
import {NgxPageScrollModule} from "ngx-page-scroll";
import {VisualTestingComponent} from './components/webcomponents/visual-testing.component';
import {ReportBugComponent} from './components/webcomponents/report-bug.component';
import {JiraIssueFormComponent} from './components/webcomponents/jira-issue-form.component';
import {JiraIssueFormFieldComponent} from './components/webcomponents/jira-issue-form-field.component';
import {AutoRefreshComponent} from "./shared/components/webcomponents/auto-refresh.component";
import {JiraIssueDetailsComponent} from './components/webcomponents/jira-issue-details.component';
import {FreshReleaseIssueFormComponent} from './components/webcomponents/fresh-release-issue-form.component';
import {FreshReleaseIssueDetailsComponent} from './components/webcomponents/fresh-release-issue-details.component';
import {AzureIssueFormComponent} from './components/webcomponents/azure-issue-form.component';
import {AzureIssueDetailsComponent} from './components/webcomponents/azure-issue-details.component';
import {YoutrackIssueFormComponent} from './components/webcomponents/youtrack-issue-form.component';
import {YoutrackIssueDetailsComponent} from './components/webcomponents/youtrack-issue-details.component';
import {MatRadioModule} from "@angular/material/radio";
import {WhyTestCaseFailedHelpComponent} from './components/webcomponents/why-test-case-failed-help.component';
import {ElementsRedirectComponent} from "./components/elements/elements-redirect.component";
import {TestCaseStatusFormComponent} from './components/webcomponents/test-case-status-form.component';
import {TestCasesFilterComponent} from './components/webcomponents/test-cases-filter.component';
import {TestCasesFiltersListComponent} from './components/webcomponents/test-cases-filters-list.component';
import {TestCasesRedirectComponent} from "./components/cases/test-cases-redirect.component";
import {TestCaseFilterFormComponent} from './components/webcomponents/test-case-filter-form.component';
import {TestCaseDetailsComponent} from "./components/cases/test-case-details.component";
import {TestCaseDetailPaginationComponent} from './components/webcomponents/test-case-detail-pagination.component';
import {TestStepHelpComponent} from './components/webcomponents/test-step-help.component';
import {TestStepHelpExamplesComponent} from './components/webcomponents/test-step-help-examples.component';
import {StepGroupTestCasesComponent} from './components/cases/step-group-test-cases.component';
import {DryRunsComponent} from './components/cases/dry-runs.component';
import {StepsListComponent} from './components/cases/steps-list.component';
import {TestCaseSummaryComponent} from './components/webcomponents/test-case-summary.component';
import {StepSummaryComponent} from './components/webcomponents/step-summary.component';
import {ElementDependentLocatorAttributesComponent} from './components/webcomponents/element-dependent-locator-attributes.component';
import {ElementAttributesComponent} from './components/webcomponents/element-attributes.component';
import {ElementFiltersComponent} from './components/webcomponents/element-filters.component'
import {ElementFiltersListComponent} from "./components/webcomponents/element-filters-list.component";
import {ElementFilterFormComponent} from "./components/webcomponents/element-filter-form.component";
import {CreateTestGroupFromStepFormComponent} from "./components/webcomponents/create-test-group-from-step-form.component";
import {StepBulkUpdateFormComponent} from './components/webcomponents/step-bulk-update-form.component';
import {TestStepHelpDocumentComponent} from './components/webcomponents/test-step-help-document.component';
import {TestStepHelpSamplesComponent} from './components/webcomponents/test-step-help-samples.component';
import {ElementAddTagComponent} from "./components/webcomponents/element-add-tag.component";
import {ListComponent as TestDataProfilesListComponent} from './components/data/list.component';
import {DryRunFormComponent} from './components/webcomponents/dry-run-form.component';
import 'codemirror/mode/jsx/jsx';
import 'codemirror/mode/clike/clike';
import 'codemirror/addon/fold/foldcode';
import 'codemirror/addon/fold/foldgutter';
import 'codemirror/addon/fold/brace-fold';
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {TestDataImportComponent} from "./components/webcomponents/test-data-import.component";
import {CreateComponent as TestDataCreateComponent} from './components/data/create.component';
import {DetailsComponent as TestDataDetailsComponent} from './components/data/details.component';
import {EditComponent as TestDataEditComponent} from './components/data/edit.component';
import {DataComponent as TestDataDataComponent} from './components/data/data.component';
import {TestCasesComponent as TestDataTestCasesComponent} from './components/data/test-cases.component'
import {OverlayModule} from "@angular/cdk/overlay";
import {TestDataGridComponent} from './components/webcomponents/test-data-grid.component';
import {TestStepMoreActionFormComponent} from './components/webcomponents/test-step-more-action-form.component';
import {TestCaseCloneFormComponent} from './components/webcomponents/test-case-clone-form.component';
import {TestCaseFormComponent} from './components/cases/test-case-form.component';
import {TestCaseCreateHelpComponent} from './components/webcomponents/test-case-create-help.component';
import {ListTagsComponent} from './components/webcomponents/list-tags.component';
import {EnvironmentsAutoCompleteComponent} from './components/webcomponents/environments-auto-complete.component';
import {DryRunRestFormComponent} from './components/webcomponents/dry-run-rest-form.component';
import {DryRunMobileNativeFormComponent} from "./components/webcomponents/dry-run-mobile-native-form.component";
import {TestPlanTimeOutSettingsComponent} from './components/webcomponents/test-plan-time-out-settings.component';
import {DryRunMobileWebFormComponent} from './components/webcomponents/dry-run-mobile-web-form.component';
import {MatIconModule} from "@angular/material/icon";
import {DryRunWebFormComponent} from './components/webcomponents/dry-run-web-form.component';
import {SelectTestLabComponent} from './components/webcomponents/select-test-lab.component';
import {ListComponent as EnvironmentsListComponent} from './components/environments/list.component';
import {DetailsComponent as EnvironmentsDetailsComponent} from './components/environments/details.component';
import {FormComponent as EnvironmentsFormComponent} from './components/environments/form.component';
import {EnvironmentsTableComponent} from './components/webcomponents/environments-table.component';
import {TestPlanListComponent} from "./components/plans/list.component";
import {ScheduleListComponent} from './components/plans/schedule-list.component';
import {DetailsComponent as TestPlanDetailsComponent} from './components/plans/details.component';
import {DetailsHeaderComponent as TestPlanDetailsHeaderComponent} from './components/plans/details-header.component';
import {RunNowButtonComponent} from './components/webcomponents/run-now-button.component';
import {ScheduleFormButtonComponent} from './components/webcomponents/schedule-form-button.component';
import {ReportsButtonComponent} from './components/webcomponents/reports-button.component';
import {SuitesComponent as TestPlanSuitesListComponent} from './components/plans/suites.component';
import {DevicesComponent as TestPlanDevicesListComponent} from './components/plans/devices.component';
import {PlugsComponent as TestPlanCICDComponent} from './components/plans/plugs.component';
import {SchedulesComponent as TestPlanSchedulesListComponent} from './components/plans/schedules.component';
import {FormComponent as TestPlanFormComponent} from './components/plans/form.component';
import {ListComponent as UploadsListComponent} from './components/uploads/list.component';
import {TestPlanFormComponent as TestPlanNameFormComponent} from './components/webcomponents/test-plan-form.component';
import {MatStepperModule} from "@angular/material/stepper";
import {TestPlanSuiteSelectionComponent} from './components/webcomponents/test-plan-suite-selection.component';
import {TestPlanSettingsFormComponent} from './components/webcomponents/test-plan-settings-form.component';
import {TestPlanTestMachineFormComponent} from './components/webcomponents/test-plan-test-machine-form.component';
import {TestPlanDeviceFormComponent} from './components/webcomponents/test-plan-device-form.component';
import {TestPlanSuitesFormComponent} from './components/webcomponents/test-plan-suites-form.component';
import {ReactiveFormsModule} from "@angular/forms";
import {TestPlanPlatformOsVersionFormComponent} from './components/webcomponents/test-plan-platform-os-version-form.component';
import {TestPlanExecutionEnvironmentInfoComponent} from './components/webcomponents/test-plan-execution-environment-info.component';
import {TestPlanAppUploadsFormComponent} from './components/webcomponents/test-plan-app-uploads-form.component';
import {TestPlanRecoveryActionsComponent} from './components/webcomponents/test-plan-recovery-actions.component';
import {TestPlanAddSuiteFormComponent} from './components/webcomponents/test-plan-add-suite-form.component';
import {ListComponent as ActionTemplatesListComponent} from './components/actions/list.component';
import {ListComponent as TestSuitesListComponent} from './components/suites/list.component';
import {DetailsComponent as TestDetailsListComponent} from './components/suites/details.component';
import {TestCasesComponent as TestCasesListInTestSuiteComponent} from './components/suites/test-cases.component';
import {TestPlansComponent as TestPlansListInTestSuiteComponent} from './components/suites/test-plans.component';
import {ActionElementSuggestionComponent} from "./components/webcomponents/action-element-suggestion.component";
import {ActionTestDataParameterSuggestionComponent} from "./components/webcomponents/action-test-data-parameter-suggestion.component";
import {ActionTestDataFunctionSuggestionComponent} from "./components/webcomponents/action-test-data-function-suggestion.component";
import {ActionTestDataEnvironmentSuggestionComponent} from "./components/webcomponents/action-test-data-environment-suggestion.component";
import {FormComponent as TestSuiteFormComponent} from './components/suites/form.component';
import {TestSuiteAddCaseFormComponent} from './components/webcomponents/test-suite-add-case-form.component';
import {TestPlanTestMachineSelectFormComponent} from './components/webcomponents/test-plan-test-machine-select-form.component';
import {CalendarModule, DateAdapter} from 'angular-calendar';
import {adapterFactory} from 'angular-calendar/date-adapters/moment';
import * as moment from 'moment';
import {SchedulesCalendarComponent} from './components/webcomponents/schedules-calendar.component';
import {SchedulesListComponent} from './components/webcomponents/schedules-list.component';
import {TdOverlayMenuButtonComponent} from './components/webcomponents/td-overlay-menu-button.component';
import {DashboardComponent} from './components/dashboard.component';
import {LatestRunsComponent} from './components/webcomponents/latest-runs.component';
import {TestCaseCoverageSummaryComponent} from './components/webcomponents/test-case-coverage-summary.component';
import {WeeklyScheduleCalendarComponent} from './components/webcomponents/weekly-schedule-calendar.component';
import {TestCaseByStatusSummaryComponent} from './components/webcomponents/test-case-by-status-summary.component';
import {TestCaseByTypeSummaryComponent} from './components/webcomponents/test-case-by-type-summary.component';
import {BySummaryDonutChartComponent} from './components/webcomponents/by-summary-donut-chart.component';
import {Ng9OdometerModule} from 'ng9-odometer';
import {DryRunSavedConfigFormComponent} from './components/webcomponents/dry-run-saved-config-form.component';
import {DryRunSavedConfigListComponent} from "./components/webcomponents/dry-run-saved-config-list.component";
import {DetailsHeaderComponent as EnvironmentDetailsHeaderComponent} from './components/environments/details-header.component';
import {TestPlansComponent as EnvironmentTestPlansComponent} from './components/environments/test-plans.component';
import {TagsChipListComponent} from './components/webcomponents/tags-chip-list.component';
import {ReRunButtonComponent} from './components/webcomponents/re-run-button.component';
import {ReRunDetailsComponent} from './components/webcomponents/re-run-details.component';
import {ReRunIconComponent} from './components/webcomponents/re-run-icon.component';
import {ResultStatusLabelComponent} from './components/webcomponents/result-status-label.component';
import {ReRunTestCaseResultsComponent} from './components/webcomponents/re-run-test-case-results.component';
import {ReRunTestSuiteResultsComponent} from './components/webcomponents/re-run-test-suite-results.component';
import {ReRunTestMachineResultsComponent} from './components/webcomponents/re-run-test-machine-results.component';
import {ReRunDataDrivenResultsComponent} from './components/webcomponents/re-run-data-driven-results.component';
import {GlobalAddComponent} from "./components/webcomponents/global-add.component";
import {ReRunTestStepResultComponent} from './components/webcomponents/re-run-test-step-result.component';
import {ReRunTestStepResultItemComponent} from './components/webcomponents/re-run-test-step-result-item.component';
import {UsageDetailsComponent} from './components/webcomponents/usage-details.component';
import {LoginFormComponent} from './components/login-form.component';
import {RecaptchaModule} from 'ng-recaptcha';
import {CommonModule} from "@angular/common";
import {ElementMetadataComponent} from "./components/webcomponents/element-metadata.component";
import {MantisIssueFormComponent} from './components/webcomponents/mantis-issue-form.component';
import {MantisIssueDetailsComponent} from './components/webcomponents/mantis-issue-details.component';
import {BackLogIssueFormComponent} from './components/webcomponents/backlog-issue-form.component';
import {BackLogIssueDetailsComponent} from './components/webcomponents/backlog-issue-details.component';
import {ZepelIssueFormComponent} from './components/webcomponents/zepel-issue-form.component';
import {ZepelIssueDetailsComponent} from './components/webcomponents/zepel-issue-details.component';
import {BugZillaIssueFormComponent} from './components/webcomponents/bugzilla-issue-form.component';
import {BugZillaIssueDetailsComponent} from './components/webcomponents/bugzilla-issue-details.component';
import {TestCaseCoverageCountComponent} from './components/webcomponents/test-case-coverage-count.component';
import {TrelloIssueFormComponent} from "./components/webcomponents/trello-issue-form.component";
import {TrelloIssueDetailsComponent} from "./components/webcomponents/trello-issue-details.component";
import {LinearIssueFormComponent} from './components/webcomponents/linear-issue-form.component';
import {LinearIssueDetailsComponent} from './components/webcomponents/linear-issue-details.component';
import {HelpActionsComponent} from './components/webcomponents/help-actions.component';
import {TestCaseDataDrivenResultListComponent} from './src/app/components/webcomponents/test-case-data-driven-result-list.component';
import {ElementBulkUpdateComponent} from './components/webcomponents/element-bulk-update.component';
import {AddonAppComponent} from './components/addon-app.component';
import {RouteLoadingComponent} from './components/webcomponents/route-loading.component';
import {MatCarouselModule} from '@ngmodule/material-carousel';
import {TestDataFilterComponent} from "./components/data/webcomponents/test-data-filter.component";
import {TestDevIconComponent} from "./components/webcomponents/test-dev-icon.component";
import {SchedulePlanFormComponent} from './components/webcomponents/schedule-plan-form.component';
import {ConsentComponent} from './components/webcomponents/consent.component';
import {BackupFormComponent} from "./components/webcomponents/backup-form.component";
import {AddonsComponent} from "./components/addons/addons.component";
import {SettingsModule} from "./settings/settings.module";
import {TestsigmaLoveComponent} from "./components/webcomponents/testsigma-love.component";
import {SupportComponent} from "./components/support.component";
import { OnboardingFormComponent } from './components/onboarding-form.component';
import { TelemetryNotificationComponent } from './components/webcomponents/telemetry-notification.component';
import {QuickStartComponent} from "./components/webcomponents/quick-start.component";
import {GetStartedBaseComponent} from "./components/webcomponents/get-started-base.component";
import {AddonDetailsComponent} from "./shared/components/webcomponents/addon-details.component";
import {SubmitElementReviewComponent} from "./components/webcomponents/submit-element-review.component";
import {ChromeRecordButtonComponent} from "./components/webcomponents/chrome-record-button.component";
import {FirstChromeInstallComponent} from "./components/webcomponents/first-chrome-install.component";
import {CodemirrorModule} from "@ctrl/ngx-codemirror";
import {DragDropModule} from "@angular/cdk/drag-drop";
import {FreshChatWidgetComponent} from "./components/webcomponents/fresh-chat-widget.component";
import {ClickUpIssueFormComponent} from "./components/webcomponents/click-up-issue-form.component";
import {ClickUpIssueDetailsComponent} from "./components/webcomponents/click-up-issue-details.component";
import {DuplicateLocatorWarningComponent} from "./components/webcomponents/duplicate-locator-warning.component";
import {TestSuitePrerequisiteChangeComponent} from "./shared/components/webcomponents/test-suite-prerequisite-change.component";
import {TestCasePrerequisiteChangeComponent} from "./components/webcomponents/test-case-prerequisite-change.component";
import {InlineSortComponent} from "./components/webcomponents/inline-sort.component";
import {ImportFormComponent} from "./components/webcomponents/import-form.component";
import {ImportGuideLinesWarningComponent} from "./components/webcomponents/import-guide-lines-warning.component";
import {SelectedElementsContainerComponent} from "./agents/components/webcomponents/selected-elements-container.component";
import {ElementsContainerComponent} from "./agents/components/webcomponents/elements-container.component";
import {MirroringContainerComponent} from "./agents/components/webcomponents/mirroring-container.component";
import {SaveWorkWarningComponent} from "./agents/components/webcomponents/save-work-warning.component";
import {AppSourceContainerComponent} from "./agents/components/webcomponents/app-source-container.component";
import {MobileStepRecorderComponent} from "./agents/components/webcomponents/mobile-step-recorder.component";
import {MobileInspectionComponent} from "./agents/components/webcomponents/mobile-inspection.component";
import { TestPlanLabTypeDropdownComponent } from './components/webcomponents/test-plan-lab-type-dropdown.component';
import {VersionSelectionComponent} from "./components/webcomponents/version-selection.component.component";
import { TestPlanSuiteMachineSelectionComponent } from './components/plans/test-plan-editor/test-plan-suite-machine-selection.component';
import { CreateMachineButtonComponent } from './components/plans/test-plan-editor/create-machine-button.component';
import { MapTestMachineFormComponent } from './components/plans/test-plan-editor/map-test-machine-form.component';
import { TestPlanEnvTagsComponent } from './components/plans/test-plan-editor/test-plan-env-tags.component';
import { TestPlanMachineInfoComponent } from './components/plans/test-plan-editor/test-plan-machine-info.component';
import { TestPlanMachineSelectionFormComponent } from './components/plans/test-plan-editor/test-plan-machine-selection-form.component';
import { TestPlanMachinesListItemComponent } from './components/plans/test-plan-editor/test-plan-machines-list-item.component';
import { TestPlanSuiteFilterComponent } from './components/plans/test-plan-editor/test-plan-suite-filter.component';
import { TestSuiteListItemComponent } from './components/plans/test-plan-editor/test-suite-list-item.component';
import { FormHeaderComponent } from './components/plans/form-header.component';
import {ActionTestDataRuntimeVariableSuggestionComponent} from './components/webcomponents/action-test-data-runtime-variable-suggestion.component';
import {ReRunPopupComponent} from "./agents/components/webcomponents/re-run-popup.component";
import {ReRunFormComponent} from "./agents/components/webcomponents/re-run-form.component";
import {TestCaseMultiSelectComponent} from "./components/webcomponents/test-case-multi-select.component";
import { FilterFormComponent } from './components/suites/filter-form.component';
import { FilterFormComponent as ResultFilterFormComponent} from './components/results/filter-form.component';
import { FilterFormComponent as PlanFilterComponet} from './components/plans/filter-form.component';
import {XrayKeyWarningComponent} from './agents/components/webcomponents/xray-key-warning-component';
import {XrayComponent} from './components/xraycomponent/xray.component';
import {TestProjectImportComponent} from "./components/webcomponents/test-project-import.component";
import {ConfirmationModalComponent} from "./shared/components/webcomponents/confirmation-modal.component";

export function momentAdapterFactory() {
  return adapterFactory(moment);
}

// AoT requires an exported function for factories
export function HttpLoaderFactory(httpClient: HttpClient) {
  return new TranslateHttpLoader(httpClient, environment.cloudFrontURL + '/assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    AppComponent,
    LeftNavComponent,
    ElementsListComponent,
    RunListComponent,
    TestDevelopmentComponent,
    TestCasesListComponent,
    ResultsListComponent,
    LabEnvironmentsInfoComponent,
    RunDetailsComponent,
    TestMachineResultDetailsComponent,
    SuiteResultDetailsComponent,
    TestCaseResultDetailsComponent,
    TestCaseResultsComponent,
    TestSuiteResultsComponent,
    TestMachineResultsComponent,
    RunDetailsQuickInfoComponent,
    ResultDonutChartComponent,
    TestCaseResultSummaryComponent,
    TestCaseDataDrivenResultsComponent,
    TestStepResultsComponent,
    TestStepResultDetailsComponent,
    TestCaseResultListComponent,
    TestStepResultFilterComponent,
    RunListFilterComponent,
    RunListInfoComponent,
    TestCaseResultFilterComponent,
    LabEnvironmentScreenShortInfoComponent,
    TestMachineDetailsQuickInfoComponent,
    TestSuiteDetailsQuickInfoComponent,
    TestSuiteResultFilterComponent,
    TestMachineResultFilterComponent,
    ResultStatusLabelInfoComponent,
    ResultAttachmentComponent,
    TestStepResultListItemComponent,
    ActionStepResultDetailsComponent,
    RestStepResultDetailsComponent,
    ElementFormComponent,
    RunListBarChartComponent,
    ScreenShortOverlayComponent,
    RunDetailsBarChartComponent,
    TestMachineTestCaseResultsComponent,
    TestMachineTestSuiteResultsComponent,
    TestSuiteTestCaseResultsComponent,
    VisualComparisonListComponent,
    TestMachineResultDetailsPaginationComponent,
    TestSuiteResultDetailsPaginationComponent,
    TestCaseResultDetailsPaginationComponent,
    SafeUrlPipe,
    TdRedirectComponent,
    ElementAutoHealingListComponent,
    VisualTestingComponent,
    ReportBugComponent,
    JiraIssueFormComponent,
    JiraIssueFormFieldComponent,
    JiraIssueDetailsComponent,
    FreshReleaseIssueFormComponent,
    FreshReleaseIssueDetailsComponent,
    AzureIssueFormComponent,
    AzureIssueDetailsComponent,
    YoutrackIssueDetailsComponent,
    YoutrackIssueFormComponent,
    WhyTestCaseFailedHelpComponent,
    ElementsRedirectComponent,
    TestCaseStatusFormComponent,
    SchedulePlanFormComponent,
    TestCasesFilterComponent,
    TestCasesFiltersListComponent,
    TestCasesRedirectComponent,
    TestCaseFilterFormComponent,
    TestCaseDetailsComponent,
    TestCaseDetailPaginationComponent,
    TestStepHelpComponent,
    TestStepHelpExamplesComponent,
    StepGroupTestCasesComponent,
    DryRunsComponent,
    StepsListComponent,
    TestCaseSummaryComponent,
    StepSummaryComponent,
    ElementDependentLocatorAttributesComponent,
    ElementAttributesComponent,
    ElementFiltersComponent,
    ElementFiltersListComponent,
    ElementFilterFormComponent,
    CreateTestGroupFromStepFormComponent,
    StepBulkUpdateFormComponent,
    TestStepHelpDocumentComponent,
    TestStepHelpSamplesComponent,
    ElementAddTagComponent,
    TestDataProfilesListComponent,
    TestDataImportComponent,
    DryRunFormComponent,
    TestDataCreateComponent,
    TestDataDetailsComponent,
    TestDataEditComponent,
    TestDataDataComponent,
    TestDataGridComponent,
    TestDataTestCasesComponent,
    TestStepMoreActionFormComponent,
    TestCaseCloneFormComponent,
    TestCaseFormComponent,
    TestCaseCreateHelpComponent,
    ListTagsComponent,
    EnvironmentsAutoCompleteComponent,
    DryRunRestFormComponent,
    DryRunMobileNativeFormComponent,
    TestPlanTimeOutSettingsComponent,
    DryRunMobileWebFormComponent,
    DryRunWebFormComponent,
    SelectTestLabComponent,
    EnvironmentsListComponent,
    EnvironmentsDetailsComponent,
    EnvironmentsFormComponent,
    EnvironmentsTableComponent,
    TestPlanListComponent,
    ScheduleListComponent,
    TestPlanDetailsComponent,
    TestPlanDetailsHeaderComponent,
    RunNowButtonComponent,
    ScheduleFormButtonComponent,
    ReportsButtonComponent,
    TestPlanSuitesListComponent,
    TestPlanDevicesListComponent,
    TestPlanCICDComponent,
    TestPlanSchedulesListComponent,
    TestPlanFormComponent,
    UploadsListComponent,
    TestPlanNameFormComponent,
    TestPlanSuiteSelectionComponent,
    TestPlanSettingsFormComponent,
    TestPlanTestMachineFormComponent,
    TestPlanDeviceFormComponent,
    TestPlanSuitesFormComponent,
    TestPlanPlatformOsVersionFormComponent,
    TestPlanExecutionEnvironmentInfoComponent,
    TestPlanAppUploadsFormComponent,
    TestPlanRecoveryActionsComponent,
    TestPlanAddSuiteFormComponent,
    ActionTemplatesListComponent,
    TestSuitesListComponent,
    TestDetailsListComponent,
    TestCasesListComponent,
    TestCasesListInTestSuiteComponent,
    TestPlansListInTestSuiteComponent,
    ActionElementSuggestionComponent,
    ActionTestDataParameterSuggestionComponent,
    ActionTestDataFunctionSuggestionComponent,
    ActionTestDataEnvironmentSuggestionComponent,
    TestSuiteFormComponent,
    TestSuiteAddCaseFormComponent,
    TestPlanTestMachineSelectFormComponent,
    SchedulesCalendarComponent,
    SchedulesListComponent,
    TdOverlayMenuButtonComponent,
    DashboardComponent,
    LatestRunsComponent,
    TestCaseCoverageSummaryComponent,
    WeeklyScheduleCalendarComponent,
    TestCaseByStatusSummaryComponent,
    TestCaseByTypeSummaryComponent,
    BySummaryDonutChartComponent,
    DryRunSavedConfigFormComponent,
    DryRunSavedConfigListComponent,
    EnvironmentDetailsHeaderComponent,
    EnvironmentTestPlansComponent,
    TagsChipListComponent,
    ReRunButtonComponent,
    ReRunDetailsComponent,
    ReRunIconComponent,
    ResultStatusLabelComponent,
    ReRunTestCaseResultsComponent,
    ReRunTestSuiteResultsComponent,
    ReRunTestMachineResultsComponent,
    ReRunDataDrivenResultsComponent,
    GlobalAddComponent,
    ReRunTestStepResultComponent,
    ReRunTestStepResultItemComponent,
    UsageDetailsComponent,
    LoginFormComponent,
    ElementDetailsHeaderComponent,
    ElementAttributeDetailsComponent,
    ElementMetadataComponent,
    MantisIssueFormComponent,
    MantisIssueDetailsComponent,
    BackLogIssueFormComponent,
    BackLogIssueDetailsComponent,
    ZepelIssueFormComponent,
    ZepelIssueDetailsComponent,
    BugZillaIssueFormComponent,
    BugZillaIssueDetailsComponent,
    TestCaseCoverageCountComponent,
    TrelloIssueFormComponent,
    TrelloIssueDetailsComponent,
    LinearIssueFormComponent,
    LinearIssueDetailsComponent,
    HelpActionsComponent,
    TestCaseDataDrivenResultListComponent,
    ElementBulkUpdateComponent,
    AddonAppComponent,
    RouteLoadingComponent,
    TestDataFilterComponent,
    TestDevIconComponent,
    SchedulePlanFormComponent,
    ConsentComponent,
    BackupFormComponent,
    AddonsComponent,
    TestsigmaLoveComponent,
    SupportComponent,
    OnboardingFormComponent,
    TelemetryNotificationComponent,
    QuickStartComponent,
    GetStartedBaseComponent,
    AddonDetailsComponent,
    SubmitElementReviewComponent,
    ChromeRecordButtonComponent,
    FirstChromeInstallComponent,
    MobileStepRecorderComponent,
    MirroringContainerComponent,
    MobileInspectionComponent,
    ElementsContainerComponent,
    SelectedElementsContainerComponent,
    AppSourceContainerComponent,
    SaveWorkWarningComponent,
    FreshChatWidgetComponent,
    ClickUpIssueFormComponent,
    ClickUpIssueDetailsComponent,
    DuplicateLocatorWarningComponent,
    InlineSortComponent,
    TestSuitePrerequisiteChangeComponent,
    TestCasePrerequisiteChangeComponent,
    ImportFormComponent,
    ImportGuideLinesWarningComponent,
    TestPlanLabTypeDropdownComponent,
    SelectTestLabComponent,
    VersionSelectionComponent,
    TestPlanSuiteMachineSelectionComponent,
    CreateMachineButtonComponent,
    MapTestMachineFormComponent,
    TestPlanEnvTagsComponent,
    TestPlanMachineInfoComponent,
    TestPlanMachineSelectionFormComponent,
    TestPlanMachinesListItemComponent,
    TestPlanSuiteFilterComponent,
    TestSuiteListItemComponent,
    FormHeaderComponent,
    ActionTestDataRuntimeVariableSuggestionComponent,
    ReRunPopupComponent,
    ReRunFormComponent,
    TestCaseMultiSelectComponent,
    FilterFormComponent,
    ResultFilterFormComponent,
    PlanFilterComponet,
    XrayKeyWarningComponent,
    XrayComponent,
    TestProjectImportComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    TranslateModule.forRoot({
      defaultLanguage: 'en',
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    SharedModule.forRoot(),
    BrowserAnimationsModule,
    SimpleNotificationsModule.forRoot(),
    TranslateModule,
    MatMenuModule,
    NgxJsonViewerModule,
    MatButtonModule,
    NgxPageScrollModule,
    MatRadioModule,
    CodemirrorModule,
    DragDropModule,
    MatSlideToggleModule,
    OverlayModule,
    MatIconModule,
    MatStepperModule,
    ReactiveFormsModule,
    CalendarModule.forRoot({provide: DateAdapter, useFactory: momentAdapterFactory}),
    Ng9OdometerModule.forRoot(),
    MatStepperModule,
    RecaptchaModule,
    HttpClientModule,
    CommonModule,
    MatCarouselModule.forRoot(),
    SettingsModule,
  ],

  entryComponents: [
    TestCaseResultSummaryComponent,
    TestCaseResultListComponent,
    TestStepResultFilterComponent,
    RunListInfoComponent,
    RunListFilterComponent,
    ElementFormComponent,
    ScreenShortOverlayComponent,
    VisualComparisonListComponent,
    ElementAutoHealingListComponent
  ],
  providers: [Title],
  bootstrap: [AppComponent],
  exports: [
    AutoRefreshComponent,
    ActionTestDataFunctionSuggestionComponent,
    StepSummaryComponent,
    ElementFormComponent,
    ActionTestDataEnvironmentSuggestionComponent,
    ActionTestDataParameterSuggestionComponent,
    TestStepMoreActionFormComponent,
    AutoRefreshComponent
  ],
  schemas: [
    NO_ERRORS_SCHEMA, CUSTOM_ELEMENTS_SCHEMA
  ]
})
export class AppModule {
}
