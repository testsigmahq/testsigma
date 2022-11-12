import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SettingsRoutingModule} from './settings-routing.module';
import {SettingsComponent} from './settings.component';
import {SharedModule} from "../shared/shared.module";
import {LeftNavComponent} from './components/webcomponents/left-nav.component';
import {ListComponent as PluginsListComponent} from './components/plugins/list.component';
import {CreateComponent as FreshReleaseCreateComponent} from './components/plugins/freshrelease/create.component';
import {DetailsComponent as FreshReleaseDetailsComponent} from './components/plugins/freshrelease/details.component';
import {CreateComponent as TrelloCreateComponent} from './components/plugins/trello/create.component';
import {DetailsComponent as TrelloDetailsComponent} from './components/plugins/trello/details.component';
import {CreateComponent as JiraCreateComponent} from './components/plugins/jira/create.component';
import {DetailsComponent as JiraDetailsComponent} from "./components/plugins/jira/details.component";
import {DetailsComponent as AzureDetailsComponent} from './components/plugins/azure/details.component';
import {CreateComponent as AzureCreateComponent} from './components/plugins/azure/create.component';
import {CreateComponent as LinearCreateComponent} from './components/plugins/linear/create.component';
import {DetailsComponent as LinearDetailsComponent} from './components/plugins/linear/details.component';
import {DragDropModule} from "@angular/cdk/drag-drop";
import {ListComponent} from './components/provisioning_profiles/list.component';
import {FormComponent as IosSettingsFormComponent} from './components/provisioning_profiles/form.component';
import {HomeComponent as TestsigmaHomeComponent} from './components/testsigma/home.component';
import { StorageComponent } from './components/storage/storage.component';
import {DetailsComponent as AzureStorageConfigDetailsComponent} from './components/storage/azure/details.component';
import {DetailsComponent as AwsStorageConfigDetailsComponent} from './components/storage/aws/details.component';
import {DetailsComponent as LocalStorageConfigDetailsComponent} from './components/storage/local/details.component'
import {CreateComponent as AzureStorageConfigCreateComponent} from './components/storage/azure/create.component';
import {CreateComponent as AwsStorageConfigCreateComponent} from './components/storage/aws/create.component';
import {CreateComponent as LocalStorageConfigCreateComponent} from './components/storage/local/create.component';
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {SigninComponent} from "./components/signin/signin.component";
import { AuthenticationConfigComponent } from './components/authentication-config/authentication-config.component';
import {CreateComponent as FormLoginCreateComponent} from './components/authentication-config/form-login/create.component';
import {CreateComponent as GoogleAuthCreateComponent} from './components/authentication-config/google/create.component';
import {DetailsComponent as GoogleAuthDetailsComponent} from './components/authentication-config/google/details.component';
import {DetailsComponent as FormLoginDetailsComponent} from './components/authentication-config/form-login/details.component';
import {CreateComponent as ApiCreateComponent} from './components/authentication-config/api/create.component';
import {DetailsComponent as ApiDetailsComponent} from './components/authentication-config/api/details.component';
import { FormComponent as JwtUpdateFormComponent } from './components/authentication-config/jwt/form.component';
import { ConfirmComponent as ConfirmRestartComponent } from './components/authentication-config/confirm.component';
import { AboutComponent } from './components/about/about.component';
import {EmailSigninComponent} from "./components/email-sigin/email-signin.component";
import {MatCardModule} from "@angular/material/card";
import { TelemetryComponent } from './components/telemetry/telemetry.component';
import {BackupsComponent} from './components/backups.component';
import {LogoutPromptComponent} from './components/authentication-config/logout-prompt.component';
import {CreateComponent} from "./components/plugins/privateGrid/create.component";
import {DetailsComponent} from "./components/plugins/privateGrid/details.component";
import {CreateComponent as XrayCreateComponent} from "./components/plugins/xray/create.component";
import {DetailsComponent as XrayDetailsComponent} from "./components/plugins/xray/details.component";

@NgModule({
  declarations: [
    SettingsComponent,
    LeftNavComponent,
    PluginsListComponent,
    FreshReleaseCreateComponent,
    FreshReleaseDetailsComponent,
    TrelloCreateComponent,
    TrelloDetailsComponent,
    LinearCreateComponent,
    LinearDetailsComponent,
    JiraCreateComponent,
    JiraDetailsComponent,
    AzureCreateComponent,
    AzureDetailsComponent,
    ListComponent,
    IosSettingsFormComponent,
    TestsigmaHomeComponent,
    SigninComponent,
    StorageComponent,
    AwsStorageConfigCreateComponent,
    AwsStorageConfigDetailsComponent,
    AzureStorageConfigCreateComponent,
    AzureStorageConfigDetailsComponent,
    LocalStorageConfigCreateComponent,
    LocalStorageConfigDetailsComponent,
    AuthenticationConfigComponent,
    FormLoginCreateComponent,
    FormLoginDetailsComponent,
    GoogleAuthDetailsComponent,
    GoogleAuthCreateComponent,
    ApiCreateComponent,
    ApiDetailsComponent,
    JwtUpdateFormComponent,
    ConfirmRestartComponent,
    AboutComponent,
    EmailSigninComponent,
    TelemetryComponent,
    BackupsComponent,
    LogoutPromptComponent,
    CreateComponent,
    DetailsComponent,
    XrayCreateComponent,
    XrayDetailsComponent
  ],
  exports: [
    SigninComponent

  ],
    imports: [
        CommonModule,
        SettingsRoutingModule,
        SharedModule.forRoot(),
        DragDropModule,
        MatListModule,
        MatIconModule,
        MatCardModule
    ]
})

export class SettingsModule {
}
