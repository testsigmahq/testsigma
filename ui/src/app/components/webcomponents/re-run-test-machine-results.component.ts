import {Component, OnInit, Input, EventEmitter, Output} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";

@Component({
  selector: 'app-re-run-test-machine-results',
  templateUrl: './re-run-test-machine-results.component.html',
  styles: []
})
export class ReRunTestMachineResultsComponent implements OnInit {
  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Output('onTestMachineResultShow') onTestMachineResultShow = new EventEmitter<TestDeviceResult>();

  testMachineResults : InfiniteScrollableDataSource;
  constructor(private environmentResultService: TestDeviceResultService) {
  }

  ngOnInit(): void {
    let query = "childRunId:"+this.testPlanResult.childResult.id;
    this.testMachineResults = new InfiniteScrollableDataSource(this.environmentResultService, query);
  }

  showTestMachineResult(environmentResult: TestDeviceResult) {
    this.onTestMachineResultShow.emit(environmentResult);
  }

}
