import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {ResultConstant} from "../../enums/result-constant.enum";
import {StatusConstant} from "../../enums/status-constant.enum";
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-run-now-button',
  template: `
    <button
      class="btn pr-6 text-nowrap" id="run"
      [disabled]="inTransit"
      [class.text-t-secondary]="!displayText"
      [class.icon-btn]="!displayText"
      [class.theme-btn-primary]="displayText && displayText!='Run' && !executionResult?.isQueued"
      [class.theme-btn-clear-default]="displayText=='Run' && !executionResult?.isQueued"
      [class.btn-delete]="executionResult?.isQueued"
      (click)="startOrStop();$event.stopImmediatePropagation();$event.stopPropagation();$event.preventDefault()"
      [matTooltip]="displayText=='Run' ? '' : (executionResult?.isQueued ? 'hint.message.common.stop' : 'runs.list_view.run_now') | translate">
      <i
        [class.fa-play]="!executionResult?.isQueued && ((displayText && displayText!='Run') || !displayText)"
        [class.fa-play-circle]="!executionResult?.isQueued && displayText=='Run'"
        [class.text-white]="executionResult?.isQueued"
        [class.mr-n7]="executionResult?.isQueued"
        [class.pr-10]="executionResult?.isQueued"
        [class.fa-stop]="executionResult?.isQueued"></i>
      <span class="px-5" [translate]="displayText" *ngIf="displayText && !executionResult?.isQueued"></span>
      <span class="px-5" [translate]="'message.common.stop'"  *ngIf="displayText && executionResult?.isQueued"></span>
    </button>
  `,
  styles: []
})
export class RunNowButtonComponent extends BaseComponent implements OnInit {
  @Input('displayText') displayText: string;
  @Input('execution') execution: TestPlan;
  @Output('onStart') onStart = new EventEmitter<void>();
  @Output('onStop') onStop = new EventEmitter<void>();

  @Input('executionResult')
  public executionResult: TestPlanResult;
  public inTransit: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private executionResultService: TestPlanResultService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.executionResult =(this.executionResult) ? this.executionResult : this.execution?.lastRun;
  }

  startOrStop() {
    if(this.executionResult?.isQueued)
      this.stop();
    else
      this.start();
  }

  stop() {
    this.inTransit=true;
    this.executionResult.result = ResultConstant.STOPPED;
    this.executionResult.status = StatusConstant.STATUS_COMPLETED;
    this.executionResultService.update(this.executionResult).subscribe(() => {
      this.inTransit=false;
      this.translate.get("execution.stopped.success").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.onStop?.emit();
      })
    })
  }

  start() {
    this.inTransit = true;
    let executionResult = new TestPlanResult();
    executionResult.testPlanId = this.execution.id;
    this.executionResultService.create(executionResult).subscribe((executionResult) => {
      this.inTransit = false;
      this.translate.get("execution.initiate.success").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.executionResult = executionResult;
        this.onStart?.emit();
      })
    }, error => {
      this.inTransit = false;
      this.showNotification(NotificationType.Error, error);
    })
  }

}
