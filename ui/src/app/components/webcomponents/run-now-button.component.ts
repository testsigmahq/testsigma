import {Component, EventEmitter, Input, OnInit, Optional, Output} from '@angular/core';
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
import {TestDeviceService} from "../../services/test-device.service";
import {TestDevice} from "../../models/test-device.model";
import {AgentService} from "../../agents/services/agent.service";

@Component({
  selector: 'app-run-now-button',
  template: `
    <button
      class="btn pr-6 text-nowrap" id="run"
      [disabled]="inTransit"
      [class.text-t-secondary]="!displayText"
      [class.icon-btn]="!displayText"
      [class.theme-btn-primary]="displayText && displayText!= ('btn.common.run' | translate) && !testPlanResult?.isQueued && !usageDetails"
      [class.theme-btn-clear-default]="displayText== ('btn.common.run' | translate) && !testPlanResult?.isQueued"
      [class.btn-delete]="testPlanResult?.isQueued || usageDetails"
      (click)="startOrStop();$event.stopImmediatePropagation();$event.stopPropagation();$event.preventDefault()"
      [matTooltip]="displayText== ('btn.common.run' | translate)  ? '' : (testPlanResult?.isQueued ? 'hint.message.common.stop' : 'runs.list_view.run_now') | translate">
      <i
        [class.fa-play]="!testPlanResult?.isQueued && ((displayText && displayText!= ('btn.common.run' | translate)) || !displayText) && !usageDetails"
        [class.fa-play-circle]="!testPlanResult?.isQueued && displayText== ('btn.common.run' | translate)"
        [class.text-white]="testPlanResult?.isQueued"
        [class.mr-n7]="testPlanResult?.isQueued"
        [class.pr-10]="testPlanResult?.isQueued"
        [class.fa-stop]="testPlanResult?.isQueued || usageDetails"></i>
      <span class="px-5" [translate]="displayText" *ngIf="displayText && !testPlanResult?.isQueued"></span>
      <span class="px-5" [translate]="'message.common.stop'" *ngIf="displayText && testPlanResult?.isQueued"></span>
    </button>
  `,
  styles: []
})
export class RunNowButtonComponent extends BaseComponent implements OnInit {
  @Input('displayText') displayText: string;
  @Input('testPlan') testPlan: TestPlan;
  @Output('onStart') onStart = new EventEmitter<void>();
  @Output('onStop') onStop = new EventEmitter<void>();
  @Optional() @Input('usageDetails') usageDetails:boolean = false;

  @Input('testPlanResult')
  public testPlanResult: TestPlanResult;
  public inTransit: boolean;

  

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public agentService: AgentService,
    private testDeviceService: TestDeviceService,
    private testPlanResultService: TestPlanResultService,) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.testPlanResult =(this.testPlanResult) ? this.testPlanResult : this.testPlan.lastRun;
  }

  startOrStop() {
    if(this.testPlanResult?.isQueued)
      this.stop();
    else
      this.start();
  }

  stop() {
    this.inTransit=true;
    this.testPlanResult.result = ResultConstant.STOPPED;
    this.testPlanResult.status = StatusConstant.STATUS_COMPLETED;


    this.testPlanResultService.update(this.testPlanResult).subscribe(() => {
      this.inTransit=false;
      this.translate.get("execution.stopped.success").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.onStop?.emit();
      })
    })
  }

  start() {
    if (this.testPlan.isHybrid) {
      this.testDeviceService.findAll("disable:false,testPlanId:" + this.testPlan.id, undefined).subscribe(res => {
        let testDevice: TestDevice = res.content[0];
        this.agentService.find(testDevice.agentId).subscribe(res => {
          if (!res.isOnline() || res.isOutOfSync()) {
            this.showNotification(NotificationType.Error, "None of the Machine are active, Please activate one machine to continue");
          }else {
            this.startExecution();
          }
        })
      });
    }else{
      this.startExecution();
    }
  }

  startExecution(){
    this.inTransit = true;
    let testPlanResult = new TestPlanResult();
    testPlanResult.testPlanId = this.testPlan.id;
    this.testPlanResultService.create(testPlanResult).subscribe((testPlanResult) => {
      this.inTransit = false;
      this.translate.get("execution.initiate.success").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.testPlanResult = testPlanResult;
        this.onStart?.emit();
      })
    }, error => {
      this.inTransit = false;
      this.showNotification(NotificationType.Error, error);
    })
  }

}
