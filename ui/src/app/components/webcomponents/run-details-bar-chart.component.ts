import {Component, Input, OnInit} from '@angular/core';
import * as Highcharts from 'highcharts';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Page} from "../../shared/models/page";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestSuiteResult} from "../../models/test-suite-result.model";

@Component({
  selector: 'app-run-details-bar-chart',
  template: `
    <highcharts-chart
      [Highcharts]="Highcharts"
      [options]="chartOptions"
      class="full-width-highcharts w-100"
    ></highcharts-chart>
  `,
  styles: []
})
export class RunDetailsBarChartComponent implements OnInit {
  @Input('results') results: Page<TestDeviceResult | TestSuiteResult>;
  @Input('id') id: string;
  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options;
  private keys: JSON;

  constructor(
    private translate: TranslateService) {
  }

  ngOnInit() {
  }

  ngOnChanges() {
    this.chartOptions = {
      title: {
        text: undefined
      },
      chart: {
        renderTo: document.getElementById(this.id),
        width: document.getElementById(this.id).offsetWidth,
        height: document.getElementById(this.id).offsetHeight
      },
      xAxis: {
        type: "category",
        labels: {
          autoRotation: [-45]
        }
      },
      yAxis: {
        min: 0,
        title: undefined,
        stackLabels: {
          enabled: false
        },
        labels: {
          enabled: false
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
            enabled: true,
            formatter : function(){
              return (this.y!=0)?this.y:"";
            }
          },
          pointWidth: 35
        }
      },
    };
    this.translate.get([
      'execution.result.SUCCESS', 'execution.result.FAILURE', 'execution.result.ABORTED',
      'execution.result.NOT_EXECUTED', 'execution.result.QUEUED',
      'execution.result.STOPPED']).subscribe((keys: JSON) => {
      this.keys = keys;
      this.chartOptions.yAxis['stackLabels'].enabled = this.results.totalElements < 50;
      this.chartOptions.plotOptions.column.pointWidth = this.results.totalElements > 50 ? undefined : 40;
      this.chartOptions['series'] = [{
        name: this.keys['execution.result.SUCCESS'],
        data: this.results.content.map((environmentResult) => [(<TestDeviceResult>environmentResult).testDevice?.title || (<TestSuiteResult>environmentResult).testSuite?.name, environmentResult.passedCount]),
        type: 'column',
        color: '#1FB47E'
      }, {
        name: this.keys['execution.result.FAILURE'],
        data: this.results.content.map((environmentResult) => [(<TestDeviceResult>environmentResult).testDevice?.title || (<TestSuiteResult>environmentResult).testSuite?.name, environmentResult.failedCount]),
        type: 'column',
        color: '#F23D3D'
      }, {
        name: this.keys['execution.result.ABORTED'],
        data: this.results.content.map((environmentResult) => [(<TestDeviceResult>environmentResult).testDevice?.title || (<TestSuiteResult>environmentResult).testSuite?.name, environmentResult.abortedCount]),
        type: 'column',
        color: '#F0B14C'
      }, {
        name: this.keys['execution.result.NOT_EXECUTED'],
        data: this.results.content.map((environmentResult) => [(<TestDeviceResult>environmentResult).testDevice?.title || (<TestSuiteResult>environmentResult).testSuite?.name, environmentResult.notExecutedCount]),
        type: 'column',
        color: '#7A68BC'
      }, {
        name: this.keys['execution.result.QUEUED'],
        data: this.results.content.map((environmentResult) => [(<TestDeviceResult>environmentResult).testDevice?.title || (<TestSuiteResult>environmentResult).testSuite?.name, environmentResult.queuedCount]),
        type: 'column',
        color: '#3C8FE2'
      }, {
        name: this.keys['execution.result.STOPPED'],
        data: this.results.content.map((environmentResult) => [(<TestDeviceResult>environmentResult).testDevice?.title || (<TestSuiteResult>environmentResult).testSuite?.name, environmentResult.stoppedCount]),
        type: 'column',
        color: '#C4C4C4'
      }]
    });
    console.log(this.chartOptions);
    this.chartOptions.series.forEach(chartOption => {
      let data = [...chartOption['data']];
      chartOption['data'] = []
      data.forEach((dataOut, index) => {
        let currentData = [...data];
        currentData = currentData.splice(index+1, currentData.length);
        currentData.forEach(dataIn => {
          if(dataOut[0] == dataIn[0]) {
            dataOut[1] = dataOut[1]+ dataIn[1];
          }
        })
        if(!chartOption['data'].length) {
          chartOption['data'].push(dataOut);
        } else {
          if(!chartOption['data'].find(dataItem => dataItem[0] == dataOut[0]))
            chartOption['data'].push(dataOut);
        }
      });
    });
  }

}
