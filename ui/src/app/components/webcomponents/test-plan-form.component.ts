import { Component, OnInit, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatHorizontalStepper } from '@angular/material/stepper';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {expand, fade} from "../../shared/animations/animations";
import {TestPlan} from "../../models/test-plan.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import { NotificationsService } from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-plan-form',
  templateUrl: './test-plan-form.component.html',
  styles: [
  ],
  animations: [fade, expand],
})
export class TestPlanFormComponent extends BaseComponent implements OnInit {
  @Input('formGroup') testPlanForm : FormGroup;
  @Input('stepper') stepper: MatHorizontalStepper;
  @Input('version') version: WorkspaceVersion;
  @Input('testPlan') testPlan: TestPlan;
  public showDescription: Boolean;
  public formSubmitted: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
  }

  get isInValid() {
    return this.testPlanForm?.controls['name']?.invalid;
  }

  next() {
    this.stepper.next();
  }
}
