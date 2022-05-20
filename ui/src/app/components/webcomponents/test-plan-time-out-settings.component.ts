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
import {StorageConfigService} from "../../services/storage-config.service";
import {StorageType} from "../../enums/storage-type.enum";

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
  public disableVisualTesting: Boolean =false;

  public panelOpenState: Boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public storageConfigService: StorageConfigService,
    public toastrService: ToastrService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get screenShotTypes() {
    return Object.keys(Screenshot);
  }

  ngOnInit(): void {
    this.setScreenshot();
    this.storageConfigService.find().subscribe(res => {
      this.disableVisualTesting =  res.storageType == StorageType.ON_PREMISE
    })
  }

  get isRest() {
    return this?.version?.workspace?.isRest
  }

  get isMobileNative() {
    return this?.version?.workspace?.isMobileNative
  }

  get retrySessionCreationControl(): FormControl {
    return <FormControl>this.testPlanFormGroup.controls['retrySessionCreation'];
  }

  setScreenshot() {
    this.testPlanFormGroup.patchValue({'screenshot': Screenshot.ALL_TYPES})
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
