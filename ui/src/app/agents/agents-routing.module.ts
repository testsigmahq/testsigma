import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {AgentsComponent} from './agents.component';
import {AgentFormComponent} from "./components/agent-form.component";
import {ListComponent as AgentsListComponent} from './components/list.component';
import {DetailsComponent as AgentsDetailsComponent} from './components/details.component';
import {TestPlansComponent as AgentsTestPlansComponent} from './components/test-plans.component';
import {DryRunsComponent as AgentsDryRunsComponent} from './components/dry-runs.component';
import {DevicesComponent as AgentsDevicesComponent} from './components/devices.component';
import {RedirectGuard} from "./guards/redirect.guard";
import {InspectionLauncherComponent} from "./components/inspection-launcher.component";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";

const routes: Routes = [
  {
    path: 'record/:versionId',
    component: InspectionLauncherComponent,
    canActivate: [AuthenticationGuard]
  },
  {
    path: '',
    component: AgentsComponent,
    canActivate: [RedirectGuard, AuthenticationGuard],
    canActivateChild: [AuthenticationGuard],
    children: [
      {
        path: '',
        component: AgentsListComponent,
        data: {legacyURL: '#/agents', title: 'page_title.agents'},
      },
      {
        path: 'new',
        component: AgentsListComponent,
        data: {legacyURL: '#/agents', title: 'page_title.agents'},
        children: [{
          path: '',
          component: AgentFormComponent,
          data: {legacyURL: '#/agents/new', title: 'page_title.agent_create'},
        }]
      },
      {
        path: ':agentId',
        component: AgentsDetailsComponent,
        data: {legacyURL: '#/agents/:agentId', title: 'page_title.agent_details'},
        children: [
          {path: "", redirectTo: "test_plans"},
          {
            path: 'test_plans',
            component: AgentsTestPlansComponent,
            data: {legacyURL: '#/agents/:agentId', title: 'page_title.agent_test_plans'}
          },
          {
            path: 'dry_runs',
            component: AgentsDryRunsComponent,
            data: {legacyURL: '#/agents/:agentId', title: 'page_title.agent_dry_runs'}
          },
          {
            path: 'devices',
            component: AgentsDevicesComponent,
            data: {legacyURL: '#/agents/:agentId', title: 'page_title.agent_devices'}
          },
          {
            path: 'edit',
            component: AgentsDetailsComponent,
            data: {legacyURL: '#/agents/:agentId', title: 'page_title.agent_details'},
            children: [
              {
                path: '',
                component: AgentFormComponent,
                data: {legacyURL: '#/agents/:agentId/edit', title: 'page_title.agent_edit'},
                outlet: 'overlay',
              }
            ]
          }
        ]
      },

    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AgentsRoutingModule {
}
