import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {TestCaseResult} from "../../models/test-case-result.model";
import {Page} from "../../shared/models/page";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import {Pageable} from "../../shared/models/pageable";
import {Observable, of} from 'rxjs';
import {debounceTime} from 'rxjs/operators';
import {Router} from '@angular/router';

@Component({
  selector: 'app-test-case-result-details-pagination',
  templateUrl: './test-case-result-details-pagination.component.html',
  styles: []
})

export class TestCaseResultDetailsPaginationComponent implements OnInit {
  @Input('testCaseResult') testCaseResult: TestCaseResult;

  public previousResults: Page<TestCaseResult>;
  public nextResults: Page<TestCaseResult>;
  public testCasesList: Observable<TestCaseResult[]>;
  public testcaseResultArray: TestCaseResult[] = [];
  public testcaseResultPreviousPage: Page<TestCaseResult>;
  public testcaseResultNextPage: Page<TestCaseResult>;
  private pageable: Pageable = new Pageable();
  public showItems: Boolean = false;

  @ViewChild('testCaseResultsViewPort') public testCaseResultsViewPort: CdkVirtualScrollViewport;

  constructor(
    private testCaseResultService: TestCaseResultService,
    public router: Router) {
  }

  ngOnInit() {
  }

  ngOnChanges() {
    this.showItems = false;
    this.pageable.pageNumber = 0;
    if(this.testCaseResult.iteration)
      this.testcaseResultArray = [this.testCaseResult.parentResult];
    else
      this.testcaseResultArray = [this.testCaseResult];
    this.testCasesList = of(this.testcaseResultArray);
    this.fetchPreviousTestcaseResult(true);
    this.fetchNextTestcaseResults(true);
    this.scrollAndAttachEvent();
  }

  scrollActiveToView() {
    let index = this.testcaseResultArray.findIndex(testcaseResult => this.testCaseResult.id == testcaseResult.id)
    if ((this.testcaseResultArray.length - index) > 4) {
      index -= 1;
    } else if ((this.testcaseResultArray.length - index) < 3) {
      index += 3;
    }
    this.testCaseResultsViewPort && this.testCaseResultsViewPort.scrollToIndex(index, 'smooth');
  }

  showList() {
    this.showItems = !this.showItems;
    this.scrollActiveToView();
  }

  listenScrollIndexEvents() {
    this.testCaseResultsViewPort.scrolledIndexChange.pipe(debounceTime(200)).subscribe((scrollIndex) => {
      console.log(scrollIndex);
      if (scrollIndex > this.testcaseResultArray.length - 4 && !this.testcaseResultNextPage.last) {
        ++this.pageable.pageNumber;
        this.fetchNextTestcaseResults();
      } else if (scrollIndex < 4 && !this.testcaseResultPreviousPage.last) {
        ++this.pageable.pageNumber;
        this.fetchPreviousTestcaseResult();
      }
    });
  }

  fetchPreviousTestcaseResult(isInit?) {
    let previousQuery = "iteration:null,testPlanResultId:" +
      this.testCaseResult.testPlanResultId +
      ",environmentResultId:" + this.testCaseResult.environmentResultId +
      ",suiteResultId:" + this.testCaseResult.suiteResultId +
      ",id<" + this.testCaseResult.id;
    this.testCaseResultService.findAll(previousQuery, "id,desc", this.pageable).subscribe(res => {
      this.testcaseResultPreviousPage = res;
      this.testcaseResultArray.forEach(existingList => {
        res.content = res.content.filter(currentList =>
          currentList.id != existingList.id && !currentList.iteration)
      })
      if (isInit) {
        this.previousResults = new Page<TestCaseResult>();
        this.previousResults.content = res.content.reverse();
      }
      this.testcaseResultArray.unshift(...res.content);
      this.testCasesList = of(this.testcaseResultArray);
    });
  }

  fetchNextTestcaseResults(isInit?) {
    let nextQuery = "iteration:null,testPlanResultId:" +
      this.testCaseResult.testPlanResultId +
      ",environmentResultId:" + this.testCaseResult.environmentResultId +
      ",suiteResultId:" + this.testCaseResult.suiteResultId +
      ",id>" + this.testCaseResult.id;
    this.testCaseResultService.findAll(nextQuery, undefined, this.pageable).subscribe(res => {
      this.testcaseResultNextPage = res;
      this.testcaseResultArray.forEach(existingList => {
        res.content = res.content.filter(currentList => currentList.id != existingList.id && !currentList.iteration);
      })
      if (isInit) {
        this.nextResults = res;
      }
      this.testcaseResultArray.push(...res.content);
      this.testCasesList = of(this.testcaseResultArray);
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

  isTestCaseActive(testCaseResult: TestCaseResult) {
    if(testCaseResult.isDataDriven)
      return this.router.url.indexOf("test_case_results/" + this.testCaseResult.id) > 0 && testCaseResult.id == this.testCaseResult.parentResult?.id;
    else
      return this.router.url.indexOf("test_case_results/" + testCaseResult.id) > 0;
  }

  findFirstNonDataDrivenCase(content: TestCaseResult[], sort?: boolean){
    if(Boolean(sort))
      content.sort((a, b) => b.id - a.id);
    if(content[0]?.iteration)
      return content.find(testCaseResult => !testCaseResult.iteration && testCaseResult.id != this.testCaseResult?.parentResult.id);
    else
      return content[0]?.id || this.testCaseResult?.id;
  }
}
