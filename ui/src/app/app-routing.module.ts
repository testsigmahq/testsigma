import {NgModule} from '@angular/core';
import {PreloadAllModules, RouterModule, Routes} from '@angular/router';
import {AuthenticationGuard} from "app/shared/guards/authentication.guard";
import {ElementsListComponent} from "./components/elements/list.component";
import {TestDevelopmentComponent} from "./components/test-development.component";
import {TestCasesListComponent} from "./components/cases/list.component";
import {ResultsListComponent} from "./components/results/list.component";
import {RunDetailsComponent} from "./components/results/run-details.component";
import {SuiteResultDetailsComponent} from "./components/results/suite-result-details.component";
import {TestMachineResultDetailsComponent} from "./components/results/test-machine-result-details.component";
import {TestCaseResultDetailsComponent} from "./components/results/test-case-result-details.component";
import {TestStepResultDetailsComponent} from "./components/results/test-step-result-details.component";
import {TdRedirectComponent} from "./components/td-redirect.component";
import {ElementsRedirectComponent} from "./components/elements/elements-redirect.component";
import {TestCasesRedirectComponent} from "./components/cases/test-cases-redirect.component";
import {TestCaseDetailsComponent} from "./components/cases/test-case-details.component";
import {DryRunsComponent} from "./components/cases/dry-runs.component";
import {StepsListComponent} from "./components/cases/steps-list.component";
import {StepGroupTestCasesComponent} from "./components/cases/step-group-test-cases.component";
import {ListComponent as TestDataProfilesListComponent} from "./components/data/list.component";
import {CreateComponent as TestDataCreateComponent} from "./components/data/create.component";
import {EditComponent as TestDataEditComponent} from "./components/data/edit.component";
import {DetailsComponent as TestDataDetailsComponent} from "./components/data/details.component";
import {DataComponent as TestDataDataComponent} from "./components/data/data.component";
import {TestCasesComponent as TestDataTestCasesComponent} from "./components/data/test-cases.component";
import {ListComponent as EnvironmentsListComponent} from "./components/environments/list.component";
import {FormComponent as EnvironmentFormComponent} from "./components/environments/form.component";
import {DetailsComponent as EnvironmentDetailsComponent} from "./components/environments/details.component";
import {TestCaseFormComponent} from "./components/cases/test-case-form.component";
import {ListComponent as UploadsListComponent} from "./components/uploads/list.component";
import {TestPlanListComponent} from "./components/plans/list.component";
import {ScheduleListComponent} from "./components/plans/schedule-list.component";

import {DetailsComponent as TestPlanDetailsComponent} from "./components/plans/details.component";
import {DetailsHeaderComponent as TestPlanDetailsHeaderComponent} from "./components/plans/details-header.component";
import {SuitesComponent as TestPlanSuitesListComponent} from './components/plans/suites.component';
import {DevicesComponent as TestPlanDevicesListComponent} from './components/plans/devices.component';
import {PlugsComponent as TestPlanPlugsComponent} from './components/plans/plugs.component';
import {SchedulesComponent as TestPlanSchedulesListComponent} from './components/plans/schedules.component';
import {FormComponent as TestPlanFormComponent} from './components/plans/form.component';
import {ListComponent as ActionTemplatesListComponent} from "./components/actions/list.component";
import {ListComponent as TestSuitesListComponent} from "./components/suites/list.component";
import {DetailsComponent as TestSuiteDetailsComponent} from "./components/suites/details.component";
import {TestCasesComponent as TestCasesListInTestSuiteComponent} from "./components/suites/test-cases.component";
import {TestPlansComponent as TestPlansListInTestSuiteComponent} from "./components/suites/test-plans.component";
import {FormComponent as TestSuiteFormComponent} from "./components/suites/form.component";
import {DashboardComponent} from "./components/dashboard.component";
import {DetailsHeaderComponent as EnvironmentDetailsHeaderComponent} from './components/environments/details-header.component';
import {TestPlansComponent as EnvironmentTestPlansComponent} from './components/environments/test-plans.component';
import {LoginFormComponent} from "./components/login-form.component";
import {UnAuthenticationGuardGuard} from "./guards/un-authentication-guard.guard";
import {AddonAppComponent} from "./components/addon-app.component";
import {ConsentGuard} from "./guards/consent.guard";
import {ConsentComponent} from "./components/webcomponents/consent.component";
import {OnboardingFormComponent} from "./components/onboarding-form.component";
import {OnboardingGuard} from "./guards/onboarding.guard";
import {Onboarding} from "./models/onboarding.model";
import {SupportComponent} from "./components/support.component";

const routes: Routes = [
  {
    path: 'login',
    component: LoginFormComponent,
    canActivate: [UnAuthenticationGuardGuard,OnboardingGuard],
    data: {title: 'page_title.login'}
  },
  {
    path: 'onboarding',
    component: OnboardingFormComponent,
    canActivate: [UnAuthenticationGuardGuard,OnboardingGuard],
    data: {title: "Onboarding"}
  },
  {
    path:'consent',
    canActivate: [AuthenticationGuard],
    canActivateChild: [ConsentGuard],
    children:[
      {
        path: '', component: ConsentComponent
      }
    ]

  },
  {
    path: '', canActivate: [AuthenticationGuard],
    canActivateChild: [AuthenticationGuard, OnboardingGuard],
    children: [

      {path: '', pathMatch: 'full', redirectTo: 'dashboard'},
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: {legacyURL: '#/dashboard_new', title: 'page_title.dashboard'}
      },
      {
        path: 'support',
        component: SupportComponent,
        data: {legacyURL: '#/support', title: 'page_title.support'}
      },
      {
        path: 'agents',
        canActivate: [AuthenticationGuard],
        canActivateChild: [AuthenticationGuard],
        loadChildren: () => import('./agents/agents.module').then(m => m.AgentsModule)
      },
      {
        path: 'td',
        data: {legacyURL: '#/td', title: 'page_title.td'},
        canActivate: [AuthenticationGuard],
        canActivateChild: [AuthenticationGuard],
        component: TdRedirectComponent,
        children: [
          {
            path: 'runs/:runId',
            data: {legacyURL: '#/td/runs/:runId', title: 'page_title.run_details'},
            component: RunDetailsComponent
          },
          {
            path: 'suite_results/:resultId',
            data: {legacyURL: '#/td/suite_results/:resultId', title: 'page_title.suite_result_details'},
            component: SuiteResultDetailsComponent
          },
          {
            path: 'machine_results/:resultId',
            data: {legacyURL: '#/td/machine_results/:resultId', title: 'page_title.test_machine_result_details'},
            component: TestMachineResultDetailsComponent
          },
          {
            path: 'test_case_results/:resultId',
            data: {legacyURL: '#/td/test_case_results/:resultId', title: 'page_title.test_case_result_details'},
            component: TestCaseResultDetailsComponent,
            children: [
              {

                path: 'step_results/:resultId',
                data: {
                  legacyURL: '#/td/test_case_results/:testResultId/step_results/:resultId',
                  title: 'page_title.test_step_result_details'
                },
                component: TestStepResultDetailsComponent
              }
            ]
          },
          {
            path: 'cases/:testCaseId',
            data: {legacyURL: '#/td/:testCaseId/case/details', title: 'page_title.test_case_details'},
            component: TestCaseDetailsComponent,
            children: [
              {path: '', pathMatch: 'full', redirectTo: 'steps'},
              {path: 'steps', component: StepsListComponent, data: {title: 'page_title.test_case_details'}},
              {path: 'dry_runs', component: DryRunsComponent},
              {path: 'dependents', component: StepGroupTestCasesComponent}
            ]
          },
          {
            path: 'environments/:environmentId',
            data: {legacyURL: '#/td/environments/:environmentId/details?v=:v', title: 'page_title.environment_details'},
            component: EnvironmentDetailsHeaderComponent,
            children: [
              {path: '', pathMatch: 'full', redirectTo: 'details'},
              {
                path: 'details',
                component: EnvironmentDetailsComponent,
                data: {title: 'page_title.environment_details'}
              },
              {path: 'plans', component: EnvironmentTestPlansComponent}
            ]
          },
          {
            path: ':versionId/data/new',
            data: {legacyURL: '#/td/:versionId/data/new', title: 'page_title.create_test_data'},
            component: TestDataCreateComponent
          },
          {
            path: ':versionId/plans/new',
            data: {legacyURL: '#/td/:versionId/plans/new', title: 'page_title.create_test_plan'},
            component: TestPlanFormComponent
          },
          {
            path: 'data/:testDataId/edit',
            data: {legacyURL: '#/td/data/:testDataId/edit', title: 'page_title.edit_test_data'},
            component: TestDataEditComponent
          },
          {
            path: 'plans/:testPlanId/edit',
            data: {legacyURL: '#/td/plans/:testPlanId/edit', title: 'page_title.edit_test_plan'},
            component: TestPlanFormComponent
          },
          {
            path: 'data/:testDataId',
            data: {legacyURL: '#/td/data/:testDataId/details', title: 'page_title.test_data_details'},
            component: TestDataDetailsComponent,
            children: [
              {path: '', pathMatch: 'full', redirectTo: 'sets', data: {legacyURL: '#/td/data/:testDataId/sets'}},
              {path: 'sets', component: TestDataDataComponent, data: {title: 'page_title.test_data_details'}},
              {path: 'cases', component: TestDataTestCasesComponent}
            ]
          },
          {
            path: 'plans/:testPlanId',
            data: {legacyURL: '#/td/plans/:testPlanId/details'},
            component: TestPlanDetailsHeaderComponent,
            children: [
              {path: '', pathMatch: 'full', redirectTo: 'details'},
              {path: 'details', component: TestPlanDetailsComponent, data: {title: 'page_title.test_plan_details'}},
              {path: 'suites', component: TestPlanSuitesListComponent},
              {path: 'devices', component: TestPlanDevicesListComponent},
              {path: 'plugs', component: TestPlanPlugsComponent},
              {path: 'schedules', component: TestPlanSchedulesListComponent}
            ]
          },
          {
            path: 'suites/:testSuiteId', data: {legacyURL: '#/td/suites/:testSuiteId'},
            component: TestSuiteDetailsComponent,
            children: [
              {path: '', pathMatch: 'full', redirectTo: 'cases'},
              {
                path: 'cases',
                component: TestCasesListInTestSuiteComponent,
                data: {title: 'page_title.test_suite_details'}
              },
              {path: 'plans', component: TestPlansListInTestSuiteComponent}
            ]
          },
          {
            path: ':versionId/suites/new',
            data: {legacyURL: '#/td/:versionId/suites/new', title: 'page_title.create_test_suite'},
            component: TestSuiteFormComponent,
          },
          {
            path: ':versionId/suites/:testSuiteId/edit',
            data: {legacyURL: '#/td/:versionId/suites/:testSuiteId/edit', title: 'page_title.edit_test_suite'},
            component: TestSuiteFormComponent,
          },
          {
            path: ':versionId',
            component: TestDevelopmentComponent,
            data: {legacyURL: '#/td/:versionId', title: 'page_title.td'},
            children: [
              {path: '', pathMatch: 'full', redirectTo: 'cases'},
              {
                path: 'cases',
                data: {legacyURL: '#/td/:versionId/cases'},
                children: [
                  {path: '', component: TestCasesRedirectComponent},
                  {
                    path: 'filter/:filterId', component: TestCasesListComponent,
                    data: {legacyURL: '#/td/:versionId/cases/filter/:filterId', title: 'page_title.test_cases'}
                  },
                  {
                    path: 'create',
                    data: {
                      legacyURL: '#/td/:versionId/case/create?isGroup=:isGroup',
                      title: 'page_title.create_test_case'
                    },
                    component: TestCaseFormComponent
                  },
                  {
                    path: ':testCaseId/edit',
                    data: {legacyURL: '#/td/:testCaseId/case/edit', title: 'page_title.edit_test_case'},
                    component: TestCaseFormComponent
                  }
                ]
              },
              {
                path: 'step_groups',
                data: {legacyURL: '#/td/:versionId/step_groups'},
                children: [
                  {path: '', component: TestCasesRedirectComponent},
                  {
                    path: 'filter/:filterId', component: TestCasesListComponent,
                    data: {legacyURL: '#/td/:versionId/step_groups/filter/:filterId', title: 'page_title.test_step_groups'}
                  },
                  {
                    path: 'create',
                    data: {
                      legacyURL: '#/td/:versionId/step_groups/create?isGroup=:isGroup',
                      title: 'page_title.create_step_group'
                    },
                    component: TestCaseFormComponent
                  },
                  {
                    path: ':testCaseId/edit',
                    data: {legacyURL: '#/td/:testCaseId/step_groups/edit', title: 'page_title.edit_step_group'},
                    component: TestCaseFormComponent
                  }
                ]
              },
              {
                path: 'elements',
                data: {legacyURL: '#/td/:versionId/elements', title: 'page_title.elements'},
                children: [
                  {
                    path: '', children: [
                      {path: '', component: ElementsRedirectComponent},
                      {path: 'filter/:filterId', component: ElementsListComponent},
                    ]
                  }
                ]
              },
              {
                path: 'data',
                data: {legacyURL: '#/td/:versionId/data', title: 'page_title.test_data_profiles'},
                component: TestDataProfilesListComponent
              },
              {
                path: 'environments',
                data: {legacyURL: '#/td/:versionId/environments', title: 'page_title.environments'},
                children: [
                  {path: '', data: {legacyURL: '#/td/:versionId/environments'}, component: EnvironmentsListComponent},
                  {
                    path: 'new',
                    data: {legacyURL: '#/td/:versionId/environments/new', title: 'page_title.create_environment'},
                    component: EnvironmentFormComponent
                  },
                  {
                    path: ':environmentId/edit',
                    data: {
                      legacyURL: '#/td/:versionId/environments/:environmentId/edit',
                      title: 'page_title.edit_environment'
                    },
                    component: EnvironmentFormComponent
                  }
                ]
              },
              {
                path: 'plans',
                data: {legacyURL: '#/td/:versionId/plans', title: 'page_title.test_plans'},
                children: [
                  {path: '', data: {legacyURL: '#/td/:versionId/plans'}, component: TestPlanListComponent},
                  {
                    path: 'schedules',
                    data: {legacyURL: '#/td/:versionId/plans/schedules', title: 'page_title.test_plan_schedules'},
                    component: ScheduleListComponent
                  }
                ]
              },
              {
                path: 'uploads',
                data: {legacyURL: '#/td/:versionId/uploads', title: 'page_title.uploads'},
                component: UploadsListComponent
              },
              {
                path: 'actions',
                data: {legacyURL: '#/td/:versionId/actions', title: 'page_title.action'},
                component: ActionTemplatesListComponent
              },
              {
                path: 'suites',
                data: {legacyURL: '#/td/:versionId/suites', title: 'page_title.test_suites'},
                component: TestSuitesListComponent
              },
              {
                path: 'results',
                data: {legacyURL: '#/td/:versionId/results', title: 'page_title.run_results'},
                component: ResultsListComponent
              },
            ]
          },
        ]
      },
      {
        path: 'addons',
        data: {legacyURL: '#/addons', title: 'page_title.add_ons'},
        component: AddonAppComponent
      },
      {
        path: 'settings',
        data: {legacyURL: '#/settings'},
        loadChildren: () => import('./settings/settings.module').then(m => m.SettingsModule)
      },
      {path: 'workspaces', loadChildren: () => import('./workspaces/workspaces.module').then(m => m.WorkspacesModule)}
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    onSameUrlNavigation: 'reload',
    preloadingStrategy: PreloadAllModules,
    relativeLinkResolution: 'legacy'
})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
