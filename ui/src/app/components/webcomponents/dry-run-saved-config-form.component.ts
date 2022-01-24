import {Component, Inject, OnInit, ElementRef, ViewChild} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {AdhocRunConfigurationService} from "../../services/adhoc-run-configuration.service";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AdhocRunConfiguration} from "../../models/adhoc-run-configuration.model";

@Component({
  selector: 'app-save-configuration-form',
  template: `
    <div class="mat-dialog-header border-0">
      <div
        class="ts-col-90 d-flex fz-15 rb-medium"
        [translate]="'save_configuration.form_title'">
      </div>
      <button
        class="theme-overlay-close"
        type="button"
        [matTooltip]="'hint.message.common.close' | translate"
        mat-dialog-close>
      </button>
    </div>

    <form
      class="ts-form rb-regular"
      style="height: calc(100% - 62px)"
      (keydown.enter)="false"
      novalidate="novalidate"
      [formGroup]="form"
      name="testCaseCloneForm">
      <div class="modal-body">
        <div class="form-group pb-10">
          <div class="p-0 field">
            <input
              #nameInput
              type="text"
              id="name"
              name="name"
              placeholder=" "
              class="form-control"
              [(ngModel)]="configuration.name"
              [formControlName]="['name']"/>
            <label
              [translate]="'message.common.label.name'"
              for="name" class="required"></label>
          </div>
        </div>
        <div
          [textContent]="'save_configuration.save_note' | translate : {workspaceType: configuration.workspaceType}"
          class="my-10 border-rds-4 note-info" *ngIf="!configuration?.id"></div>
      </div>
      <div class="text-right py-10 pr-20">
        <button
          class="theme-btn-clear-default"
          mat-dialog-close
          [translate]="'btn.common.close'">
        </button>
        <button
          [disabled]="!form.valid"
          class="theme-btn-primary"
          type="submit"
          (click)="createSaveConfiguration()"
          [translate]="'btn.common.save'">
        </button>
      </div>
    </form>

  `,
  styles: []
})
export class DryRunSavedConfigFormComponent extends BaseComponent implements OnInit {
  public form: FormGroup;
  public configuration: AdhocRunConfiguration;
  @ViewChild('nameInput') nameInput : ElementRef;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private dryRunSavedConfigurationService: AdhocRunConfigurationService,
    private dialogRef: MatDialogRef<DryRunSavedConfigFormComponent>,
    @Inject(MAT_DIALOG_DATA) public options: {
      configuration: AdhocRunConfiguration
    }) {
    super(authGuard, notificationsService, translate, toastrService);
    this.configuration = this.options.configuration;
  }

  ngOnInit(): void {
    this.form = new FormGroup({
      name: new FormControl(this.configuration.name, [Validators.required, Validators.minLength(4)]),
    })
    this.focusInput();
  }

  focusInput() {
    if(this.nameInput?.nativeElement)
      this.nameInput.nativeElement.focus()
    else
      setTimeout(()=> this.focusInput(), 100);
  }

  createSaveConfiguration() {
    this.configuration.id = null;
    if (this.form.valid)
      this.dryRunSavedConfigurationService.create(this.configuration).subscribe((configuration) => {
          this.translate.get('message.common.created.success', {FieldName: 'Favorite Ad-hoc Run Config'}).subscribe((res) => {
            this.showNotification(NotificationType.Success, res);
            this.dialogRef.close(configuration);
          })
        },
        error => {
          this.translate.get('message.common.created.failure', {FieldName: 'Favorite Ad-hoc Run Config'}).subscribe((res) => {
            this.showAPIError(error, res);
          })
        })
  }

}
