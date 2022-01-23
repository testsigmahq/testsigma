import {Component, OnInit} from '@angular/core';
import {AuthenticationGuard} from '../../shared/guards/authentication.guard';
import {Observable, of} from 'rxjs';
import {ResultBase} from '../../models/result-base.model';
import {MobileInspectionService} from '../../shared/services/mobile-inspection-service';
import {MobileInspectionStatus} from '../../enums/mobile-inspection-status.enum';
import {MobileInspection} from '../../models/mobile-inspection.model';
import {TestPlanLabType} from '../../enums/test-plan-lab-type.enum';
import {DevicesService} from '../../agents/services/devices.service';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {StatusConstant} from "../../enums/status-constant.enum";
import * as moment from 'moment';
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {BaseComponent} from "../../shared/components/base.component";

@Component({
  selector: 'app-usage-details',
  templateUrl: './usage-details.component.html',
  styles: []
})
export class UsageDetailsComponent extends BaseComponent implements OnInit {
  public runsList: Observable<ResultBase[]>;
  public runsArray: ResultBase[] = [];
  public tempRunsArray: ResultBase[] = [];
  public inspectionsList: Observable<MobileInspection[]>;
  public inspectionsArray: MobileInspection[] = [];
  public isFetching: boolean;
  public runsQuery: String;
  public userIds: Number[] = [];

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private executionResultService: TestPlanResultService,
    private mobileInspectionService: MobileInspectionService,
    private devicesService: DevicesService) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  get today() {
    return moment();
  }

  //local mobile
  get localMobileInspectionRunningCount() {
    return this.inspectionsArray?.filter(value => value.labType === TestPlanLabType.Hybrid)?.length || 0;
  }

  //exe cloud progres
  get additionalParallelRunsOnCloudExecution() {
    let dryCount = 0;
    this.runsArray?.forEach((er: TestPlanResult) => {
      if ((er.status == StatusConstant.STATUS_PRE_FLIGHT || er.status == StatusConstant.STATUS_IN_PROGRESS) && !er.testPlan?.isHybrid) {
        dryCount = dryCount + er.totalRunningCount;
      }
    });
    return dryCount;
  }

  //exe local progress
  get additionalParallelRunsOnLocalExecution() {
    let dryCount = 0;
    this.runsArray?.forEach((er: TestPlanResult) => {
      if ((er.status == StatusConstant.STATUS_PRE_FLIGHT || er.status == StatusConstant.STATUS_IN_PROGRESS) && er.testPlan?.isHybrid) {
        dryCount = dryCount + er.totalRunningCount;
      }
    });
    return dryCount;
  }


  ngOnInit(): void {
    this.isFetching = true;
    this.refresh();
  }

  refresh() {
    this.runsList = new Observable<ResultBase[]>();
    this.runsArray = [];
    this.inspectionsList = new Observable<MobileInspection[]>();
    this.inspectionsArray = [];
    this.executionResultService.getRunningCounts().subscribe(res => {
      this.runsArray.unshift(...res.content);
      this.runsList = of(this.runsArray);
      this.fetchMobileInspections();
    })
  }

  showNotification(type: NotificationType, message) {
    const temp = {
      type: type,
      title: status,
      content: message,
      timeOut: 3000,
      position: ["bottom", "left"],
      showProgressBar: true,
      pauseOnHover: true,
      clickToClose: true,
      animate: 'fromLeft'
    };
    this.notificationsService.create(temp.title, temp.content, temp.type, temp);
  }

  stopMobileInspection(inspectionId: number) {
    let mobileInspection = new MobileInspection();
    mobileInspection.id = inspectionId;
    mobileInspection.status = MobileInspectionStatus.FINISHED;
    this.devicesService.updateSession(mobileInspection)
      .subscribe({
        next: () => {
          this.translate.get("usage_details.mobile_inspections.stop.success").subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
            this.refresh();
          });
        }, error: (error: any) => {
          this.translate.get("usage_details.mobile_inspections.stop.failure").subscribe((res: string) => {
            this.showNotification(NotificationType.Error, res);
            this.refresh();
          });
        }
      });
  }

  isDry(executionResult: TestPlanResult) {
    return !!executionResult.dryTestPlan;
  }

  private fetchMobileInspections() {
    this.mobileInspectionService.findAll("status!" + MobileInspectionStatus.FINISHED, "id,desc").subscribe(res => {
      this.inspectionsArray.unshift(...res.content);
      this.sortByStartTimeInspections();
      this.inspectionsList = of(this.inspectionsArray);
      this.isFetching = false;
    })
  }

  private sortByStartTimeInspections() {
    this.inspectionsArray.sort((res1, res2) => {
      if (res1.startedAt < res2.startedAt)
        return -1;
      if (res1.startedAt > res2.startedAt)
        return 1;
      return 0;
    })
  }
}
