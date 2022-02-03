import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {LinkedEntitiesModalComponent} from "./linked-entities-modal.component";
import {TestDevice} from "../../../models/test-device.model";

@Component({
  selector: 'app-uploads-entities-modal',
  templateUrl: './uploads-entities-modal.component.html',
})
export class UploadEntitiesModalComponent extends LinkedEntitiesModalComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
    super(modalData);
  }

  ngOnInit(): void {
  }

  openLinkedEntity(id) {
    let entityUrl;
    if(this.modalData.linkedEntityList['cachedItems'][0] instanceof TestDevice)
      entityUrl = "/#/td/plans/"+ id +"/details";
    window.open(window.location.origin + entityUrl, "_blank");
  }
}
