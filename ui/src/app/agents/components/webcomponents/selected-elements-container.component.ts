import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MobileElementRect} from "../../models/mobile-element-rect.model";
import {MobileElement} from "../../models/mobile-element.model";
import {SendKeysRequest} from "../../models/send-keys-request.model";
import {SendKeyModalComponent} from "./send-key-modal.component";
import {MatDialog} from "@angular/material/dialog";
import {MobileRecordingComponent} from "./mobile-recording.component";
import {DevicesService} from "../../services/devices.service";

@Component({
  selector: 'app-selected-elements',
  templateUrl: './selected-elements-container.component.html'
})
export class SelectedElementsContainerComponent{
  @Input("inspectedElement") public inspectedElement: MobileElementRect;
  @Input("currentPageSource") public currentPageSource: MobileElement;
  @Input("isNativeAppEnabled") public isNativeAppEnabled: boolean;
  @Input("sessionId") public sessionId: String;
  @Input() isStepRecorder?: boolean;
  @Output() saveEnterStep = new EventEmitter<SendKeysRequest>();
  @Output() saveClearStep = new EventEmitter<MobileElement>();
  @Output() saveTapStep = new EventEmitter<MobileElement>();
  public bounds: any;
  @Input("devicesService")private devicesService: DevicesService ; //| CloudDevicesService;
  @Input()optimisingXpath: boolean;

  constructor(
    private dialog: MatDialog
  ) {
    // @ts-ignore
    const width = (this.inspectedElement?.mobileElement.x2) - (this.inspectedElement?.mobileElement.x1);
    // @ts-ignore
    const height = (this.inspectedElement?.mobileElement.y2) - (this.inspectedElement?.mobileElement.y1);
    this.bounds = "[ " + this.inspectedElement?.mobileElement.x1 + ", " + this.inspectedElement?.mobileElement.y1 + " ] "
      + "[ " + width + ", " + height + " ]";
  }

  ngOnChanges(){
    // @ts-ignore
    const width = (this.inspectedElement?.mobileElement.x2) - (this.inspectedElement?.mobileElement.x1);
    // @ts-ignore
    const height = (this.inspectedElement?.mobileElement.y2) - (this.inspectedElement?.mobileElement.y1);
    this.bounds = "[ " + this.inspectedElement?.mobileElement.x1 + ", " + this.inspectedElement?.mobileElement.y1 + " ] "
      + "[ " + width + ", " + height + " ]";
  }

  public tap(mobileElement: MobileElement) {
    this.beforeAction();
    if (mobileElement?.text?.length <4 && mobileElement?.text?.length>0)
      mobileElement.text = mobileElement.text +'-'+ this.getCurrentTimeStamp();
    this.devicesService.tapElement(this.sessionId, mobileElement)
      .subscribe({
        next: () => {
          if(this.isStepRecorder)
            this.saveTapStep.emit(mobileElement);
          this.handleActionSuccess();
          this.removeInspectionElements();
          this.clearSelection();
        },
        error: (err) => {
          this.handleActionFailure(err, "mobile_recorder.notification.tap.failure", "Tap","/#6--failed-to-tap-on-the-element")
          this.inspectedElement = null;
        }
      });
  }

  private sendkeys(sendKeysRequest: SendKeysRequest) {
    this.beforeAction(true);
    if (sendKeysRequest?.mobileElement?.text?.length < 4 && sendKeysRequest?.mobileElement?.text?.length>0)
      sendKeysRequest.mobileElement.text = sendKeysRequest?.mobileElement?.text +'-'+ this.getCurrentTimeStamp();
    this.devicesService.sendKeys(this.sessionId, sendKeysRequest)
      .subscribe({
        next: () => {
          if(this.isStepRecorder)
            this.saveEnterStep.emit(sendKeysRequest);
          this.handleActionSuccess()},
        error: (err) => this.handleActionFailure(err, "mobile_recorder.notification.send_keys.failure", "Enter", "/#9--failed-to-enter-data-into-the-element")
      });
  }

  public clear(mobileElement: MobileElement) {
    this.beforeAction();
    this.devicesService.clearElement(this.sessionId, mobileElement)
      .subscribe({
        next: () => {
          if(this.isStepRecorder)
            this.saveClearStep.emit(mobileElement);
          this.handleActionSuccess();
        },
        error: (err) => this.handleActionFailure(err, "mobile_recorder.notification.clear.failure","Clear", "/#5--failed-to-clear-the-elements-text")
      });

  }

  public sendKeyDialog(mobileElement: MobileElement) {
    this.setTimer(1800);
    SendKeyModalComponent.prototype.keys = '';
    const dialogRef = this.dialog.open(SendKeyModalComponent, {width: '500px',
      panelClass: ['mat-dialog', 'rds-none']});
    const sendKeysRequest = new SendKeysRequest();
    dialogRef.afterClosed().subscribe(result => {
      this.setTimer(1800);
      if (result) {
        sendKeysRequest.mobileElement = mobileElement;
        sendKeysRequest.keys = result;
        SendKeyModalComponent.prototype.keys = sendKeysRequest.keys;
        this.sendkeys(sendKeysRequest);
      }
    });
  }

  public enableTap(): boolean {
    return (!(this.inspectedElement.mobileElement.clickable
      || this.inspectedElement.mobileElement.type == 'android.widget.Button'
      || this.inspectedElement.mobileElement.type == 'android.widget.ImageButton'
      || this.inspectedElement.mobileElement.type == 'android.widget.Spinner'
      || this.inspectedElement.mobileElement.type == 'android.view.View'
      || this.inspectedElement.mobileElement.type == 'android.widget.TextView'
      || this.inspectedElement.mobileElement.type == 'XCUIElementTypeButton'
      || this.inspectedElement.mobileElement.type == 'XCUIElementTypeKey'
      || this.inspectedElement.mobileElement.type == 'android.widget.ImageView'
      || this.inspectedElement.mobileElement.type == 'textInputLayout'));
  }

  public textEditable(): boolean {
    return (!(this.inspectedElement.mobileElement.type == 'android.widget.EditText'
      || this.inspectedElement.mobileElement.type == 'android.widget.TextView'
      || this.inspectedElement.mobileElement.type == 'XCUIElementTypeTextField'
      || this.inspectedElement.mobileElement.type == 'XCUIElementTypeSecureTextField'
      || this.inspectedElement.mobileElement.type == 'input'));
  }

  private setTimer(number){
    let recorderDialog: MobileRecordingComponent = this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent).componentInstance;
    recorderDialog.setTimer(number)
  }

  private handleActionSuccess() {
    let recorderDialog: MobileRecordingComponent = this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent).componentInstance;
    recorderDialog.handleActionSuccess();
  }

  private handleActionFailure(error, messageKey: string, actionType?: string, guideLink?:string) {
    let recorderDialog: MobileRecordingComponent = this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent).componentInstance;
    recorderDialog.handleActionFailure(error, messageKey, actionType, guideLink);
  }

  private clearSelection() {
    let recorderDialog: MobileRecordingComponent = this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent).componentInstance;
    recorderDialog.clearSelection();
  }

  private removeInspectionElements() {
    let recorderDialog: MobileRecordingComponent = this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent).componentInstance;
    recorderDialog.getMirroringContainerComponent().removeInspectionElements();
  }

  private beforeAction(sendKeys?: boolean) {
    let recorderDialog: MobileRecordingComponent = this.dialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent).componentInstance;
    if(Boolean(sendKeys)) {
      recorderDialog.loadingActions = true;
      recorderDialog.viewType = "NATIVE_APP";
    } else
      recorderDialog.beforeAction();
  }

  private getCurrentTimeStamp() {
    var oDate = new Date();
    return ""+oDate.getDate()+oDate.getMonth()+oDate.getFullYear()+oDate.getHours()+oDate.getMinutes()+oDate.getSeconds();
  }
}
