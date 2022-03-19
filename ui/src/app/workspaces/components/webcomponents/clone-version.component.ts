import { Component, OnInit, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import * as moment from 'moment';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {WorkspaceVersionService} from "../../../shared/services/workspace-version.service";
import { NotificationType, NotificationsService } from 'angular2-notifications';
import {BaseComponent} from "../../../shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import { Router } from '@angular/router';

@Component({
  selector: 'app-clone-version',
  templateUrl: './clone-version.component.html'
})
export class CloneVersionComponent extends BaseComponent implements OnInit {
  public versionForm: any;
  public version: WorkspaceVersion = new WorkspaceVersion();
  private fullScreenDetails: boolean;
  public saving: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: {version: WorkspaceVersion},
    private dialogRef: MatDialogRef<CloneVersionComponent>,
    private workspaceVersionService: WorkspaceVersionService,
    private formBuilder: FormBuilder,
    private router: Router,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.fullScreenDetails = this.router.url.endsWith("/details");
    Object.assign(this.version, this.modalData.version);
    this.version.versionName = "Copy of (" + this.version.name + ")";
    this.versionForm = this.formBuilder.group({
      workspaceId: new FormControl(this.version.workspaceId, []),
      versionName: new FormControl(this.version.versionName, [Validators.required])
    });
  }

  clone() {
    if(this.versionForm.invalid)
      return;
    this.saving = true;
    this.workspaceVersionService.clone(this.version).subscribe(
      (res) => {
        this.saving = false;
        this.translate.get('message.common.created.success', {FieldName: 'Version'}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
          let url;
          url = ['/workspaces',this.version.workspace.id,'versions'];
          this.dialogRef.afterClosed().subscribe(()=>this.router.navigate(url));
          this.dialogRef.close();
        });
      },
      (exception) => {
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: 'Version'}).subscribe((res) => {
          this.showAPIError(exception, res,'Workspace Version');
        });
      }
    )
  }
}
