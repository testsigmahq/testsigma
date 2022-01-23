import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {WorkspacesRoutingModule} from './workspaces-routing.module';
import {WorkspacesComponent} from './workspaces.component';
import {SharedModule} from "../shared/shared.module";
import {LeftNavComponent} from './components/left-nav.component';
import {FormComponent as VersionFormComponent} from './components/versions/form.component';
import {WorkspaceDetailsComponent as ApplicationDetailsComponent} from './components/workspace-details.component';
import {DetailsComponent as VersionDetailsComponent} from './components/versions/details.component';
import {ListComponent as VersionsListComponent} from './components/versions/list.component';
import {TestCasePrioritiesComponent} from "./components/test-case-priorities.component";
import {TestCaseTypesComponent} from "./components/test-case-types.component";
import {CloneVersionComponent} from './components/webcomponents/clone-version.component';
import {UserEmailsAutoCompleteComponent} from './components/webcomponents/users-auto-complete.component';
import {VersionHelpComponent} from './components/versions/version-help.component';

@NgModule({
  declarations: [
    WorkspacesComponent,
    LeftNavComponent,
    VersionFormComponent,
    ApplicationDetailsComponent,
    VersionDetailsComponent,
    VersionsListComponent,
    TestCaseTypesComponent,
    TestCasePrioritiesComponent,
    CloneVersionComponent,
    UserEmailsAutoCompleteComponent,
    VersionHelpComponent
  ],
  imports: [
    CommonModule,
    WorkspacesRoutingModule,
    SharedModule.forRoot()
  ]
})
export class WorkspacesModule {
}
