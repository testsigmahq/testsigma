import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {WorkspacesComponent} from './workspaces.component';
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import {FormComponent as VersionFormComponent} from './components/versions/form.component';
import {WorkspaceDetailsComponent as ApplicationDetailsComponent} from "./components/workspace-details.component";
import {DetailsComponent as VersionDetailsComponent} from "./components/versions/details.component";
import {ListComponent as VersionsListComponent} from './components/versions/list.component';
import {TestCasePrioritiesComponent} from "./components/test-case-priorities.component";
import {TestCaseTypesComponent} from "./components/test-case-types.component";
import {LeftNavComponent} from "./components/left-nav.component";

const routes: Routes = [
  {
    path: '',
    component: WorkspacesComponent,
    canActivate: [AuthenticationGuard],
    canActivateChild: [AuthenticationGuard],
    children: [
      {
        path: ':workspaceId',
        component: LeftNavComponent,
        children: [
          { path: '', redirectTo: 'details' },
          {
            path: 'details', component: ApplicationDetailsComponent,
            data: { title: 'page_title.application_details'}
          },
          {
            path: 'test_case_priorities', component: TestCasePrioritiesComponent,
            data: { title: 'page_title.test_case_priorities'}
          },
          {
            path: 'test_case_types', component: TestCaseTypesComponent,
            data: { title: 'page_title.test_case_types'}
          },
          {
            path: 'versions',
            children: [
              {
                path: '', component: VersionsListComponent,
                data: {
                  title: 'page_title.workspace_versions'
                }
              },
              {
                path: 'new', component: VersionFormComponent,
                data: {
                  title: 'page_title.create_version'
                }
              },
              {
                path: ':versionId/edit', component: VersionFormComponent,
                data: {
                  title: 'page_title.edit_version'
                }
              },
              {
                path: ':versionId', component: VersionsListComponent,
                children: [
                  {path: '', redirectTo: 'details'},
                  {
                    path: 'details', component: VersionDetailsComponent,
                    data: {
                      title: 'page_title.version_details'
                    }
                  }
                ]
              }
            ]
          }
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WorkspacesRoutingModule {
}
