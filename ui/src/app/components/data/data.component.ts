import {Component, OnInit} from '@angular/core';
import {TestDataService} from "../../services/test-data.service";
import {TestData} from "../../models/test-data.model";
import {ActivatedRoute} from '@angular/router';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-test-data-tabs',
  templateUrl: './data.component.html',
  host: {'class': 'd-flex ts-col-100'}
})
export class DataComponent implements OnInit {
  public testData: TestData;
  public testDataForm: FormGroup = new FormGroup({});

  constructor(private route: ActivatedRoute, private testDataService: TestDataService) {

  }

  ngOnInit(): void {
    this.testDataService.show(this.route.parent.snapshot.params.testDataId).subscribe(res => this.testData = res);
  }

}
