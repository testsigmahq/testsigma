import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {ElementTagService} from "../../services/element-tag.service";
import {FormControl, FormGroup} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from "../../shared/components/base.component";
import {NotificationsService, NotificationType} from 'angular2-notifications';


@Component({
  selector: 'element-add-label',
  templateUrl: './element-add-tag.component.html'
})

export class ElementAddTagComponent extends BaseComponent implements OnInit {
  @ViewChild('selectField') selectField;
  public tag: any;
  public associatedTags = [];
  public tagList = [];
  public tagForm: FormGroup;
  public searchValue: String = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { elementId: number, versionId: number },
    public dialogRef: MatDialogRef<ElementAddTagComponent>,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private elementTagService: ElementTagService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.getAllTags();
    this.getSelectedTags();
    this.tagForm = new FormGroup({
      chipControl: new FormControl(),
      searchControl: new FormControl()
    });
  }

  getSelectedTags() {
    this.elementTagService.find(this.data.elementId).subscribe(res => {
      for (let i = 0; i < res.length; i++) {
        this.associatedTags.push(res[i].name);
      }
      this.tagForm.controls['chipControl'].patchValue(this.associatedTags);
    });
  }

  getAllTags() {
    this.elementTagService.findAll(undefined).subscribe(res => {
      for (let i = 0; i < res.length; i++) {
        this.tagList.push(res[i].name);
      }
    });
  }

  updateTags() {
    this.elementTagService.update(this.data.elementId, this.associatedTags)
      .subscribe(
        () => {
          this.translate.get("message.common.update.success", {FieldName: 'labels'})
            .subscribe(res => this.showNotification(NotificationType.Success, res));
          this.dialogRef.close();
        },
        (err) => {
          this.translate.get("message.common.update.failure", {FieldName: 'labels'})
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
