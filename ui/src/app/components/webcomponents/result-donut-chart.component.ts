import {Component, Input, OnInit} from '@angular/core';
import * as Highcharts from 'highcharts';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {ResultBase} from "../../models/result-base.model";

@Component({
  selector: 'app-result-donut-chart',
  template: `
    <highcharts-chart
      [Highcharts]="Highcharts"
      [options]="chartOptions"
    ></highcharts-chart>
  `,
  styles: []
})
export class ResultDonutChartComponent implements OnInit {
  @Input('width') width: number;
  @Input('height') height: number;
  @Input('resultEntity') resultEntity: ResultBase;

  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options;

  constructor(public translate: TranslateService) {
  }

  ngOnChanges() {
    this.translate.get([
      'execution.result.SUCCESS', 'execution.result.FAILURE', 'execution.result.ABORTED',
      'execution.result.NOT_EXECUTED', 'execution.result.QUEUED',
      'execution.result.STOPPED']).subscribe((keys) => {
      this.populateChartOptions(keys);
    });
  }

  ngOnInit() {
  }

  chartTitle(keys): string {
    let textSize = this.width <= 90 ? 'sm fz-10 pl-2 pt-10 flex-column' : 'md fz-12 pt-14';
    let enableCount = !this.resultEntity?.isExecuting ? 'd-block' : 'd-none';
    let title = '<div class="result-status-text-0 d-flex align-items-center text-black ' + textSize + '">' +
      '<div class="text-center text-black rb-regular-i ' + enableCount + '">' + 'Total=' + '</div>' +
      '<div class="f-semi-bold text-center status-value text-black rb-regular-i ' + enableCount + '" >' + this.resultEntity?.totalCountValue + '</div>' +
      '</div>';
    return title;
  }

  populateChartOptions(keys) {
    let data = [{
      name: keys['execution.result.SUCCESS'],
      y: Math.round(((this.resultEntity?.passedCount || 0) / (this.resultEntity?.totalCount || 1)) * 100),
      color: '#1FB47E'
    }, {
      name: keys['execution.result.FAILURE'],
      y: Math.round(((this.resultEntity?.failedCount || 0) / (this.resultEntity?.totalCount || 1)) * 100),
      color: '#F23D3D'
    }, {
      name: keys['execution.result.ABORTED'],
      y: Math.round(((this.resultEntity?.abortedCount || 0) / (this.resultEntity?.totalCount || 1)) * 100),
      color: '#F0B14C'
    }, {
      name: keys['execution.result.NOT_EXECUTED'],
      y: Math.round(((this.resultEntity?.notExecutedCount || 0) / (this.resultEntity?.totalCount || 1)) * 100),
      color: '#7A68BC'
    }, {
      name: keys['execution.result.QUEUED'],
      y: Math.round(((this.resultEntity?.totalCount == 0 && this.resultEntity?.isQueued ? 1 : this.resultEntity?.queuedCount) / (this.resultEntity?.totalCount || 1)) * 100),
      color: '#3C8FE2'
    }, {
      name: keys['execution.result.STOPPED'],
      y: Math.round(((this.resultEntity?.totalCount == 0 && !this.resultEntity?.isQueued ? 1 : this.resultEntity?.stoppedCount) / (this.resultEntity?.totalCount || 1)) * 100),
      color: '#C4C4C4'
    }]
    data = data.filter(res => res.y>0)
    this.chartOptions = {
      title: {
        verticalAlign: 'middle',
        floating: true,
        text: this.chartTitle(keys),
        useHTML: true
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
        outside: true,
        valueSuffix: ' %'
      },
      plotOptions: {
        pie: {
          size: '100%',
          innerSize: '60%',
          slicedOffset: 0,
          dataLabels: {
            enabled: true,
            distance: -12,
            color: '#fff',
            formatter: function() {
              return this.y + '%';
            },
            style: {
              textOutline: null
            }
          },
          states: {
            hover: {
              halo: null
            }
          }
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
