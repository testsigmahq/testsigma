import {ModuleWithProviders, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {MomentModule} from 'ngx-moment';
import {SessionService} from "app/shared/services/session.service";
import {TestsigmaOsConfigService} from "app/shared/services/testsigma-os-config.service";
import {HttpHeadersService} from "app/shared/services/http-headers.service";
import {UrlConstantsService} from "app/shared/services/url.constants.service";
import {BaseComponent} from "app/shared/components/base.component";
import {AuthenticationGuard} from "app/shared/guards/authentication.guard";
import {LoadingCircleComponent} from "app/shared/components/webcomponents/loading-circle.component"
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatSelectModule} from '@angular/material/select';
import {MatDialogModule} from '@angular/material/dialog';
import {UploadService} from "./services/upload.service";
import {MatTooltipModule} from '@angular/material/tooltip';
import {ElementService} from "./services/element.service";
import {DurationFormatComponent} from './components/webcomponents/duration-format.component';
import {ConfirmationModalComponent} from "app/shared/components/webcomponents/confirmation-modal.component";
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {NotificationsService, SimpleNotificationsModule} from 'angular2-notifications';
import {ResultPieChartColumnComponent} from './components/webcomponents/result-pie-chart-column.component';
import {HighchartsChartModule} from 'highcharts-angular';
import {TestDeviceResultService} from "./services/test-device-result.service";
import {TestMachineInfoColumnComponent} from './components/webcomponents/test-machine-info-column.component';
import {PaginationComponent} from "./components/webcomponents/pagination.component";
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {ScrollingModule} from "@angular/cdk/scrolling";
import {WorkspaceSwitcherComponent} from "./components/webcomponents/workspace-switcher.component";
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatTableModule} from '@angular/material/table';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatChipsModule} from '@angular/material/chips';
import {OverlayModule} from '@angular/cdk/overlay';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {AutoCompleteComponent} from "./components/webcomponents/auto-complete.component";
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {ClipboardModule} from '@angular/cdk/clipboard';
import {SearchPipe} from "./pipes/search.pipe";
import {RequestCache} from "./cache/request-cache";
import {CacheInterceptor} from "./cache/cache.interceptor";
import {ListAttachmentsComponent} from "./components/webcomponents/list-attachments.component";
import {MatTabsModule} from "@angular/material/tabs";
import {AgentsAutoCompleteComponent} from "../agents/components/webcomponents/agents-auto-complete.component";
import {AgentDevicesAutoCompleteComponent} from "../agents/components/webcomponents/agent-devices-auto-complete.component";
import {UploadsAutoCompleteComponent} from "./components/webcomponents/uploads-auto-complete.component";
import {ToggleSearchButtonComponent} from "./components/webcomponents/toggle-search-button.component";
import {SortByButtonComponent} from "./components/webcomponents/sort-by-button.component";
import {PlaceholderLoaderComponent} from './components/webcomponents/placeholder-loader.component';
import {DescriptionComponent} from './components/webcomponents/description.component';
import {MatRadioModule} from '@angular/material/radio';
import {MatStepperModule} from '@angular/material/stepper';
import {EditCommentModalComponent} from './components/webcomponents/edit-comment-modal.component';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {ViIconComponent} from "./components/webcomponents/vi-icon.component";
import {LinkedEntitiesModalComponent} from './components/webcomponents/linked-entities-modal.component';
import {RouterModule} from "@angular/router";
import {DesiredCapabilitiesComponent} from "./components/webcomponents/desired-capabilities.component";
import {UploadEntitiesModalComponent} from "./components/webcomponents/upload-entities-modal.component";
import {MatMenuModule} from '@angular/material/menu';
import {AsyncBtnDirective} from "./directives/async-btn.directive";
import { WarningModalComponent } from './components/webcomponents/warning-modal.component';
import { SanitizeHtmlPipe } from './pipes/sanitize-html.pipe';
import {InspectionModalComponent} from "./components/webcomponents/inspection-modal.component";
import {MatDividerModule} from "@angular/material/divider";
import {MultiSelectAutocomplete} from "./components/webcomponents/multiselect-autocomplete";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {UploadsFormComponent} from "./components/webcomponents/uploads-form.component";
import {InspectorTestLabSelectionComponent} from './components/webcomponents/inspector-test-lab-selection.component';
import { TooltipOnEllipsisDirective } from './directives/tooltip-on-ellipsis.directive';
import {AutoRefreshComponent} from "./components/webcomponents/auto-refresh.component";
import {TestsigmaCloudComponent} from "./components/webcomponents/testsigma-cloud.component";
import { TestCaseActionStepsComponent } from 'app/components/webcomponents/test-case-action-steps.component';
import {ActionTestStepListItemComponent} from "../components/webcomponents/action-test-step-list-item.component";
import {TestStepFormContainerComponent} from "../components/webcomponents/test-step-form-container.component";
import {DragDropModule} from "@angular/cdk/drag-drop";
import {ActionStepFormComponent} from "../components/webcomponents/action-step-form.component";
import {TestStepRestFormComponent} from "../components/webcomponents/test-step-rest-form.component";
import {TestStepForLoopFormComponent} from "../components/webcomponents/test-step-for-loop-form.component";
import {CodemirrorModule} from "@ctrl/ngx-codemirror";
import {TestStepGroupFormComponent} from "../components/webcomponents/test-step-group-form.component";
import {RestFormRequestDetailsComponent} from "../components/webcomponents/rest-form-request-details.component";
import {RestFormResponseDetailsComponent} from "../components/webcomponents/rest-form-response-details.component";
import {RestFormStoreDetailsComponent} from "../components/webcomponents/rest-form-store-details.component";
import {RestStepHeadersComponent} from "../components/webcomponents/rest-step-headers.component";
import {PromptModalComponent} from "./components/webcomponents/prompt-modal.component";
import {ToastrModule} from "ngx-toastr";
import {SortDirective} from "./directives/sort.directive";

@NgModule({
    declarations: [
        BaseComponent,
        LoadingCircleComponent,
        DurationFormatComponent,
        ConfirmationModalComponent,
        PromptModalComponent,
        ResultPieChartColumnComponent,
        TestMachineInfoColumnComponent,
        PaginationComponent,
        WorkspaceSwitcherComponent,
      AutoRefreshComponent,
        AutoCompleteComponent,
        SearchPipe,
        ListAttachmentsComponent,
        DesiredCapabilitiesComponent,
        AgentsAutoCompleteComponent,
        AgentDevicesAutoCompleteComponent,
        UploadsAutoCompleteComponent,
        ToggleSearchButtonComponent,
        SortByButtonComponent,
        PlaceholderLoaderComponent,
        DescriptionComponent,
        EditCommentModalComponent,
        ViIconComponent,
        LinkedEntitiesModalComponent,
        UploadEntitiesModalComponent,
        WarningModalComponent,
        SanitizeHtmlPipe,
        AsyncBtnDirective,
        SortDirective,
        InspectionModalComponent,
      UploadsFormComponent,
      MultiSelectAutocomplete,
      InspectorTestLabSelectionComponent,
      TooltipOnEllipsisDirective,
      TestsigmaCloudComponent,
      TestCaseActionStepsComponent,
      ActionTestStepListItemComponent,
      TestStepFormContainerComponent,
      ActionStepFormComponent,
      TestStepRestFormComponent,
      TestStepForLoopFormComponent,
      TestStepGroupFormComponent,
      RestFormRequestDetailsComponent,
      RestFormResponseDetailsComponent,
      RestFormStoreDetailsComponent,
      RestStepHeadersComponent
    ],
  providers: [
    TestsigmaOsConfigService,
    SessionService,
    HttpHeadersService,
    UrlConstantsService,
    AuthenticationGuard,
    UploadService,
    ElementService,
    NotificationsService,
    TranslateService,
    TestDeviceResultService,
    RequestCache
  ],
  imports: [
    CommonModule,
    MomentModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatTooltipModule,
    TranslateModule,
    HighchartsChartModule,
    MatProgressSpinnerModule,
    ScrollingModule,
    MatSlideToggleModule,
    MatTableModule,
    MatExpansionModule,
    MatChipsModule,
    OverlayModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    MatButtonToggleModule,
    ClipboardModule,
    MatTabsModule,
    ReactiveFormsModule,
    MatRadioModule,
    MatCheckboxModule,
    MatRadioModule,
    RouterModule,
    MatMenuModule,
    MatDividerModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    DragDropModule,
    CodemirrorModule,
    ToastrModule.forRoot()
  ],
  exports: [
    TranslateModule,
    MomentModule,
    BaseComponent,
    LoadingCircleComponent,
    MatAutocompleteModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatTooltipModule,
    DurationFormatComponent,
    ConfirmationModalComponent,
    PromptModalComponent,
    ResultPieChartColumnComponent,
    HighchartsChartModule,
    TestMachineInfoColumnComponent,
    SearchPipe,
    PaginationComponent,
    MatProgressSpinnerModule,
    ScrollingModule,
    WorkspaceSwitcherComponent,
    MatSlideToggleModule,
    MatTableModule,
    MatExpansionModule,
    MatChipsModule,
    OverlayModule,
    MatDatepickerModule,
    AutoCompleteComponent,
    AutoRefreshComponent,
    MatSnackBarModule,
    ClipboardModule,
    MatButtonToggleModule,
    ListAttachmentsComponent,
    MatTabsModule,
    AgentsAutoCompleteComponent,
    AgentDevicesAutoCompleteComponent,
    UploadsAutoCompleteComponent,
    SortByButtonComponent,
    ToggleSearchButtonComponent,
    PlaceholderLoaderComponent,
    DescriptionComponent,
    MatRadioModule,
    MatStepperModule,
    MatCheckboxModule,
    DesiredCapabilitiesComponent,
    UploadEntitiesModalComponent,
    SanitizeHtmlPipe,
    AsyncBtnDirective,
    SortDirective,
    InspectionModalComponent,
    UploadsFormComponent,
    MultiSelectAutocomplete,
    TooltipOnEllipsisDirective,
    TestsigmaCloudComponent,
    TestCaseActionStepsComponent,
    ActionTestStepListItemComponent,
    TestStepFormContainerComponent,
    MatDividerModule,
    ToastrModule
  ]
})
export class SharedModule {
  static forRoot(): ModuleWithProviders<SharedModule> {
    return {
      ngModule: SharedModule,
      providers: [
        RequestCache,
        {provide: HTTP_INTERCEPTORS, useClass: CacheInterceptor, multi: true},
        SessionService, UrlConstantsService, HttpHeadersService, AuthenticationGuard, UploadService,
        ElementService, NotificationsService, TranslateService, TestDeviceResultService, TestsigmaOsConfigService
      ]
    };
  }
}
