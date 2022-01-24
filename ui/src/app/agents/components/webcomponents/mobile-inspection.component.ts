import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {JsonPipe} from "@angular/common";
import {MobileRecordingComponent} from "./mobile-recording.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MirroringData} from "../../models/mirroring-data.model";
import {DevicesService} from "../../services/devices.service";
import {ElementsContainerComponent} from "./elements-container.component";
import {ElementService} from "../../../shared/services/element.service";
import {MirroringContainerComponent} from "./mirroring-container.component";

@Component({
  selector: 'app-mobile-inspection',
  templateUrl: './mobile-inspection.component.html',
  providers: [JsonPipe],

})
export class MobileInspectionComponent extends MobileRecordingComponent implements OnInit{
  @ViewChild('mirroringContainerComponent') mirroringContainerComponent: MirroringContainerComponent;
  @ViewChild('saveElementsComponent',{static: false}) saveElementsComponent: ElementsContainerComponent;
  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    @Inject(MAT_DIALOG_DATA) public data: MirroringData,
    public JsonPipe: JsonPipe,
    public localDeviceService: DevicesService,
    public elementService: ElementService,
    public dialogRef: MatDialogRef<MobileRecordingComponent>,
    public dialog: MatDialog
  ) {
    super(
      authGuard, notificationsService, translate, toastrService,
      data,
      JsonPipe,
      localDeviceService,
      elementService,
      dialogRef,
      dialog)
  }

  //public cloudDeviceService: CloudDevicesService,

  ngOnInit(): void {
    super.ngOnInit();
    super.mobileRecorderComponentInstance = this;
  }

}
