import {Component, OnInit} from '@angular/core';
import {TestDataService} from "../../services/test-data.service";
import {TestData} from "../../models/test-data.model";
import {ActivatedRoute} from '@angular/router';
import {FormGroup} from '@angular/forms';
import {TestDataSetService} from "../../services/test-data-set.service";

@Component({
  selector: 'app-test-data-tabs',
  templateUrl: './data.component.html',
  host: {'class': 'd-flex ts-col-100'}
})
export class DataComponent implements OnInit {
  public testData: TestData;
  public testDataForm: FormGroup = new FormGroup({});

  constructor(private route: ActivatedRoute,
              private testDataService: TestDataService,
              private testDataSetService: TestDataSetService) {

  }

  ngOnInit(): void {
    this.testDataService.show(this.route.parent.snapshot.params.testDataId).subscribe(testData => {
      if(testData.isMigrated) {
        this.testDataSetService.findAll("testDataProfileId:" + this.route.parent.snapshot.params.testDataId, 'position').subscribe(res => {
          testData.data = res.content;
          this.testData = testData;
        })
      }
      else{
        this.testData = testData;
      }
    });
  }

}
