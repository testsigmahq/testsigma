import {Component, Input, OnInit} from '@angular/core';
import * as Highcharts from 'highcharts';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-by-summary-donut-chart',
  template: `
    <highcharts-chart
      [Highcharts]="Highcharts"
      [options]="chartOptions"
    ></highcharts-chart>
  `,
  styles: [
  ]
})
export class BySummaryDonutChartComponent implements OnInit {
  @Input('width') width: number;
  @Input('height') height: number;
  @Input('entity') entity: any;

  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options;
  constructor(public translate: TranslateService) { }


  ngOnChanges() {
      this.populateChartOptions(this.entity)
  }
  ngOnInit(): void {
  }


  populateChartOptions(keys) {
    this.chartOptions = {
      title: {
        text: ''
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
          innerSize: '60%',
          slicedOffset: 0,
          dataLabels: {
            enabled: false
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
        data: keys,
        type: 'pie'
      }]
    };
  }
}
