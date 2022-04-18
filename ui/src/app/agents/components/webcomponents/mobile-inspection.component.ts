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
import {SaveWorkWarningComponent} from "./save-work-warning.component";

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

  saveAndQuit(){
    if(this.saveElementsComponent.isEdit && this.saveElementsComponent.elementForm?.pristine &&
      (this.saveElementsComponent.element?.locatorValue==this.saveElementsComponent.fetchedElement?.locatorValue)){
      return;
    }
    if(this.saveElementsComponent.uiId){
      this.saveElementsComponent.updateElement(true);
    } else {
      if (this.isEdit) {
        this.saveElementsComponent.createElement(true);
      } else {
        if(!Boolean(this.saveElementsComponent?.element)) {
          this.saveElementsComponent.saveElements();
          setTimeout(()=> this.deleteSession(),1000);
        } else {
          this.saveElementsComponent.addToList(true);
          if (this.ElementForm.invalid) return;
          this.saveElementsComponent.saveElements();
          setTimeout(()=> this.deleteSession(),1000);
        }
      }
    }
  }

  get isMultipleRecorder():boolean{
    return !(this.saveElementsComponent?.uiId||this.saveElementsComponent?.isEdit);
  }

  get singleElementRecorderPristine():boolean{
    return (this.saveElementsComponent?.isEdit && (this.saveElementsComponent?.elementForm == undefined ||this.saveElementsComponent?.elementForm?.pristine && this.saveElementsComponent.element?.locatorValue==this.saveElementsComponent?.fetchedElement?.locatorValue));
  }

  get multipleRecorderIsPristine():boolean{
    return !Boolean(this.saveElementsComponent?.element?.locatorValue) && (this.saveElementsComponent.elements == undefined || (this.saveElementsComponent.elements.length == 0 && this.saveElementsComponent.editedIndex != -1 ) || (this.saveElementsComponent?.elements?.filter(element => !element.saved)?.length == 0 && this.saveElementsComponent.editedIndex == -1))
  }

  openUnsavedWorkWarning() {
    if(this.saveElementsComponent != undefined && this.isMultipleRecorder){
      if(this.multipleRecorderIsPristine){
        this.deleteSession();
        return;
      }
    } else if(this.saveElementsComponent == undefined || this.singleElementRecorderPristine){
      this.deleteSession();
      return;
    }
    const dialogRef = this.dialog.open(SaveWorkWarningComponent, {
      width: '450px',
      panelClass: ['matDialog', 'delete-confirm'],
      data: {number: (!this.isMultipleRecorder)?0:
          this.saveElementsComponent.elements?.filter(element => !element.saved && !element.errors)?.length},
    });
    dialogRef.afterClosed().subscribe(res =>{
      if(res==false){
        this.saveAndQuit();
      } else if(res==true){
        this.deleteSession();
      }
    })
  }

}
