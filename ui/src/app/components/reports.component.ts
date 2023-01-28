import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import * as Highcharts from 'highcharts';
import {ReportsService} from "../services/reports.service";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import jspdf from 'jspdf';
import html2canvas from 'html2canvas';

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
    setTimeout(function(){
    var elem = document.getElementById("run-chart");
    html2canvas(elem).then(canvas => {
      var imgWidth = 208;
      var imgHeight = canvas.height * imgWidth / canvas.width;
      const contentDataURL = canvas.toDataURL('image/png')
      var position = 0;
      var doc = new jspdf("p","mm","a4");
      doc.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight)
      doc.save('skill-set.pdf');
    });
    },2000);
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
            enabled: false
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
