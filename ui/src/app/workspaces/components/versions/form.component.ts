import {Component, OnInit} from '@angular/core';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {WorkspaceVersionService} from "../../../shared/services/workspace-version.service";
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from "../../../shared/components/base.component";
import * as moment from 'moment';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  host: {'style': 'flex:1', 'class': 'h-100'}
})
export class FormComponent extends BaseComponent implements OnInit {
  public versionForm: FormGroup;
  public version = new WorkspaceVersion();
  public submitted = false;
  public saving: boolean;
  public hasMultipleApps: Boolean;
  todayDate: Date = new Date();

  constructor(
    private route: ActivatedRoute,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    private versionService: WorkspaceVersionService,
    private formBuilder: FormBuilder) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
      this.route.parent.parent.params.subscribe((res) => {
      this.route.params.subscribe(versionIdParams => {
        this.version.id = versionIdParams.versionId;
        this.version.workspaceId = res.workspaceId;
        if (typeof this.version.id !== "undefined")
          this.fetchVersion();
        else {

          this.setForm();
        }
      })
    })
  }

  setForm() {
    this.versionForm = this.formBuilder.group({
      workspaceId: new FormControl(this.version.workspaceId, []),
      versionName: new FormControl(this.version.versionName, [Validators.required]),
      description: new FormControl(this.version.description, [])
    });
  }

  onSubmit() {
    this.submitted = true;
    if (this.versionForm.invalid) return;
    if (this.version.id)
      this.update();
    else
      this.create();
  }

  private create() {
    if (this.versionForm.invalid) return;
    this.version = new WorkspaceVersion().deserialize(this.versionForm.getRawValue());
    this.versionService.create(this.version).subscribe(
      res => {
        this.saving = false;
        this.translate.get("message.common.created.success", {FieldName: "Version"}).subscribe((msg: string) => {
          this.showNotification(NotificationType.Success, msg);
        });
        this.router.navigate(['/workspaces', res.workspace.id, 'versions', res.id], {relativeTo: this.route});
      },
      _err => {
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: "Version"})
          .subscribe(msg => this.showAPIError(_err, msg))
      }
    );
  }

  private update() {
    if (this.versionForm.invalid) return;
    this.saving = true;
    let versionId = this.version.id;
    this.version = new WorkspaceVersion().deserialize(this.versionForm.getRawValue());
    this.version.id = versionId;
    this.versionService.update(this.version).subscribe(
      res => {
        this.saving = false;
        this.translate.get("message.common.update.success", {FieldName: "Version"}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
        });
        this.router.navigate(['/workspaces', res.workspaceId, 'versions', res.id], {relativeTo: this.route});
      },
      _err => {
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: "Version"})
          .subscribe(msg => this.showAPIError(_err, msg))
      }
    );
  }

  private fetchVersion() {
    this.versionService.show(this.version.id).subscribe(res => {
      this.version = res;
      this.setForm();
    });
  }

}
