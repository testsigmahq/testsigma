import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {InspectionModalComponent} from "../../shared/components/webcomponents/inspection-modal.component";
import {MobileRecordingComponent} from "./webcomponents/mobile-recording.component";
import {MirroringData} from "../models/mirroring-data.model";
import {ActivatedRoute, Params, Router} from '@angular/router';
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {AgentService} from "../services/agent.service";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService} from 'angular2-notifications';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {MobileStepRecorderComponent} from "./webcomponents/mobile-step-recorder.component";
import {MobileInspectionComponent} from "./webcomponents/mobile-inspection.component";

@Component({
  selector: 'app-mobile-inspection-launcher',
  templateUrl: './inspection-launcher.component.html',
  styles: []
})

export class InspectionLauncherComponent extends BaseComponent  implements OnInit {
  public data: MirroringData;
  private testcaseId: any;
  private testCaseResultId: any;
  private isUploads: boolean = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public dialog: MatDialog,
    private agentService: AgentService,
    private workspaceVersionService: WorkspaceVersionService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.data = new MirroringData();
      this.data.recording = true;
      this.data.workspaceVersionId = params.versionId;
      this.fetchVersion();
      this.data.uiId = null;

      if (this.route.snapshot.queryParamMap['params']) {
        let queryParams = this.route.snapshot.queryParamMap['params'];
        this.data.uiIdName = queryParams.uiIdName;
        this.data.uiIdScreenName = queryParams.uiIdScreenName;
        this.data.uiId = queryParams.elementId && queryParams.elementId != 'undefined' ? queryParams.elementId : null;
        this.testcaseId = queryParams.testCaseId;
        this.testCaseResultId = queryParams.testCaseResultId;
        this.data.isRecord = queryParams.isRecord == 'true';
        this.data.isStepRecord = queryParams.isStepRecord == 'true';
      }

      const dialogRef = this.dialog.open(InspectionModalComponent, {
        maxWidth: '50rem',
        minHeight: '28rem',
        panelClass: ['inspection-launcher-dialog', 'rds-none'],
        data: this.data,
        disableClose: true
      });

      dialogRef.afterClosed().subscribe(launch => {
        if(launch == undefined){
          this.router.navigate(['settings', 'billing']);
        } else if(launch&& launch== 'isUploads') {
          this.isUploads = true
          this.afterClose(true)
        } else if (launch){
          const isStepRecord = this.data.isStepRecord;
          const testCaseId = this.data.testCaseId;
          this.data = launch;
          this.data.isStepRecord = isStepRecord;
          this.data.testCaseId = testCaseId;
          this.launchRecording();
        }
        else{
          this.afterClose(this.testCaseResultId || this.testcaseId);
        }
      });
    });
  }

  fetchVersion() {
    this.workspaceVersionService.show(this.data?.workspaceVersionId).subscribe(res => {
      this.data.workspaceVersion = res;
    })
  }

  launchRecording() {
    let dialogRef;
    if(this.data.isStepRecord) {
      dialogRef = this.dialog.open(MobileStepRecorderComponent, {
        data: this.data,
        panelClass: ['mat-dialog', 'full-width', 'rds-none', 'w-100', 'h-100'],
        disableClose: true
      });
    } else {
      dialogRef = this.dialog.open(MobileInspectionComponent, {
        data: this.data,
        panelClass: ['mat-dialog', 'full-width', 'rds-none', 'w-100', 'h-100'],
        disableClose: true
      });
    }
    dialogRef.afterClosed().subscribe(launch => {
      if(launch)
      this.afterClose(true);
      else
        this.afterClose(this.testCaseResultId || this.testcaseId)
    });
  }

  afterClose(isNeedToClose?:boolean) {
    if (window.top != window) {
      if(this.isUploads) {
        this.isUploads = false;
        this.router.navigate(['/td',this.data.workspaceVersionId, 'uploads'])
      } else if (this.testCaseResultId && this.testCaseResultId != "undefined") {
        this.router.navigate(['/td/test_case_results', this.testCaseResultId])
      } else if (this.testcaseId && this.testcaseId != "undefined") {
        this.router.navigate(['/td', 'cases', this.testcaseId, 'steps']);
      } else {
        if (window.top.location.href.includes('elements')) {
          this.router.navigate(['td', this.data.workspaceVersionId, 'elements']);
        } else
        // @ts-ignore
        window.top.angular.element(window.top.document).scope().gotoTestDevelopment('home.applications.view.version.fields', false, true);
      }
      if(isNeedToClose)
      this.dialog.closeAll()
    } else {
      if (this.testCaseResultId && this.testCaseResultId != "undefined") {
        this.router.navigate(['/td/test_case_results', this.testCaseResultId])
      } else if (this.testcaseId && this.testcaseId != "undefined") {
        this.router.navigate(['/td', 'cases', this.testcaseId, 'steps']);
      } else {
        this.router.navigate(['td', this.data.workspaceVersionId, 'elements']);
      }
      if (isNeedToClose)
        this.dialog.closeAll()
    }
  }
}
