import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {ResultConstant} from "../../enums/result-constant.enum";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {Page} from "../../shared/models/page";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {Pageable} from "../../shared/models/pageable";

@Component({
  selector: 'app-run-details-quick-info',
  templateUrl: './run-details-quick-info.component.html',
  styles: []
})
export class RunDetailsQuickInfoComponent extends BaseComponent implements OnInit {
  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Input('showList') showList: String;
  @Output('toggleDetailsAction') toggleDetailsAction = new EventEmitter<Boolean>();
  @Output('filterAction') filterAction = new EventEmitter<any>();
  @ViewChild('buildNoInput') buildNoInput: ElementRef;

  public results: Page<TestDeviceResult | TestSuiteResult>;
  public showRunDetails: Boolean;
  public resultConstant: typeof ResultConstant = ResultConstant;
  public isEditBuildNo: boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testPlanResultService: TestPlanResultService,
    private environmentResultService: TestDeviceResultService,
    private testSuiteResultService: TestSuiteResultService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    // this.userService.show(this.testPlanResult.executedById).subscribe(user => this.testPlanResult.executedBy = user);
  }

  ngOnChanges() {
    if (this.showList == 'TCR') {
      this.testPlanResult.consolidateCount()
    }
    if (this.showList == 'TMR') {
      let page = new Pageable();
      page.pageSize = 100;
      this.environmentResultService.findAll("testPlanResultId:" + this.testPlanResult.id, undefined, page).subscribe(res => {
        this.results = res;
        if (this.testPlanResult.childResult) new TestPlanResult().consolidateListCount(res);
      });
    } else if (this.showList == 'TSR') {
      let page = new Pageable();
      page.pageSize = 1000;
      this.testSuiteResultService.findAll("testPlanResultId:" + this.testPlanResult.id, undefined, page).subscribe(res => {
        this.results = res
        if (this.testPlanResult.childResult) new TestPlanResult().consolidateListCount(res);
      });
    }
  }

  toggleDetails() {
    this.showRunDetails = !this.showRunDetails;
    this.toggleDetailsAction.emit(this.showRunDetails);
  }

  filter(query) {
    this.filterAction.emit({
      applyFilter: true,
      filterResult: [query]
    });
  }

  updateBuildId() {
    this.testPlanResultService.update(this.testPlanResult).subscribe({
      next: () => {
        this.translate.get("message.common.update.success", {FieldName: 'Build Number'}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
          this.toggleBuildNo();
        });
      }, error:(error) => {
        this.translate.get("message.common.update.failure", {FieldName: 'Build Number'}).subscribe((res: string) => {
          this.showAPIError(error, res);
        });
    }
    })
  }

  toggleBuildNo() {
    this.isEditBuildNo = !this.isEditBuildNo;
    if (this.isEditBuildNo) {
      setTimeout(() => {
        this.buildNoInput.nativeElement.focus();
      }, 10);
    }
  }

  get passedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isPassed).length) / this.results.totalElements) * 100);
  }

  get failedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isFailed).length) / this.results.totalElements) * 100);
  }

  get notExecutedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isNotExecuted).length) / this.results.totalElements) * 100);
  }

  get abortedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isAborted).length) / this.results.totalElements) * 100);
  }

  get queuedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isQueued).length) / this.results.totalElements) * 100);
  }

  get stoppedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isStopped).length) / this.results.totalElements) * 100);
  }

  get passedCount(): Number {
    return this.results.content.filter(res => res.isPassed).length;
  }

  get failedCount(): Number {
    return this.results.content.filter(res => res.isFailed).length;
  }

  get notExecutedCount(): Number {
    return this.results.content.filter(res => res.isNotExecuted).length;
  }

  get abortedCount(): Number {
    return this.results.content.filter(res => res.isAborted).length;
  }

  get queuedCount(): Number {
    return this.results.content.filter(res => res.isQueued).length;
  }

  get stoppedCount(): Number {
    return this.results.content.filter(res => res.isStopped).length;
  }

  get totalCount(): Number {
    return this.results.content.length;
  }
}
