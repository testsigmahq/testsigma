import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {Page} from "../../shared/models/page";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import {Pageable} from "../../shared/models/pageable";
import {Observable, of} from 'rxjs';
import {debounceTime} from 'rxjs/operators';
import {Router} from '@angular/router';

@Component({
  selector: 'app-test-machine-result-details-pagination',
  templateUrl: './test-machine-result-details-pagination.component.html',
  styles: []
})
export class TestMachineResultDetailsPaginationComponent implements OnInit {
  @Input('testDeviceResult') testDeviceResult: TestDeviceResult;


  public showItems: Boolean = false;
  public previousResults: Page<TestDeviceResult>;
  public nextResults: Page<TestDeviceResult>;
  public environmentList: Observable<TestDeviceResult[]>;
  public environmentResultArray: TestDeviceResult[] = [];
  public environmentResultPreviousPage: Page<TestDeviceResult>;
  public environmentResultNextPage: Page<TestDeviceResult>;
  private pageable: Pageable = new Pageable();
  @ViewChild('testMachineResultsViewPort') public testMachineResultsViewPort: CdkVirtualScrollViewport;

  constructor(
    private environmentResultService: TestDeviceResultService,
    public router: Router) {
  }

  ngOnInit() {
  }


  ngOnChanges() {
    this.showItems = false;
    this.pageable.pageNumber = 0;

    this.environmentResultArray = [this.testDeviceResult];
    this.environmentList = of(this.environmentResultArray);
    this.fetchPreviousTestcaseResult(true);
    this.fetchNextTestcaseResults(true);
    this.scrollAndAttachEvent();
  }

  scrollActiveToView() {
    let index = this.environmentResultArray.findIndex(environmentResult => this.testDeviceResult.id == environmentResult.id)
    if ((this.environmentResultArray.length - index) > 4) {
      index -= 1;
    } else if ((this.environmentResultArray.length - index) < 3) {
      index += 3;
    }
    this.testMachineResultsViewPort && this.testMachineResultsViewPort.scrollToIndex(index, 'smooth');
  }

  showList() {
    this.showItems = !this.showItems;
    this.scrollActiveToView();
  }

  listenScrollIndexEvents() {
    this.testMachineResultsViewPort.scrolledIndexChange.pipe(debounceTime(200)).subscribe((scrollIndex) => {
      console.log(scrollIndex);
      if (scrollIndex > this.environmentResultArray.length - 4 && !this.environmentResultNextPage.last) {
        ++this.pageable.pageNumber;
        this.fetchNextTestcaseResults();
      } else if (scrollIndex < 4 && !this.environmentResultPreviousPage.last) {
        ++this.pageable.pageNumber;
        this.fetchPreviousTestcaseResult();
      }
    });
  }

  fetchPreviousTestcaseResult(isInit?) {
    let previousQuery = "executionRunId:" + this.testDeviceResult.testPlanResult.id +
      ",id<" + this.testDeviceResult.id;
    this.environmentResultService.findAll(previousQuery, "id,desc", this.pageable).subscribe(res => {
      this.environmentResultPreviousPage = res;
      if (isInit) {
        this.previousResults = new Page<TestDeviceResult>();
        this.previousResults.content = res.content.reverse();
      }
      this.environmentResultArray.unshift(...res.content);
      this.environmentList = of(this.environmentResultArray);
    });
  }

  fetchNextTestcaseResults(isInit?) {
    let nextQuery = "executionRunId:" + this.testDeviceResult.testPlanResult.id +
      ",id>" + this.testDeviceResult.id;
    this.environmentResultService.findAll(nextQuery, undefined, this.pageable).subscribe(res => {
      this.environmentResultNextPage = res;
      if (isInit) {
        this.nextResults = res;
      }
      this.environmentResultArray.push(...res.content);
      this.environmentList = of(this.environmentResultArray);
    });
  }

  scrollAndAttachEvent() {
    setTimeout(() => {
      this.scrollActiveToView()
      setTimeout(() => {
        this.listenScrollIndexEvents();
      }, 1000);
    }, 200);
  }

  isTestCaseActive(id: number) {
    return this.router.url.indexOf("machine_results/" + id) > 0;
  }
}
