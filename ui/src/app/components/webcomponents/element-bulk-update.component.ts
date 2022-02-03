import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {ElementTagService} from "../../services/element-tag.service";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {ElementAddTagComponent} from "./element-add-tag.component";
import {BaseComponent} from "../../shared/components/base.component";
import {ElementService} from "../../shared/services/element.service";

@Component({
  selector: 'app-element-bulk-update',
  templateUrl: './element-bulk-update.component.html'
})
export class ElementBulkUpdateComponent extends BaseComponent implements OnInit {
  @ViewChild('selectField') selectField;
  public tag: any;
  public associatedTags = [];
  public tagList = [];
  public tagForm: FormGroup;
  public searchValue: String = '';
  public screenName: string;
  public saving: boolean;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { elementIds: number[], versionId: number},
    public dialogRef: MatDialogRef<ElementAddTagComponent>,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private elementTagService: ElementTagService,
    private elementService: ElementService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.getAllTags();
    this.tagForm = new FormGroup({
      chipControl: new FormControl(),
      searchControl: new FormControl()
    });
  }

  getAllTags() {
    this.elementTagService.findAll(undefined).subscribe(res => {
      for (let i = 0; i < res.length; i++) {
        this.tagList.push(res[i].name);
      }
    });
  }

  updateScreenNameAndTags() {
    this.saving = true;
    this.elementService.bulkUpdateScreenNameAndTag(this.data.elementIds, this.screenName, this.associatedTags)
      .subscribe(
        () => {
          this.translate.get("message.common.update.success", {FieldName: 'Elements'})
            .subscribe(res => this.showNotification(NotificationType.Success, res));
          this.dialogRef.close(true);
        },
        (err) => {
          this.saving = false;
          this.translate.get("message.common.update.failure", {FieldName: 'Elements'})
            .subscribe(res => this.showNotification(NotificationType.Error, res));
        }
      );
  }

  selected(event): void {
    this.associatedTags = event.value;
    this.tagForm.controls['chipControl'].patchValue(this.associatedTags);
  }

  removeFromList(tag: String) {
    this.associatedTags.splice(this.associatedTags.indexOf(tag), 1);
    this.tagForm.controls['chipControl'].patchValue(this.associatedTags);
  }

  closePanel() {
    this.searchValue = '';
    this.selectField.close();
  }

  createNewTag(value: string) {
    if (this.tagList.indexOf(value) == -1)
      this.tagList.push(value);
    if(this.associatedTags.indexOf(value) == -1) {
      this.associatedTags.push(value);
    }
    this.closePanel();
  }

}
