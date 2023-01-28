import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import * as Highcharts from 'highcharts';
import {ReportsService} from "../services/reports.service";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import jspdf from 'jspdf';
import html2canvas from 'html2canvas';
import {TestDeviceResult} from "../models/test-device-result.model";
import {TestSuiteResult} from "../models/test-suite-result.model";

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
    public authGuard: AuthenticationGuard,
    public reportsService: ReportsService
  ) {
  }
  public Highcharts: typeof Highcharts = Highcharts;
  public runDurationTrendChartOptions: Highcharts.Options;
  public flakyTestsChartOptions : Highcharts.Options;
  public topFailuresChartOptions : Highcharts.Options;
  public lingeredTestsChartOptions : Highcharts.Options;
  public failuresByCategoryChartOptions : Highcharts.Options;


  ngOnInit(): void {
    this.populateFlakyTestsChartOptions();
    this.populateRunDurationTrendChartOptions();
    this.populateTopFailuresChartOptions();
    this.populateLingeredTestsChartOptions();
    this.populateFailuresByCategoryChartOptions();
  }

  populateFlakyTestsChartOptions() {
    this.reportsService.getFlakyTests(39).subscribe(res =>{
     console.log(res);
    this.flakyTestsChartOptions = {
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
    });
  }


  populateRunDurationTrendChartOptions(){
    this.reportsService.getRunDurationTrend(39).subscribe(res =>{
      console.log(res);
      this.runDurationTrendChartOptions = {
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
    });
  }

  populateTopFailuresChartOptions(){
    this.reportsService.getTopFailures(39).subscribe(res =>{
      console.log(res);
      this.topFailuresChartOptions = {
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
    });
  }

  populateLingeredTestsChartOptions(){
    this.reportsService.getLingeredTests(39).subscribe(res =>{
      console.log(res);
      let formatted = this.formatLingeredTestsOutput(res);
      this.lingeredTestsChartOptions = {
        chart: {
          type: 'column'
        },
        title: {
          text: '',
          align: 'left'
        },
        xAxis: {
          categories: formatted.categories,
          title:{
            text: "Test Case ID"
          }
        },
        yAxis: {
          min: 0,
          title: {
            text: 'Lingered Tests'
          },
          stackLabels: {
            enabled: true,
            style: {
              fontWeight: 'bold',
              color: ( // theme
                Highcharts.defaultOptions.title.style &&
                Highcharts.defaultOptions.title.style.color
              ) || 'gray',
              textOutline: 'none'
            }
          }
        },
        legend: {
          align: 'left',
          x: 70,
          verticalAlign: 'top',
          y: 70,
          floating: true,
          backgroundColor:
            Highcharts.defaultOptions.legend.backgroundColor || 'white',
          borderColor: '#CCC',
          borderWidth: 1,
          shadow: false
        },
        tooltip: {
          headerFormat: '<b>{point.x}</b><br/>',
          pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
        },
        plotOptions: {
          column: {
            stacking: 'normal',
            dataLabels: {
              enabled: true
            }
          }
        },
        series: [{
          name: "SUCCESS",
          data: [2,2],
          type: 'column',
          color: '#1FB47E'
        }, {
          name: "FAILURE",
          data: [1,3],
          type: 'column',
          color: 'red'
        }]
      }
    });
  }


  populateFailuresByCategoryChartOptions(){
    this.reportsService.getFailuresByCategory(39).subscribe(res =>{
      console.log(res);
      this.failuresByCategoryChartOptions = {
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
    });
  }


  exportToPDF(elemId){
    setTimeout(function(){
      var elem = document.getElementById(elemId);
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

  formatLingeredTestsOutput(res){
    let obj = {
      categories:[],
      series:[]
    };
    let categories = [];
    let series = [];
    res.forEach((obj)=>{
      if(categories.indexOf(obj.testCaseId)==-1)
        categories.push(obj.testCaseId);
      if(series.indexOf(obj.result)==-1)
        series.push(obj.result);
    });

    obj.categories = categories;
    return obj;
  }
}
