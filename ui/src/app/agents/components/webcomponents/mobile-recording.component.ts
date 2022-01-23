import {Component, Inject, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef, MatDialogState} from '@angular/material/dialog';
import {UploadService} from '../../../shared/services/upload.service';
import {DevicesService} from '../../services/devices.service';
import {AuthenticationGuard} from '../../../shared/guards/authentication.guard';
import {MobileElement} from '../../models/mobile-element.model';
import {JsonPipe} from '@angular/common';
import {MirroringData} from '../../models/mirroring-data.model';
import {AgentDevice} from '../../models/agent-device.model';
import {MirroringResponse} from '../../models/mirroring-response.model';
import {SessionCreationRequest} from '../../models/session-creation-request.model';
import {SessionResponse} from '../../models/session-response.model';
import {ApplicationPathType} from '../../../enums/application-path-type.enum';
import {TestPlanLabType} from '../../../enums/test-plan-lab-type.enum';
import {WorkspaceType} from '../../../enums/workspace-type.enum';
import {Platform} from '../../../enums/platform.enum';
import {Element} from '../../../models/element.model';
import {ElementService} from '../../../shared/services/element.service';
import {ScreenDimensions} from '../../models/screen-dimensions.model';
import {BaseComponent} from '../../../shared/components/base.component';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {interval, Subscription} from 'rxjs';
import {SessionExpiredModalComponent} from './session-expired-modal.component';
import {MobileInspectionStatus} from '../../../enums/mobile-inspection-status.enum';
import {MobileInspection} from '../../../models/mobile-inspection.model';
import {ScreenOrientation} from "../../../enums/screen-orientation.enum";
import {MirroringContainerComponent} from "./mirroring-container.component";
import {ElementsContainerComponent} from "./elements-container.component";
import {FormGroup} from "@angular/forms";

@Component({
  selector: 'app-mobile-recording',
  template: ``,
  providers: [JsonPipe],
})
export class MobileRecordingComponent extends BaseComponent implements OnInit {

  public checkingAvailability: Boolean = true;
  public deviceIsOffline: Boolean = true;
  public mirroring: boolean = true;
  public dataSource: any;
  public sessionId: String;
  public mobileSessionId: number;
  public element: Element;
  public elements: Element[] =[];
  public loading: boolean;
  public loadingActions: boolean;
  public uiId: number;
  public deleteClicked: true;
  public isEdit: boolean;
  public driverSessionKeepAlivePoller: Subscription;
  public platform: Platform;
  public inspectorSessionKeepAlivePoller: Subscription;
  public timeout: any;
  public viewType: string = 'NATIVE_APP';
  public currentPageSource: MobileElement;
  public devicesService: DevicesService; //CloudDevicesService
  public fullScreenMode: boolean;
  public uiElementForm: FormGroup;
  public selectedIndex: number = 2;
  public selectedElement1: Element;
  public mobileRecorderComponentInstance: any;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    @Inject(MAT_DIALOG_DATA) public data: MirroringData,
    public JsonPipe: JsonPipe,
    public localDeviceService: DevicesService,
    //public cloudDeviceService: CloudDevicesService,
    public elementService: ElementService,
    public dialogRef: MatDialogRef<MobileRecordingComponent>,
    public dialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
    this.devicesService = this.data.testsigmaAgentEnabled ? localDeviceService : localDeviceService;//cloudDeviceService;
    this.fullScreenMode = this.data.showFullScreen;
    this.selectedIndex = this.fullScreenMode? 0:1;
  }

  ngOnInit() {
    this.loading = true;
    this.element = null;
    this.elements = [];
    if (this.data.recording) {
      this.mirroring = false;
      this.startInspection();
    } else {
      this.startMirroring();
    }

    this.uiId = this.data.uiId;
    this.isEdit = (this.data.uiIdName || (this.uiId != null))
      && (this.data.isRecord == undefined || !this.data.isRecord);

    this.platform = this.isIosNative() ? Platform.iOS : Platform.Android;
  }

  ngOnChanges(changes:SimpleChanges){
    console.log(changes);
  }

  synchronousDelay(milliseconds: number) {
    return new Promise(resolve => setTimeout(resolve, milliseconds));
  }

  isIosNative(): boolean {
    return (this.data.workspaceVersion && this.data.workspaceVersion.workspace && this.data.workspaceVersion.workspace.workspaceType == WorkspaceType.IOSNative);
  }

  startInspection() {
    if (this.data.device) {
      this.devicesService.show(this.data.device).subscribe((device: AgentDevice) => {
        this.checkingAvailability = false;
        if (device.isOnline) {
          this.startSession();
        } else {
          this.deviceIsOffline = true;
        }
      }, (err) => {
        console.log("Error in start inspection - ", err);
        this.showNotification(NotificationType.Error, this.translate.instant("mobile_recorder.notification.start.failure"));
        this.loading = false;
        if (this.data.recording) {
          this.dialogRef.close();
        }
      });
    } else {
      this.startSession();
    }
  }

  startMirroring() {
    this.devicesService
      .startMirroring(this.data.device) //, this.authGuard.user)
      .subscribe({
        next: (mirroringResponse: MirroringResponse) => {
          this.openSocketConnection();
          this.showNotification(NotificationType.Success, this.translate.instant("mobile_recorder.notification.start.success"));
        },
        error: (err: any) => {
          console.log("Error in start mirroring  ------ ", err);
          this.showNotification(NotificationType.Error, this.translate.instant("mobile_recorder.notification.start.failure"));
          this.loading = false;
          this.dialogRef.close();
        }
      });
  }

  startSession() {
    this.devicesService.startSession(this.getMobileInspection()).subscribe({
        next:
          (mobileInspection: MobileInspection) => {
            this.mobileSessionId = mobileInspection.id;
            this.createSession(mobileInspection.id);
          },
        error: (error: any) => {
          this.showNotification(NotificationType.Error, error && error.error && error.error.code ? error.error.code :
            this.translate.instant("mobile_recorder.notification.start.failure"));
          this.loadingActions = false;
          this.loading = false;
        }
      }
    );
  }

  createSession(id: number) {
    this.loadingActions = true;
    const driverSessionRequest = this.getSessionRequest();
    driverSessionRequest.mobileSessionId = id;
    this.devicesService.createSession(driverSessionRequest).subscribe({
      next:
        (sessionResponse: SessionResponse) => {
          this.sessionId = sessionResponse.sessionId;
          if (this.data.recording) {
            this.devicesService.getScreenDimensions(this.sessionId).subscribe((screenDimensions: ScreenDimensions) => {
              this.getMirroringContainerComponent().createCanvas(screenDimensions);
              this.devicesService.getOrientation(this.sessionId).subscribe((res: ScreenOrientation) => {
                this.getMirroringContainerComponent().isLandscapeMode = res == ScreenOrientation.LANDSCAPE;
                this.renderCurrentScreenshot();
                this.renderCurrentScreenshot();
                this.startDriverSessionKeepAlivePoller();
                this.startInspectorSessionKeepAlivePoller();
                this.attachActiveSessionTimer();
              })
            });
          }
          this.renderCurrentScreenshot();
          this.loadingActions = false;
        },
      error: (error: any) => {
        if (this.mobileSessionId) {
          this.devicesService.updateSession(this.getMobileInspectionUpdate())
            .subscribe({
              next: () => {
              }
            });
          this.mobileSessionId = null;
        }
        console.log("Error while creating the session - ", error);
        this.showNotification(NotificationType.Error, this.translate.instant("mobile_recorder.notification.start.failure"));
        this.loadingActions = false;
        this.loading = false;
      }
    });
  }

  startDriverSessionKeepAlivePoller() {
    let _this = this;
    this.driverSessionKeepAlivePoller = interval(40 * 1000).subscribe(() => {
      _this.devicesService.getScreenDimensions(_this.sessionId).subscribe({
        next: () => {
        }
      });
    });
  }

  startInspectorSessionKeepAlivePoller() {
    let _this = this;
    this.inspectorSessionKeepAlivePoller = interval(120 * 1000).subscribe(() => {
      const mobileInspection = new MobileInspection();
      mobileInspection.id = _this.mobileSessionId;
      _this.devicesService.updateSession(mobileInspection).subscribe({
        next: (mobileInspection: MobileInspection) => {
          this.notifyMobileInspectionStoppedStatus(mobileInspection);
        }
      });
    });
  }

  notifyMobileInspectionStoppedStatus(mobileInspection: MobileInspection) {
    if (mobileInspection.status === MobileInspectionStatus.FINISHED) {
      this.mobileSessionId = null;
      this.stopInspectorSessionKeepAlivePoller();
      this.stopDriverSessionKeepAlivePoller();
      this.getMirroringContainerComponent().fabricCanvas.clear();
      this.translate.get('mobile_recorder.notification.stopped.alert',
        {userEmail: ("Someone")}).subscribe((res) => {
        this.showNotification(NotificationType.Error, res);
      })
      this.loadingActions = false;
      this.loading = false;
    }
  }

  stopDriverSessionKeepAlivePoller() {
    if (this.driverSessionKeepAlivePoller != null) {
      this.driverSessionKeepAlivePoller.unsubscribe();
    }
  }

  stopInspectorSessionKeepAlivePoller() {
    if (this.inspectorSessionKeepAlivePoller != null) {
      this.inspectorSessionKeepAlivePoller.unsubscribe();
    }
  }

  getSessionRequest() {
    let sessionRequest = new SessionCreationRequest();
    if (this.data.device) {
      sessionRequest.executionLabType = TestPlanLabType.Hybrid;
      sessionRequest.uniqueId = this.data.device.uniqueId;
    } else {
      sessionRequest.executionLabType = this.data.testPlanLabType;
      sessionRequest.uniqueId = this.data?.cloudDevice?.deviceName;
    }
    if (this.isIosNative()) {
      sessionRequest.workspaceType = WorkspaceType.IOSNative;
      sessionRequest.platform = Platform.iOS;
    } else {
      sessionRequest.workspaceType = WorkspaceType.AndroidNative;
      sessionRequest.platform = Platform.Android;
    }
    if (!this.data.isManually) {
      sessionRequest.applicationPathType = ApplicationPathType.UPLOADS;
    } else if (this.data.app_package && this.data.app_activity) {
      sessionRequest.applicationPathType = ApplicationPathType.APP_DETAILS;
    } else if (this.data.upload) {
      sessionRequest.applicationPathType = ApplicationPathType.UPLOADS;
    }
    return sessionRequest;
  }

  getMobileInspection() {
    let mobileInspection = new MobileInspection();
    if (this.data.device) {
      mobileInspection.labType = TestPlanLabType.Hybrid;
      mobileInspection.agentDeviceId = this.data.device.id;
      mobileInspection.status = MobileInspectionStatus.TRIGGERED;
    } else {
      mobileInspection.labType = this.data.testPlanLabType;
      mobileInspection.status = MobileInspectionStatus.TRIGGERED;
      mobileInspection.platformDeviceId = this.data?.cloudDevice?.id;
    }
    mobileInspection.capabilities = this.data.capabilities;
    if (this.isIosNative()) {
      mobileInspection.platform = Platform.iOS;
    } else {
      mobileInspection.platform = Platform.Android;
    }
    if (this.data.isManually) {
      mobileInspection.applicationPathType = ApplicationPathType.APP_DETAILS;
      if (this.isIosNative()) {
        mobileInspection.bundleId = this.data?.bundleId;
      } else {
        mobileInspection.applicationPackage = this.data.app_package;
        mobileInspection.appActivity = this.data.app_activity;
      }
    } else {
      mobileInspection.appUploadId = this.data.uploadId;
      mobileInspection.applicationPathType = ApplicationPathType.UPLOADS;
    }
    return mobileInspection;
  }

  getMobileInspectionUpdate() {
    let mobileInspection = new MobileInspection();
    mobileInspection.id = this.mobileSessionId;
    mobileInspection.status = MobileInspectionStatus.FINISHED;
    //mobileInspection.stoppedUserId = this.authGuard.session.user.id;

    return mobileInspection;
  }

  quit() {
    this.deleteSession();
  }

  deleteSession() {
    this.deleteClicked = true;
    if (this.data.recording) {
      if (this.sessionId) {
        this.devicesService.deleteSession(this.sessionId)
          .subscribe(() => this.closeAndNotify(), () => this.closeAndNotify());
        this.stopInspectorSessionKeepAlivePoller();
        if (this.mobileSessionId) {
          this.devicesService.updateSession(this.getMobileInspectionUpdate())
            .subscribe({
              next: () => {
              }
            });
        }
      } else {
        if (this.mobileSessionId) {
          this.devicesService.updateSession(this.getMobileInspectionUpdate())
            .subscribe({
              next: () => {
              }
            });
          this.stopInspectorSessionKeepAlivePoller();
          this.mobileSessionId = null;
        }
        this.stopInspectorSessionKeepAlivePoller();
        this.closeAndNotify();
      }
      this.stopDriverSessionKeepAlivePoller();
    } else {
      if (this.sessionId) {
        this.devicesService.deleteSession(this.sessionId).subscribe((v) => {
          this.devicesService.stopMirroring(this.data.device)
            .subscribe(() => this.closeAndNotify(), () => this.closeAndNotify());
          this.stopInspectorSessionKeepAlivePoller();
          this.devicesService.updateSession(this.getMobileInspectionUpdate())
            .subscribe({
              next: () => {
              }
            });
        });
      } else {
        if (this.mobileSessionId) {
          this.devicesService.updateSession(this.getMobileInspectionUpdate())
            .subscribe({
              next: () => {
              }
            });
          this.stopInspectorSessionKeepAlivePoller();
          this.mobileSessionId = null;
        }
        this.devicesService.stopMirroring(this.data.device)
          .subscribe(() => this.closeAndNotify(), () => this.closeAndNotify());
      }
    }
    this.sessionId = null;
  }

  closeAndNotify() {
    this.dialogRef.close(this.getSaveElementsComponent()?.returnResponse);
    this.showNotification(NotificationType.Success, this.translate.instant("mobile_recorder.notification.closed"));
    if(this.getSaveElementsComponent())
      this.getSaveElementsComponent().returnResponse = undefined;
  }

  openSocketConnection() {
    let _this = this;
    this.getMirroringContainerComponent().createCanvas();
    let ws = new WebSocket('wss://local.testsigmaagent.com:8484/agent/mobile-frames?deviceUUID='
      + this.data.device.uniqueId);
    ws.binaryType = 'blob';
    ws.onmessage = (message: MessageEvent) => this._onWSMessage(message);
    ws.onclose = (closeEvent: CloseEvent) => {
      console.log("Received a close event on socket " + closeEvent.code + " - " + closeEvent.reason);
      _this.dialogRef.close();
    };
    ws.onopen = (openEvent: Event) => {
      console.log("Received a open even on socket");
    }
  };

  _onWSMessage(message: MessageEvent) {
    if (message.data !== "MIRRORING_STARTING") {
      let img = new Image();
      img.onload = () => this.getMirroringContainerComponent()._onImageLoad(img);
      img.src = message.data;
    } else {
      console.log("MIRRORING_STARTING message received...");
    }
  }

  renderCurrentScreenshot() {
    if (this.data.recording) {
      this.loadingActions = true;
      this.devicesService.getScreenshot(this.data.agent, this.sessionId).subscribe({
        next: (imageBase64URL: String) => {
          let img = new Image();
          img.onload = () => this.getMirroringContainerComponent()._onImageLoad(img);
          img.src = imageBase64URL + '';
        },
        error: (error) => {
          console.log('Error in get screenshot - ', error);
          this.showAPIError(NotificationType.Error, this.translate.instant('mobile_recorder.notification.session_expired'));
          this.loadingActions = false;
        }
      });
    } else if (this.sessionId) {
      this.getCurrentPageSource();
    }
  }

  getCurrentPageSource() {
    this.devicesService.getPageSource(this.sessionId, this.platform)
      .subscribe((mobileElement: MobileElement) => {
        this.currentPageSource = mobileElement;
        this.dataSource = [JSON.parse(this.JsonPipe.transform(mobileElement))];
        if (!this.getMirroringContainerComponent()?.actionType) {
          this.getMirroringContainerComponent().drawElements(mobileElement);
          if (this.getMirroringContainerComponent().viewType != "NATIVE_APP") {
            const viewType = this.getMirroringContainerComponent().viewType;
            this.getMirroringContainerComponent().viewType = "NATIVE_APP";
            this.getMirroringContainerComponent().switchViewMode(viewType);
          }
        }
        this.loadingActions = false;
        this.setTimer(1800);
      });
  }

  setTimer(timeLeft) {
    if (this.timeout)
      clearTimeout(this.timeout);
    this.timeout = setTimeout(() => {
      if (this.dialogRef.getState() != MatDialogState.OPEN)
        return;
      if(!this.dialog.openDialogs?.find(dialog => dialog.componentInstance instanceof SessionExpiredModalComponent)){
        const dialogRef =
          this.dialog
            .open(SessionExpiredModalComponent,
              {
                width: '500px',
                panelClass: 'send-key-dialog',
                data: {timeOut: timeLeft}
              });
        dialogRef.afterClosed().subscribe(result => {
          if (result) {
            this.quit();
          } else {
            this.setTimer(600);
          }
        });
      }
    }, (timeLeft * 1000));
  }

  attachActiveSessionTimer() {
    document.addEventListener("visibilitychange", () => {
      document.hidden ? this.setTimer(600) : this.setTimer(1800);
    });
  }

  public beforeAction() {
    this.setTimer(1800);
    this.loadingActions = true;
  }

  public handleActionFailure(error, messageKey: string) {
    let message;
    if (error['status'] == 500 && error['error']['error'].includes("A session is either terminated or not started")) {
      message = "mobile_recorder.notification.session_expired";
    } else if (error["error"]["error"]?.startsWith("Screen rotation cannot be changed")) {
      message = "mobile_recorder.notification.change_orientation_failed";
    } else {
      message = messageKey;
    }
    this.showAPIError(error, this.translate.instant(message))
    this.loadingActions = false
  }

  handleActionSuccess() {
    this.devicesService.getOrientation(this.sessionId).subscribe((res: ScreenOrientation) => {
      this.getMirroringContainerComponent().isLandscapeMode = res == ScreenOrientation.LANDSCAPE;
      let asyncRenderScreenshot = async () => {
        await this.synchronousDelay(500);
        this.renderCurrentScreenshot();
        this.renderCurrentScreenshot();
      }
      asyncRenderScreenshot();
    })
  }

  handleElementUpdate(err, isCreate) {
    if (!err) {
      this.showNotification(NotificationType.Success, this.translate.instant('element.notification.' + (isCreate ? 'save' : 'update') + '.success'));
      this.quit();
    } else {
      var failureMessage = (err == 'duplicate') ? 'element.message.name.duplicate' : 'element.notification.' + (isCreate ? 'save' : 'update') + '.failure';
      this.showNotification(NotificationType.Error, this.translate.instant(failureMessage));
    }
  }

  clearSelection(){
    this.getMirroringContainerComponent().clearSelection()
    this.element = null;
  }

  backToListView() {
    this.getSaveElementsComponent().backToListView();
  }

  initElement() {
    this.getSaveElementsComponent().inspectedElement = this.getMirroringContainerComponent().inspectedElement;
    this.getSaveElementsComponent().editedIndex = -1;
    this.getSaveElementsComponent().initElement();
  }

  toggleFullScreen() {
    if(!this.loading){
      this.fullScreenMode= (!this.fullScreenMode);
      this.renderCurrentScreenshot();
    }
  }

  public getSaveElementsComponent = () => this.mobileRecorderComponentInstance?.saveElementsComponent;

  public getMirroringContainerComponent =  () =>  this.mobileRecorderComponentInstance?.mirroringContainerComponent;
}
