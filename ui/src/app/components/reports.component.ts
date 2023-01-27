import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import * as Highcharts from 'highcharts';
import {AddonActionService} from "../services/addon-action.service";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styles: [
  ],
  host: {'class': 'page-content-container'},
})
export class ReportsComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    public authGuard: AuthenticationGuard
  ) {
  }
  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options;

  ngOnInit(): void {
    this.populateChartOptions();
  }

  populateChartOptions() {
    this.chartOptions = {
      chart: {
        backgroundColor: 'transparent',
        margin: 0,
        width: 200,
        height: 200,
        type: 'pie'
      },
      title: {
        text: ''
      },
      credits: {
        enabled: false
      },
      tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      accessibility: {
        point: {
          valueSuffix: '%'
        }
      },
      plotOptions: {
        pie: {
          size: '100%',
          innerSize: '60%',
          slicedOffset: 0,
          allowPointSelect: true,
          dataLabels: {
            enabled: true,
            padding: 0,
            style: {
              fontSize: '8px'
            }
          }
        }
      },
      series: [{
        name: 'Brands',
        colorByPoint: true,
        type:'pie',
        data: [{
          name: 'Chrome',
          y: 70,
          color:'#1FB47E'
        },{
          name: 'Internet Explorer',
          y: 30,
          color:'#1FA87E'
        }]
      }]
    };
  }
}
