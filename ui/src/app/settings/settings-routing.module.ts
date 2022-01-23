import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SettingsComponent} from './settings.component';
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import {ListComponent as PluginsListComponent} from "./components/plugins/list.component";
import {ListComponent as IosSettingsListComponent} from './components/provisioning_profiles/list.component';
import {FormComponent as IosSettingsFormComponent} from './components/provisioning_profiles/form.component';
import { HomeComponent as TestsigmaHomeComponent } from './components/testsigma/home.component';
import {StorageComponent} from "./components/storage/storage.component";
import {AuthenticationConfigComponent} from "./components/authentication-config/authentication-config.component";
import {AboutComponent} from "./components/about/about.component";
import {TelemetryComponent} from "./components/telemetry/telemetry.component";
import {BackupsComponent} from "./components/backups.component";


const routes: Routes = [
  {
    path: '', data:{legacyURL:'#/settings'},
    component: SettingsComponent,
    canActivate: [AuthenticationGuard],
    canActivateChild: [AuthenticationGuard],
    children: [
      {path: '', pathMatch: 'full', redirectTo: 'plugs' },
      {path: 'plugs', component: PluginsListComponent,
        children: [
          {path: 'bug_report', component: PluginsListComponent, data:{legacyURL:'#/settings/bug_report', title: 'page_title.plugs'}},
          {path: 'test_lab', component: PluginsListComponent, data:{legacyURL:'#/settings/test_lab', title: 'page_title.plugs'}},
          {path: 'product_management', component: PluginsListComponent, data:{legacyURL:'#/settings/product_management', title: 'page_title.plugs'}},
          {path: 'ci_cd', component: PluginsListComponent, data:{legacyURL:'#/settings/ci_cd', title: 'page_title.plugs'}},
        ]
      },
      {
        path: 'provisioning_profiles',
          children: [
            { path: '', component: IosSettingsListComponent, data:{legacyURL:'#/settings/provisioning_profiles', title: 'page_title.ios_setting'}},
            { path: 'new', component: IosSettingsFormComponent, data:{legacyURL:'#/settings/new', title: 'page_title.ios_setting'}},
            { path: ':id', component: IosSettingsFormComponent, data:{legacyURL:'#/settings/form', title: 'page_title.ios_setting'}},
          ]
      },
      {
        path: 'testsigma',
        children: [
          { path: '', component: TestsigmaHomeComponent, data:{ title: 'page_title.testsigma_free_lab'}},
          { path: '/signup', component: IosSettingsListComponent, data:{ title: 'page_title.testsigma_free_lab.email_form'}},
          { path: '/otp', component: IosSettingsListComponent, data:{ title: 'page_title.testsigma_free_lab.activation'}},
        ]
      },
      {
        path: 'storage',
        children: [
          { path: '', component: StorageComponent, data:{ title: 'page_title.storage'}},
        ]
      },
      {
        path: 'auth',
        children: [
          { path: '', component: AuthenticationConfigComponent, data:{ title: 'page_title.auth_config'}},
        ]
      },
      {path: 'backups', component: BackupsComponent, data:{title: 'page_title.backups'}},
      {
        path: 'about',
        children: [
          { path: '', component: AboutComponent, data:{ title: 'page_title.settings.about'}},
        ]
      },
      {
        path: 'telemetry',
        children: [
          { path: '', component: TelemetryComponent, data:{ title: 'page_title.settings.telemetry'}},
        ]
      }

    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SettingsRoutingModule {

}
