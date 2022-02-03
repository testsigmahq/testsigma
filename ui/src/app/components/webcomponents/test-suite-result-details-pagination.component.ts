import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {Pageable} from "../../shared/models/pageable";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {Page} from "../../shared/models/page";
import {CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import {Observable, of} from 'rxjs';
import {debounceTime} from 'rxjs/operators';
import {Router} from '@angular/router';

@Component({
  selector: 'app-test-suite-result-details-pagination',
  templateUrl: './test-suite-result-details-pagination.component.html',
  styles: []
})
export class TestSuiteResultDetailsPaginationComponent implements OnInit {
  @Input('suiteResult') suiteResult: TestSuiteResult;

  public showItems: Boolean = false;
  public previousResults: Page<TestSuiteResult>;
  public nextResults: Page<TestSuiteResult>;
  public suiteResultList: Observable<TestSuiteResult[]>;
  public suiteResultArray: TestSuiteResult[] = [];
  public suiteResultPreviousPage: Page<TestSuiteResult>;
  public suiteResultNextPage: Page<TestSuiteResult>;
  private pageable: Pageable = new Pageable();
  @ViewChild('suiteResultsViewPort') public suiteResultsViewPort: CdkVirtualScrollViewport;

  constructor(
    private testSuiteResultService: TestSuiteResultService,
    public router: Router) {
  }

  ngOnInit() {
  }

  ngOnChanges() {
    this.showItems = false;
    this.pageable.pageNumber = 0;

    this.suiteResultArray = [this.suiteResult];
    this.suiteResultList = of(this.suiteResultArray);
    this.fetchPreviousTestcaseResult(true);
    this.fetchNextTestcaseResults(true);
    this.scrollAndAttachEvent();
  }

  scrollActiveToView() {
    let index = this.suiteResultArray.findIndex(environmentResult => this.suiteResult.id == environmentResult.id)
    if ((this.suiteResultArray.length - index) > 4) {
      index -= 1;
    } else if ((this.suiteResultArray.length - index) < 3) {
      index += 3;
    }
    this.suiteResultsViewPort && this.suiteResultsViewPort.scrollToIndex(index, 'smooth');
  }

  showList() {
    this.showItems = !this.showItems;
    this.scrollActiveToView();
  }

  listenScrollIndexEvents() {
    this.suiteResultsViewPort.scrolledIndexChange.pipe(debounceTime(200)).subscribe((scrollIndex) => {
      console.log(scrollIndex);
      if (scrollIndex > this.suiteResultArray.length - 4 && !this.suiteResultNextPage.last) {
        ++this.pageable.pageNumber;
        this.fetchNextTestcaseResults();
      } else if (scrollIndex < 4 && !this.suiteResultPreviousPage.last) {
        ++this.pageable.pageNumber;
        this.fetchPreviousTestcaseResult();
      }
    });
  }

  fetchPreviousTestcaseResult(isInit?) {
    let previousQuery = "environmentResultId:" + this.suiteResult.testDeviceResult.id + ",id<" + this.suiteResult.id;
    this.testSuiteResultService.findAll(previousQuery, "id,desc", this.pageable).subscribe(res => {
      this.suiteResultPreviousPage = res;
      if (isInit) {
        this.previousResults = new Page<TestSuiteResult>();
        this.previousResults.content = res.content.reverse();
      }
      this.suiteResultArray.unshift(...res.content);
      this.suiteResultList = of(this.suiteResultArray);
    });
  }

  fetchNextTestcaseResults(isInit?) {
    let nextQuery = "environmentResultId:" + this.suiteResult.testDeviceResult.id + ",id>" + this.suiteResult.id;
    this.testSuiteResultService.findAll(nextQuery, undefined, this.pageable).subscribe(res => {
      this.suiteResultNextPage = res;
      if (isInit) {
        this.nextResults = res;
      }
      this.suiteResultArray.push(...res.content);
      this.suiteResultList = of(this.suiteResultArray);
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
    return this.router.url.indexOf("suite_results/" + id) > 0;
  }

}
