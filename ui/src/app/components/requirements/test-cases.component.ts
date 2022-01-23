import {Component, OnInit} from '@angular/core';
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestCaseService} from "../../services/test-case.service";
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-test-cases',
  templateUrl: './test-cases.component.html',
  styles: []
})
export class TestCasesComponent implements OnInit {
  testCases: InfiniteScrollableDataSource;

  constructor(
    private route: ActivatedRoute,
    private testCaseService: TestCaseService) {
  }

  ngOnInit(): void {
    this.testCases = new InfiniteScrollableDataSource(
      this.testCaseService,
      "deleted:false,isStepGroup:false,requirementId:" + this.route.parent.snapshot.params.requirementId,
      "createdDate,desc");
  }

}
