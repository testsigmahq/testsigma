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
        type: 'cylinder',
        options3d: {
            enabled: true,
            alpha: 15,
            beta: 15,
            depth: 50,
            viewDistance: 25
        }
    },
    title: {
        text: 'Number of confirmed COVID-19'
    },
    subtitle: {
        text: 'Source: ' +
            '<a href="https://www.fhi.no/en/id/infectious-diseases/coronavirus/daily-reports/daily-reports-COVID19/"' +
            'target="_blank">FHI</a>'
    },
    legend: {
      enabled: false
    },
    xAxis: {
        categories: ['0-9', '10-19', '20-29', '30-39', '40-49', '50-59', '60-69', '70-79', '80-89', '90+'],
        title: {
            text: 'Age groups'
        }
    },
    yAxis: {
        title: {
            margin: 20,
            text: 'Reported cases'
        }
    },
    tooltip: {
        headerFormat: '<b>Age: {point.x}</b><br>'
    },
    plotOptions: {
        // series: {
        //   type: 'slowstochastic',
        //     depth: 25,
        //     colorByPoint: true
        // }
        column: {
          depth: 25
      }
    },
    series: [{
        type: 'cylinder',
        data: [95321, 169339, 121105, 136046, 106800, 58041, 26766, 14291,
            7065, 3283],
        name: 'Cases',
        showInLegend: false,
        colorByPoint: true
    }]
    };
    });
  }

  populateRunDurationTrendChartOptions(){
    this.reportsService.getRunDurationTrend(39).subscribe(res =>{
      console.log(res);
      this.runDurationTrendChartOptions = {
        chart: {
          type: 'spline'
        },
        title: {
          text: 'Duration Trend',
          align: 'left'
        },
        subtitle: {
          text: 'Trend of time taken for recent test plan run',
          align: 'left'
        },
        xAxis: {
          title: {
              text: 'Test Plan#'
          },
          categories: ['#189', '#190', '#191', '#192', '#193'],
          maxPadding: 0.05,
          showLastLabel: true
      },
      yAxis: {
          title: {
              text: 'Duration'
          },
          labels: {
              format: 'minutes',
              formatter: function () {
                return this.value + 'm';
             }
          },
          lineWidth: 2
      },
      legend: {
          enabled: false
      },
      tooltip: {
          headerFormat: '<b>{series.name}</b><br/>',
          pointFormat: '{point.y} minutes',
          shared: true
      },
      plotOptions: {
          spline: {
              marker: {
                radius: 4,
                lineColor: '#666666',
                lineWidth: 1
              }
          }
      },
      series: [{
          name: 'Duration',
          type: 'line',
          marker: {
            symbol: 'square'
          },
          data: [['#189', 15], ['#190', 20],['#191', 25], ['#192', 30], ['#193', 20] ]
            //   data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, {
            //     y: 26.5,
            //     marker: {
            //        symbol: 'url(http://www.highcharts.com/demo/gfx/sun.png)'
            //     }
            //  }, 23.3, 18.3, 13.9, 9.6]
      }]
      }
    });
  }

  populateTopFailuresChartOptions(){
    this.reportsService.getTopFailures(39).subscribe(res =>{
      console.log(res);
      this.topFailuresChartOptions = {
        chart: {
          type: 'column'
        },
        title: {
          text: 'Top Failures',
          align: 'left'

        },
        subtitle: {
          text: 'Top failures stacked based on their occurance',
          align: 'left'
        },
        xAxis: {
          categories: ['#189', '#190', '#191', '#192', '#193'],
          title: {
            text: 'Test Plan'
        },
      },
      yAxis: {
          min: 0,
          title: {
              text: 'Count of Failures'
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
      exporting: {
        enabled: false
      },
      series: [{
          name: 'Element Not Found Exception',
          type:'column',
          data: [3, 5, 1, 6, 2]
      }, 
      {
          name: 'Stale Element Exception',
          type:'column',
          data: [8, 4, 6, 8, 1]
      }, 
      {
          name: 'Assertion Errors',
          type:'column',
          data: [2, 3, 5, 8, 0]
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
          type: 'pyramid'
        },
        title: {
          text: 'Lingered Tests',
          align: 'left'

        },
        subtitle: {
          text: 'Slowest tests in the recent test runs',
          align: 'left'
        },
      colors: ['#C79D6D', '#B5927B', '#CE9B84', '#B7A58C', '#C7A58C'],
      xAxis: {
          crosshair: true,
          labels: {
              style: {
                  fontSize: '14px'
              }
          },
          type: 'category'
      },
      yAxis: {
          min: 0,
          title: {
              text: 'Duration (s)'
          }
      },
      tooltip: {
          valueSuffix: ' s'
      },
      series: [{
          name: 'Duration',
          colorByPoint: true,
          type: 'bar',
          data: [
              ['Dashboard Test', 138.8],
              ['Analytics Test', 136.4],
              ['Templates Verification', 104],
              ['Integration Workflow', 101.1],
              ['Addons Test', 75]
          ],
          showInLegend: false
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
