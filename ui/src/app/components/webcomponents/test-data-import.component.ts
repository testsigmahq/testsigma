import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {collapse, fade} from "../../shared/animations/animations";
import {TestDataService} from "../../services/test-data.service";
import {FormControl, Validators} from '@angular/forms';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-test-data-import',
  animations: [collapse, fade],
  template: `
    <div *ngIf="!importing">
    <div class="mat-dialog-header border-0">
      <div
        class="ts-col-90 d-flex fz-15 rb-medium"
        [translate]="'ui_identifiers.import.title'">
      </div>
      <button
        class="close"
        [matTooltip]="'hint.message.common.close' | translate"
        mat-dialog-close>
      </button>
    </div>
    <form class="ts-form px-25 pb-20" name="uiIdentifierImportForm" #uiIdentifierImportForm="ngForm">

      <div class="py-10">
        <label class="theme-btn-clear-default overflow-x-hidden" *ngIf="!uploadedFileObject?.name" @fade>
          <i class="fa-pin pr-5"></i> Choose File
          <input class="d-none" type="file" name="file" required
                 #file="ngModel"
                 accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                 [(ngModel)]="importedFileName"
                 (change)="uploadedFile($event);">
        </label>
        <div class="overflow-x-hidden mt-10 p-16 pt-0 border-rds-2 f-medium d-inline-flex align-items-center
                    bg-grey-x-light mw-100 w-100"
             *ngIf="uploadedFileObject?.name" @fade>
          <span [textContent]="[uploadedFileObject?.name]" class="pr-10 text-truncate"></span>
          <i class="fa-trash-thin pointer ml-auto" (click)="removeUpload()"></i>
        </div>
      </div>
      <div *ngIf="uploadedFileObject?.name" class="overflow-x-hidden pt-20" @collapse>
        <div class="form-group">
          <input id="name" class="form-control" type="text" [(ngModel)]="name" [formControl]="formControl"/>
          <label class="control-label required" for="name"
                 [translate]="'test_data_profiles.sort_by.testDataName'"></label>
          <div class="error left"
               *ngIf="formControl?.errors?.minlength"
               [textContent]="'form.validation.common.min_length' | translate: {FieldName:('test_data_profiles.sort_by.testDataName'|translate) , min:'4'}"></div>
          <div class="error left"
               *ngIf="formControl?.errors?.maxlength"
               [textContent]="'form.validation.common.max_length' | translate: {FieldName:('test_data_profiles.sort_by.testDataName'|translate) , max:'256'}"></div>
          <div class="error left" *ngIf="formControl?.errors?.required && isSubmited" [textContent]="'form.validation.common.required'| translate:{ FieldName:('test_data_profiles.sort_by.testDataName'|translate) }"></div>
        </div>
      </div>
      <div *ngIf="uploadedFileObject?.name" class="overflow-x-hidden pt-20" @collapse>
        <span class="rb-medium" [translate]="'import.label.how_do_we_handle'"></span>
        <div class="d-flex py-20">
          <div class="d-flex align-items-center">
            <input
              type="radio"
              id="over_ride"
              name="over_ride"
              [(ngModel)]="isReplace" [value]="true"/>
            <label
              for="over_ride"
              class="pl-5 pointer"
              [translate]="'import.label.overwrite'"></label>
          </div>
          <div class="d-flex align-items-center pr-30 pl-30">
            <input
              type="radio"
              id="ignore"
              name="ignore"
              [(ngModel)]="isReplace" [value]="false"/>
            <label
              for="ignore"
              class="pl-5 pointer"
              [translate]="'import.label.ignore'"></label>
          </div>
        </div>
      </div>
    </form>
    <div class="border-lightGray-t-1 d-flex py-15 px-25">
      <a class="text-link my-auto" href="https://s3.amazonaws.com/static.testsigma.com/angular2/import_samples/Test_Data_Sample_Format.xlsx" target="_blank"
         download="Test_Data_Sample_Format.xlsx">
        <i class="fa-import"></i>
        <span class="pl-5" [translate]="'btn.common.download.link'"></span>
      </a>
      <div class="ml-auto">
        <button class="btn btn-clear" [translate]="'btn.common.cancel'" mat-dialog-close></button>
        <button class="btn btn-primary"
                [translate]="'btn.common.import'"
                [disabled]="uiIdentifierImportForm.invalid || formControl.invalid || hasDuplicateColumns"
                (click)="importFile()"></button>
      </div>
    </div>
    </div>

    <div
      class="theme-overlay-container" *ngIf="importing">
      <div class="ts-col-100 theme-overlay-header"></div>
      <div
        class="theme-overlay-content ts-col-100">
        <app-placeholder-loader
          class="ts-form align-items-center mt-20"
          [isLogoLoader]="true"
          [displayText]="('imports.form.progress' | translate)"
        ></app-placeholder-loader>
      </div>
      <div class="ts-col-100 theme-overlay-footer"></div>
    </div>
  `
})

export class TestDataImportComponent extends BaseComponent {
  public importedFileName: String;
  public uploadedFileObject;
  public fields: { name: String, isSelected: boolean , isDuplicate: boolean}[] = [];
  public name: String;
  public formControl: FormControl;
  private encryptedFieldNames: String[] = [];
  public hasDuplicateColumns: boolean;
  public duplicateColumns: string;
  public importing: boolean = false;
  public isReplace: Boolean = true;
  public isSubmited:boolean = false;


  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { versionId: number },
    public dialogRef: MatDialogRef<TestDataImportComponent>,
    private testDataService: TestDataService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService) {
    super(authGuard, notificationsService, translate);
    this.formControl = new FormControl(this.name, [
      Validators.required,
      Validators.minLength(4),
      Validators.maxLength(256)
    ]);
  }

  importFieldNames() {
    this.testDataService.importFieldNames(this.uploadedFileObject)
      .subscribe(res =>{
        this.setDuplicateColumns(res);
        res.forEach(name => {
          if (name.toLowerCase() != 'name' && name.toLowerCase() != 'description' && name.toLowerCase() != 'expectedtofail' || this.hasDuplicateColumns) {
            this.fields.push({name: name, isSelected: false, isDuplicate: this.duplicateColumns.indexOf(name) > -1 });
          }
        });
      });
  }

  uploadedFile(event) {
    this.encryptedFieldNames = [];
    this.uploadedFileObject = event.target.files ? event.target.files[0] : null;
    this.importFieldNames();
  }

  importFile() {
    this.importing = true;
    this.isSubmited = true;
    this.fields.forEach((field) => {
      if (field.isSelected)
        this.encryptedFieldNames.push(field.name);
    });
    this.testDataService.importAsync(this.uploadedFileObject, this.name, this.encryptedFieldNames, this.isReplace, this.data.versionId)
      .subscribe(
        (res) => this.dialogRef.close(true),
        (err) => {
          this.importing = false;
          this.translate.get('message.common.import.failure', {FieldName: "Test Data Profile"})
            .subscribe(msg => { this.showAPIError(err, msg); });
        });
  }

  removeUpload() {
    this.uploadedFileObject=null;
    this.fields=[]
  }

  private setDuplicateColumns(res) {
    let duplicateColumns = this.findDuplicates(res);
    this.hasDuplicateColumns = duplicateColumns.length > 0;
    this.duplicateColumns = "";
    duplicateColumns.forEach( (columnName, i) => this.duplicateColumns += ('"' + columnName + '"' +( i < (duplicateColumns.length-1) ?', ' : '')))
  }

  private findDuplicates = (arr) => {
    let sorted_arr = arr.slice().sort();
    let results = [];
    for (let i = 0; i < sorted_arr.length - 1; i++) {
      if (sorted_arr[i + 1] == sorted_arr[i]) {
        results.push(sorted_arr[i]);
      }
    }
    return results;
  }
}
