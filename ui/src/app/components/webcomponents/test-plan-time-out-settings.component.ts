import {Component, Input, OnInit} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {FormControl, FormGroup} from '@angular/forms';
import {Screenshot} from "../../enums/screenshot.enum";
import {Environment} from "../../models/environment.model";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-plan-time-out-settings',
  templateUrl: './test-plan-time-out-settings.component.html',
  styles: []
})
export class TestPlanTimeOutSettingsComponent extends BaseComponent implements OnInit {
  @Input('formGroup') testPlanFormGroup: FormGroup;
  @Input('version') version: WorkspaceVersion;
  @Input('environment') environment?: Environment;
  @Input('environmentId') environmentId: number;

  public panelOpenState: Boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get screenShotTypes() {
    return Object.keys(Screenshot);
  }

  ngOnInit(): void {
  }

  get isRest() {
    return this?.version?.workspace?.isRest
  }

  get isMobileNative() {
    return this?.version?.workspace?.isMobileNative
  }

  get visualTestingEnabledControl(): FormControl {
    return <FormControl>this.testPlanFormGroup.controls['visualTestingEnabled'];
  }

  get retrySessionCreationControl(): FormControl {
    return <FormControl>this.testPlanFormGroup.controls['retrySessionCreation'];
  }

  setScreenshot() {
    this.testPlanFormGroup.patchValue({'screenshot':
        this.testPlanFormGroup.controls['visualTestingEnabled'].value ? Screenshot.ALL_TYPES : this.testPlanFormGroup.controls['screenshot'].value})
  }

  setSessionCreationTimeout() {
    this.testPlanFormGroup.patchValue({'retrySessionCreationTimeout':
        this.testPlanFormGroup.controls['retrySessionCreation'].value ? 30 : null})
  }

  setEnvironmentId($event: any) {
    if($event) {
      this.environment = $event;
      this.environmentId = $event.id;
    }
    this.testPlanFormGroup.patchValue({environmentId: $event?.id})
  }
}
