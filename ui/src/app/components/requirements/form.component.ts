import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {RequirementsService} from "../../services/requirements.service";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Requirement} from "../../models/requirement.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-requirements-form',
  templateUrl: './form.component.html',
  host: {'class': 'page-content-container'}
})
export class FormComponent extends BaseComponent implements OnInit {
  public requirementForm: FormGroup;
  public requirement: Requirement;
  public versionId: number;
  public version: WorkspaceVersion;
  formSubmitted = false;
  public saving = false;
  todayDate:Date = new Date();

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private requirementsService: RequirementsService,
    private versionService: WorkspaceVersionService,
    private router: Router,
    private formBuilder: FormBuilder) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.versionId = this.route.snapshot.parent.parent.params.versionId;
    this.route.snapshot.params = {...this.route.snapshot.params, ...{versionId: this.versionId}}
    this.versionService.show(this.versionId).subscribe(res => {
      this.version = res;
    })
    if (!this.route.snapshot.params.requirementId) {
      this.pushToParent(this.route, this.route.snapshot.parent.parent.params);
      this.requirement = new Requirement();
      this.requirement.workspaceVersionId = this.versionId;
      this.initiateForm();
    } else {
      this.pushToParent(this.route, this.route.snapshot.params);
      this.fetchRequirement(this.route.snapshot.params.requirementId);
    }
  }

  public update() {
    this.requirement.requirementDescription = this.requirementForm.controls.description.value;
    this.formSubmitted = true;
    if (this.requirementForm.invalid) return;
    this.saving = true;
    this.requirementsService.update(this.requirement).subscribe(
      (req: Requirement) => {
        this.saving = false;
        this.translate.get('message.common.update.success', {FieldName: "Requirement"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', 'requirements', req.id, 'details'])
      },
      (err) => {
        this.saving = false;
        this.translate.get('message.common.update.failure', {FieldName: "Requirement"})
          .subscribe(res => this.showAPIError(err, res))
      }
    );
  }

  public create() {
    this.requirement.requirementDescription = this.requirementForm.controls.description.value;
    this.formSubmitted = true;
    if (this.requirementForm.invalid) return;
    this.saving = true;
    this.requirementsService.create(this.requirement).subscribe(
      (req: Requirement) => {
        this.saving = false;
        this.translate.get('message.common.created.success', {FieldName: "Requirement"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', 'requirements', req.id, 'details'])
      },
      (err) => {
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: "Requirement"})
          .subscribe(res => this.showAPIError(err, res))
      }
    );
  }

  private fetchRequirement(requirementId: number) {
    this.requirementsService.show(requirementId).subscribe(res => {
      this.requirement = res;
      this.initiateForm();
    });
  }

  private initiateForm() {
    this.requirementForm = this.formBuilder.group({
      name: new FormControl(this.requirement.requirementName, [Validators.required,Validators.maxLength(125)]),
      description: new FormControl(this.requirement.requirementDescription)
    });
  }
}
