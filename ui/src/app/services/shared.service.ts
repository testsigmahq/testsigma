import {Injectable} from "@angular/core";
import {LinkedEntitiesModalComponent} from "../shared/components/webcomponents/linked-entities-modal.component";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";

@Injectable({
  providedIn: 'root'
})
export class SharedService{
  constructor(
    private translate:TranslateService,
    private matModal: MatDialog,
  ) {
  }

  public openLinkedTestStepsDialog(list) {
    this.translate.get("step_is_prerequisite_to_another_step").subscribe((res) => {
      this.matModal.open(LinkedEntitiesModalComponent, {
        width: '568px',
        height: 'auto',
        data: {
          description: res,
          linkedEntityList: list,
        },
        panelClass: ['mat-dialog', 'rds-none']
      });
    });
  }
}
