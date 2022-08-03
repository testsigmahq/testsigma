import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
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
import {Router} from "@angular/router";

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
  @Input('formSubmitted') formSubmitted: boolean;
  @Input('tabPosition') tabPosition: Number;
  @Input('isNewUI') isNewUI: boolean;
  @Output('updateHeaderBtns') updateHeaderBtns = new EventEmitter<{tabPosition: Number, buttons: any[]}>();

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public router: Router) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
    this.invokeInitialBtnState();
  }

  get isInValid() {
    return this.testPlanForm?.controls['name']?.invalid;
  }

  next() {
    this.stepper.next();
  }

  invokeInitialBtnState() {
    this.updateHeaderBtns.emit({
      tabPosition: this.tabPosition,
      buttons: [
        {
          className: 'theme-btn-clear-default',
          content: this.translate.instant('btn.common.cancel'),
          clickHandler: ()=> {
            this.router.navigate(['/td', this.version?.id, 'plans']);
          }
        },
        {
          className: 'theme-btn-primary ml-15',
          content: this.translate.instant('pagination.next'),
          clickHandler: ()=> {
            (this.isInValid ? this.formSubmitted = true : this.next());
          }
        }
      ]
    });
  }
}
