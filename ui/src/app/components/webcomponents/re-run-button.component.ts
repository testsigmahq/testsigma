import { Component, OnInit, Input } from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import { NotificationsService, NotificationType } from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {ReRunType} from "../../enums/re-run-type.enum";
import { Router } from '@angular/router';

@Component({
  selector: 'app-re-run-button',
  template: `
    <div [class.mouse-over]="!inTransit"
      class="dropdown section-title d-inline-block mx-10">
      <button class="border-rds-right-1 btn btn-primary">
        <span [translate]="inTransit ? 'btn.common.please_wait' : 'btn.common.re_run'"></span>
      </button>
      <button class="border-rds-left-1 btn btn-primary px-4" style="margin-left: 1px">
        <i class="fa-caret-down mx-0 fz-12" style="margin-left: 0 !important;margin-right: 0 !important;"></i>
      </button>
      <div
        class="dropdown-menu"
        style="box-shadow: none;margin-top: -6px;padding-top: .5rem;background:
           transparent;left: -110px;">
        <ul
          class="bg-white border-rds-4 ng-scope p-15 shadow-all2-b4">
          <li
            class="border-rds-10 btn rb-medium grey-on-hover text-dark ml-0">
            <a
              (click)="reRun(reRunTypes.ONLY_FAILED_TESTS)"
              class="text-dark text-decoration-none" [translate]="'re_run.ONLY_FAILED_TESTS'"></a>
          </li>
          <li
            class="border-rds-10 btn rb-medium grey-on-hover text-dark ml-0">
            <a
              (click)="reRun(reRunTypes.ALL_TESTS)"
              class="text-dark text-decoration-none" [translate]="'re_run.ALL_TESTS'"></a>
          </li>
        </ul>
      </div>
    </div>
  `
})
export class ReRunButtonComponent extends BaseComponent implements OnInit {
  @Input('executionResult') parentExecutionResult: TestPlanResult;

  public executionResult: TestPlanResult;
  public inTransit: boolean;
  public reRunTypes = ReRunType;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private executionResultService: TestPlanResultService,
    private router: Router) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
  }

  reRun(reRunType: ReRunType) {
    this.inTransit = true;
    let executionResult = new TestPlanResult();
    executionResult.parentExecutionResultId = this.parentExecutionResult.id;
    executionResult.testPlanId = this.parentExecutionResult.testPlanId;
    executionResult.isReRun = true;
    executionResult.reRunType = reRunType;
    this.executionResultService.create(executionResult).subscribe((result: TestPlanResult) => {
      this.inTransit = false;
      this.translate.get("re_run.initiate.success").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.router.navigate(['/td', 'runs', result.id]);
      })
    }, error => {
      this.inTransit = false;
      this.showNotification(NotificationType.Error, error);
    })
  }

}
