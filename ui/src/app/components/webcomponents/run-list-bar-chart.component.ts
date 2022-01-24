import {Component, Input, OnInit} from '@angular/core';
import * as Highcharts from 'highcharts';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {Pageable} from "../../shared/models/pageable";
import {Page} from "../../shared/models/page";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {ResultConstant} from "../../enums/result-constant.enum";

@Component({
  selector: 'app-run-list-bar-chart',
  template: `
    <div class="highChart ml-auto m-auto ts-col-100 pb-20">
      <highcharts-chart
        *ngIf="chartOptions?.series"
        [Highcharts]="Highcharts"
        [options]="chartOptions"
        class="full-width-highcharts"
      ></highcharts-chart>
    </div>
    <div
      *ngIf="executionResults?.totalElements > 0"
      class="legend-container overflow-x-auto bg-highlight d-flex fz-12 justify-content-between lsp-19 text-right ts-col-100">
      <div class="legend-item px-14 py-8 text-nowrap pointer flex-auto"
           [class.bg-grey-light]="this.successFilter"
           (click)="toggleSuccessFilter()">
        <i
          [class.bg-active-status-0]="this.successFilter"
          class="result-status-0 fa-show legend-icon btn">
        </i>
        <span [textContent]="('execution.result.SUCCESS' | translate) +
              ' ('+this.passedPercentage+'%)'"></span>
      </div>
      <div
        [class.bg-grey-light]="this.failedFilter"
        class="legend-item px-14 py-8 text-nowrap pointer flex-auto"
        (click)="toggleFailedFilter()">
        <i
          [class.bg-active-status-1]="this.failedFilter"
          class="result-status-1 fa-show legend-icon btn">
        </i>
        <span [textContent]="('execution.result.FAILURE' | translate) +
                '( '+this.failedPercentage+'%)'"></span>
      </div>
      <div
        [class.bg-grey-light]="abortedFilter"
        class="legend-item px-14 py-8 text-nowrap pointer flex-auto"
        (click)="toggleAbortedFilter()">
        <i
          [class.bg-active-status-2]="this.abortedFilter"
          class="result-status-2 fa-show legend-icon btn">
        </i>
        <span [textContent]="('execution.result.ABORTED' | translate) +
                '( '+this.abortedPercentage+'% )'"></span>
      </div>
      <div
        [class.bg-grey-light]="this.notExecutedFilter"
        class="legend-item px-14 py-8 text-nowrap pointer flex-auto"
        (click)="toggleNotExecutedFilter()">
        <i
          [class.bg-active-status-3]="this.notExecutedFilter"
          class="result-status-3 fa-show legend-icon btn">
        </i>
        <span [textContent]="('execution.result.NOT_EXECUTED' | translate) +
                '( '+this.notExecutedPercentage+'% )'"></span>
      </div>
      <div
        [class.bg-grey-light]="this.queuedFilter"
        class="legend-item px-14 py-8 text-nowrap pointer"
           (click)="toggleQueuedFilter()">
        <i
          [class.bg-active-status-5]="this.queuedFilter"
          class="result-status-5 fa-show legend-icon btn">
        </i>
        <span [textContent]="('execution.result.QUEUED' | translate) +
                '( '+this.queuedPercentage+'% )'"></span>
      </div>
      <div
        [class.bg-grey-light]="this.stoppedFilter"
        class="legend-item px-14 py-8 text-nowrap pointer flex-auto"
        (click)="toggleStoppedFilter()">
        <i
          [class.bg-active-status-6]="this.stoppedFilter"
          class="result-status-6 fa-show legend-icon btn">
        </i>
        <span [textContent]="('execution.result.STOPPED' | translate) +
                '( '+this.stoppedPercentage+'% )'"></span>
      </div>
    </div>
  `,
  styles: []
})
export class RunListBarChartComponent implements OnInit {
  @Input('execution') execution: TestPlan;

  public stoppedFilter: Boolean;
  public successFilter: Boolean;
  public failedFilter: Boolean;
  public abortedFilter: Boolean;
  public notExecutedFilter: Boolean;
  public queuedFilter: Boolean;

  public resultConstant: typeof ResultConstant = ResultConstant;
  public executionResults: Page<TestPlanResult>;
  private keys: JSON;
  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options = {
    title: {
      text: undefined
    },
    chart: {
      margin: 0,
      width: 1160,
      height: 180
    },
    xAxis: {
      visible: false,
      labels: {
        enabled: false
      }
    },
    yAxis: {
      min: 0,
      title: undefined,
      stackLabels: {
        enabled: true
      },
      gridLineWidth: 0
    },
    credits: {
      enabled: false
    },
    tooltip: {
      enabled: true
    },
    legend: {
      enabled: false
    },
    plotOptions: {
      column: {
        stacking: 'normal',
        dataLabels: {
          enabled: false
        },
        pointWidth: 18
      }
    },
  };

  constructor(
    private executionResultService: TestPlanResultService,
    private translate: TranslateService) {

  }

  ngOnInit() {
    this.translate.get([
      'execution.result.SUCCESS', 'execution.result.FAILURE', 'execution.result.ABORTED',
      'execution.result.NOT_EXECUTED', 'execution.result.QUEUED',
      'execution.result.STOPPED']).subscribe((keys: JSON) => {
      this.keys = keys;
      this.populateChartOptions("", true);
    });
  }

  populateChartOptions(query, firstRequest?: boolean) {
    query += "reRunParentId:null,testPlanId:" + this.execution.id;
    let pageable = new Pageable();
    pageable.pageSize = 300;
    pageable.pageNumber = 0;
    this.chartOptions['series'] = undefined;
    this.executionResultService.findAll(query, undefined, pageable).subscribe((res) => {
      if (firstRequest) {
        this.executionResults = res;
      }
      this.chartOptions.yAxis['stackLabels'].enabled = res.totalElements < 10;
      this.chartOptions.plotOptions.column.pointWidth = res.totalElements > 50 ? undefined : 18;
      this.chartOptions['series'] = [{
        name: this.keys['execution.result.SUCCESS'],
        data: res.content.map((executionResult) => executionResult.passedCount),
        type: 'column',
        color: '#1FB47E'
      }, {
        name: this.keys['execution.result.FAILURE'],
        data: res.content.map((executionResult) => executionResult.failedCount),
        type: 'column',
        color: '#F23D3D'
      }, {
        name: this.keys['execution.result.ABORTED'],
        data: res.content.map((executionResult) => executionResult.abortedCount),
        type: 'column',
        color: '#F0B14C'
      }, {
        name: this.keys['execution.result.NOT_EXECUTED'],
        data: res.content.map((executionResult) => executionResult.notExecutedCount),
        type: 'column',
        color: '#7A68BC'
      }, {
        name: this.keys['execution.result.QUEUED'],
        data: res.content.map((executionResult) => executionResult.totalCount == 0 && executionResult.isQueued ? 1 : executionResult.queuedCount),
        type: 'column',
        color: '#3C8FE2'
      }, {
        name: this.keys['execution.result.STOPPED'],
        data: res.content.map((executionResult) => executionResult.totalCount == 0 && !executionResult.isQueued ? 1 : executionResult.stoppedCount),
        type: 'column',
        color: '#C4C4C4'
      }]
    });
  }

  toggleNotExecutedFilter() {
    this.notExecutedFilter = !this.notExecutedFilter;
    if (this.notExecutedFilter) {
      this.populateChartOptions("result:" + ResultConstant.NOT_EXECUTED);
    } else {
      this.populateChartOptions("");
    }
  }

  toggleAbortedFilter() {
    this.abortedFilter = !this.abortedFilter;
    if (this.abortedFilter) {
      this.populateChartOptions("result:" + ResultConstant.ABORTED);
    } else {
      this.populateChartOptions("");
    }
  }

  toggleSuccessFilter() {
    this.successFilter = !this.successFilter;
    if (this.successFilter) {
      this.populateChartOptions("result:" + ResultConstant.SUCCESS);
    } else {
      this.populateChartOptions("");
    }
  }

  toggleFailedFilter() {
    this.failedFilter = !this.failedFilter;
    if (this.failedFilter) {
      this.populateChartOptions("result:" + ResultConstant.FAILURE);
    } else {
      this.populateChartOptions("");
    }
  }

  toggleQueuedFilter() {
    this.queuedFilter = !this.queuedFilter;
    if (this.queuedFilter) {
      this.populateChartOptions("result:" + ResultConstant.QUEUED);
    } else {
      this.populateChartOptions("");
    }
  }

  toggleStoppedFilter() {
    this.stoppedFilter = !this.stoppedFilter;
    if (this.stoppedFilter) {
      this.populateChartOptions("result:" + ResultConstant.STOPPED);
    } else {
      this.populateChartOptions("");
    }
  }

  get passedPercentage(): Number {
    return Math.round(((this.executionResults.content.filter(res => res.isPassed).length) / this.executionResults.totalElements) * 100);
  }

  get failedPercentage(): Number {
    return Math.round(((this.executionResults.content.filter(res => res.isFailed).length) / this.executionResults.totalElements) * 100);
  }

  get notExecutedPercentage(): Number {
    return Math.round(((this.executionResults.content.filter(res => res.isNotExecuted).length) / this.executionResults.totalElements) * 100);
  }

  get abortedPercentage(): Number {
    return Math.round(((this.executionResults.content.filter(res => res.isAborted).length) / this.executionResults.totalElements) * 100);
  }

  get queuedPercentage(): Number {
    return Math.round(((this.executionResults.content.filter(res => res.isQueued).length) / this.executionResults.totalElements) * 100);
  }

  get stoppedPercentage(): Number {
    return Math.round(((this.executionResults.content.filter(res => res.isStopped).length) / this.executionResults.totalElements) * 100);
  }
}
