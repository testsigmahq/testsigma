import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AgentsRoutingModule} from './agents-routing.module';
import {AgentsComponent} from './agents.component';
import {AgentFormComponent} from './components/agent-form.component'
import {DevicesComponent} from './components/devices.component';
import {ListComponent as AgentsListComponent} from './components/list.component';
import {DetailsComponent} from './components/details.component';
import {TestPlansComponent} from './components/test-plans.component';
import {DryRunsComponent} from './components/dry-runs.component';
import {SharedModule} from "../shared/shared.module";
import {RedirectGuard} from "./guards/redirect.guard";
import {InspectionLauncherComponent} from './components/inspection-launcher.component';
import {InspectionModalComponent} from '../shared/components/webcomponents/inspection-modal.component';
import {MobileRecordingComponent} from './components/webcomponents/mobile-recording.component';
import {ConfirmationModalComponent} from '../shared/components/webcomponents/confirmation-modal.component';
import {SessionExpiredModalComponent} from './components/webcomponents/session-expired-modal.component';
import {SendKeyModalComponent} from './components/webcomponents/send-key-modal.component';
import { SearchElementsModalComponent } from './components/webcomponents/search-elements-modal.component';
import { AgentsFilterComponent } from './components/webcomponents/agents-filter.component';
import { ElementsContainerComponent } from './components/webcomponents/elements-container.component';
import { SelectedElementsContainerComponent } from './components/webcomponents/selected-elements-container.component';
import { AppSourceContainerComponent } from './components/webcomponents/app-source-container.component';
import { MirroringContainerComponent } from './components/webcomponents/mirroring-container.component';
import { MobileInspectionComponent } from './components/webcomponents/mobile-inspection.component';
import { MobileStepRecorderComponent } from './components/webcomponents/mobile-step-recorder.component';
import {SaveWorkWarningComponent} from "./components/webcomponents/save-work-warning.component";

@NgModule({
  declarations: [
    AgentsComponent,
    AgentFormComponent,
    AgentsListComponent,
    DetailsComponent,
    TestPlansComponent,
    DryRunsComponent,
    DevicesComponent,
    DevicesComponent,
    InspectionLauncherComponent,
    MobileRecordingComponent,
    SendKeyModalComponent,
    SessionExpiredModalComponent,
    SendKeyModalComponent,
    SearchElementsModalComponent,
    AgentsFilterComponent,
    ElementsContainerComponent,
    SelectedElementsContainerComponent,
    AppSourceContainerComponent,
    MirroringContainerComponent,
    MobileInspectionComponent,
    SaveWorkWarningComponent,
    MobileStepRecorderComponent
  ],
  providers: [
    RedirectGuard
  ],
  imports: [
    CommonModule,
    AgentsRoutingModule,
    SharedModule.forRoot()
  ],
  exports: [],
  entryComponents: [
    InspectionModalComponent,
    MobileRecordingComponent,
    ConfirmationModalComponent,
    SendKeyModalComponent,
    SessionExpiredModalComponent
  ]
})
export class AgentsModule {
}
