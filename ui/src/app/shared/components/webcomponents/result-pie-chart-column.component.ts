import {Component, Input, OnInit} from '@angular/core';
import * as Highcharts from 'highcharts';
import {TranslateService} from '@ngx-translate/core';
import {ResultBase} from "../../../models/result-base.model";

@Component({
  selector: 'app-result-pie-chart-column',
  template: `
    <div class="d-flex">
      <div class="chart-status-container">
          <highcharts-chart
            *ngIf="!resultEntity?.isPassed && !resultEntity?.isFailed"
            [Highcharts]="Highcharts"
            [options]="chartOptions"
            style="flex: 0 0 auto"
          ></highcharts-chart>
        <div
          *ngIf="resultEntity?.isPassed"
          [class.fa-result-0]="width < 36 "
          [class.fz-20]="width < 36 "
          [class.success-status]="width >= 36 "
          class="sample" [style]="'width:'+width+';height:'+height">
        </div>
        <div
          *ngIf="resultEntity?.isFailed"
          [class.failure-sh]="width < 36 "
          [class.fz-20]="width < 36 "
          [class.failure-status]="width >= 36 "
          class="sample" [style]="'width:'+width+';height:'+height">
        </div>
        <div
          class="chart-status result-status-text-1"
          *ngIf="resultEntity?.isFailed">
          <div class="d-flex align-items-end">
            <div
              class="status-text"
              [translate]="'execution.result.FAILURE'"></div>
            <app-vi-icon [isVisuallyPassed]="resultEntity?.isVisuallyPassed"></app-vi-icon>
          </div>
        </div>
        <div
          class="chart-status result-status-text-2"
          *ngIf="resultEntity?.isAborted">
          <div class="d-flex align-items-end">
            <div
              class="status-text"
              [translate]="'execution.result.ABORTED'"></div>
            <app-vi-icon [isVisuallyPassed]="resultEntity?.isVisuallyPassed"></app-vi-icon>
          </div>
        </div>
        <div
          class="chart-status result-status-text-3"
          *ngIf="resultEntity?.isNotExecuted">
          <div class="d-flex align-items-end">
            <div
              class="status-text"
              [translate]="'execution.result.NOT_EXECUTED'"></div>
            <app-vi-icon [isVisuallyPassed]="resultEntity?.isVisuallyPassed"></app-vi-icon>
          </div>
        </div>
        <div
          class="chart-status result-status-text-6"
          *ngIf="resultEntity?.isStopped">
          <div class="d-flex align-items-end">
            <div
              class="status-text"
              [translate]="'execution.result.STOPPED'"></div>
            <app-vi-icon [isVisuallyPassed]="resultEntity?.isVisuallyPassed"></app-vi-icon>
          </div>
        </div>
        <div
          class="chart-status text-t-secondary"
          *ngIf="resultEntity?.isPassed">
          <div class="d-flex align-items-end">
            <div
              class="status-text"
              [translate]="'execution.result.SUCCESS'"></div>
            <app-vi-icon [isVisuallyPassed]="resultEntity?.isVisuallyPassed"></app-vi-icon>
          </div>
        </div>
        <div class="chart-status text-t-secondary"
             *ngIf="resultEntity?.isQueued">
          <div class="d-flex align-items-end">
            <div
              class="status-text"
              [ngClass]="{'running-status': resultEntity?.isRunning}"
              [translate]="resultEntity?.isRunning? 'execution.result.running' : 'execution.result.QUEUED'"></div>
            <app-vi-icon [isVisuallyPassed]="resultEntity?.isVisuallyPassed"></app-vi-icon>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class ResultPieChartColumnComponent implements OnInit {
  @Input('width') width: number = 36;
  @Input('height') height: number = 36;
  @Input('resultEntity') resultEntity: ResultBase;
  @Input('totalCount') totalCount: number;

  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options;

  constructor(public translate: TranslateService) {

  }

  ngOnInit() {
    if(!this.totalCount){
      this.totalCount = this.resultEntity?.totalCount;
    }
    this.translate.get([
      'execution.result.SUCCESS', 'execution.result.FAILURE', 'execution.result.ABORTED',
      'execution.result.NOT_EXECUTED','execution.result.QUEUED',
      'execution.result.STOPPED']).subscribe((keys) => {
      this.populateChartOptions(keys);
    });
  }

  ngOnChanges() {
    this.translate.get([
      'execution.result.SUCCESS', 'execution.result.FAILURE', 'execution.result.ABORTED',
      'execution.result.NOT_EXECUTED','execution.result.QUEUED',
      'execution.result.STOPPED']).subscribe((keys) => {
      this.populateChartOptions(keys);
    });
  }

  get isRunning(){
    return this.resultEntity?.isRunning && (this.resultEntity?.totalCount == 0 || this.resultEntity?.totalCount == this.resultEntity?.queuedCount)
  }

  populateChartOptions(keys) {
    let data = [{
      name: keys['execution.result.SUCCESS'],
      y: this.resultEntity.passedCount,
      color: '#1FB47E'
    }, {
      name: keys['execution.result.FAILURE'],
      y: this.resultEntity.failedCount,
      color: '#F23D3D'
    }, {
      name: keys['execution.result.ABORTED'],
      y: this.resultEntity.abortedCount,
      color: '#F0B14C'
    }, {
      name: keys['execution.result.NOT_EXECUTED'],
      y: this.resultEntity.notExecutedCount,
      color: '#7A68BC'
    }, {
      name: this.isRunning ? keys['execution.result.running'] : keys['execution.result.QUEUED'],
      y: this.isRunning || (this.resultEntity?.totalCount == 0 && this.resultEntity?.isQueued && !this.resultEntity?.isRunning) ? 1 : this.resultEntity?.queuedCount, // Need to revamp the running count
      color: this.isRunning ? '#038061' : '#3C8FE2'
    }, {
      name: keys['execution.result.STOPPED'],
      y: this.resultEntity.totalCount == 0 && !this.resultEntity.isQueued ? 1 : this.resultEntity.stoppedCount,
      color: '#C4C4C4'
    }]
    data = data.filter(res => res.y > 0)
    this.chartOptions = {
      title: {
        text: undefined
      },
      chart: {
        type: 'pie',
        margin: 0,
        width: this.width,
        height: this.height,
        backgroundColor: 'transparent'
      },
      credits: {
        enabled: false
      },
      tooltip: {
        outside: true
      },
      plotOptions: {
        pie: {
          size: '100%',
          slicedOffset: 0,
          dataLabels: {
            enabled: false
          },
          states: {
            hover: {
              halo: null
            }
          },
          animation: false
        }
      },
      accessibility: {
        point: {
          valueSuffix: '%'
        }
      },
      series: [{
        name: 'Count',
        colorByPoint: true,
        data: data,
        type: 'pie'
      }]
    };
  }

}
