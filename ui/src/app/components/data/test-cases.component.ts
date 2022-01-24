import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {TestCaseService} from "../../services/test-case.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";

@Component({
  selector: 'app-test-data-profiles',
  templateUrl: './test-cases.component.html',
  host: {'class': 'ts-col-100'},
})
export class TestCasesComponent implements OnInit {
  testCases: InfiniteScrollableDataSource;

  constructor(
    private testCaseService: TestCaseService,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.testCases = new InfiniteScrollableDataSource(
      this.testCaseService,
      "deleted:false,testDataId:" + this.route.parent.snapshot.params.testDataId,
      "name,asc");
  }
}
