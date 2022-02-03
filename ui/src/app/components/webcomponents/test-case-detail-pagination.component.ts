import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Page} from "../../shared/models/page";
import {TestCaseService} from "../../services/test-case.service";
import {TestCase} from "../../models/test-case.model";
import {CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import {Pageable} from "../../shared/models/pageable";
import {Observable, of} from 'rxjs';
import {debounceTime} from 'rxjs/operators';
import {Router} from '@angular/router';

@Component({
  selector: 'app-test-case-detail-pagination',
  templateUrl: './test-case-detail-pagination.component.html',
  styles: []
})
export class TestCaseDetailPaginationComponent implements OnInit {
  @Input('testCase') testCase: TestCase;
  @Input('versionId') versionId: number;

  public showItems: Boolean = false;
  public previousResults: Page<TestCase>;
  public nextResults: Page<TestCase>;
  public testCaseList: Observable<TestCase[]>;
  public testCaseArray: TestCase[] = [];
  public testCasePreviousPage: Page<TestCase>;
  public testCaseNextPage: Page<TestCase>;
  private pageable: Pageable = new Pageable();

  @ViewChild('testCaseDetailsViewPort') public testCaseDetailsViewPort: CdkVirtualScrollViewport;

  constructor(
    private testCaseService: TestCaseService,
    public router: Router) {
  }

  ngOnInit() {
  }

  ngOnChanges() {
    this.showItems = false;
    this.pageable.pageNumber = 0;

    this.testCaseArray = [this.testCase];
    this.testCaseList = of(this.testCaseArray);
    this.fetchPreviousTestcaseResult(true);
    this.fetchNextTestcaseResults(true);
    this.scrollAndAttachEvent();
  }

  scrollActiveToView() {
    let index = this.testCaseArray.findIndex(environmentResult => this.testCase.id == environmentResult.id)
    if ((this.testCaseArray.length - index) > 4) {
      index -= 1;
    } else if ((this.testCaseArray.length - index) < 3) {
      index += 3;
    }
    this.testCaseDetailsViewPort && this.testCaseDetailsViewPort.scrollToIndex(index, 'smooth');
  }

  showList() {
    this.showItems = !this.showItems;
    this.scrollActiveToView();
  }

  listenScrollIndexEvents() {
    this.testCaseDetailsViewPort.scrolledIndexChange.pipe(debounceTime(200)).subscribe((scrollIndex) => {
      console.log(scrollIndex);
      if (scrollIndex > this.testCaseArray.length - 4 && !this.testCaseNextPage.last) {
        ++this.pageable.pageNumber;
        this.fetchNextTestcaseResults();
      } else if (scrollIndex < 4 && !this.testCasePreviousPage.last) {
        ++this.pageable.pageNumber;
        this.fetchPreviousTestcaseResult();
      }
    });
  }

  fetchPreviousTestcaseResult(isInit?) {
    let previousQuery = "workspaceVersionId:" + this.versionId + ",deleted:false,id<" + this.testCase.id;
    this.testCaseService.findAll(previousQuery, "id,desc", this.pageable).subscribe(res => {
      this.testCasePreviousPage = res;
      if (isInit) {
        this.previousResults = new Page<TestCase>();
        this.previousResults.content = res.content.reverse();
      }
      this.testCaseArray.unshift(...res.content);
      this.testCaseList = of(this.testCaseArray);
    });
  }

  fetchNextTestcaseResults(isInit?) {
    let nextQuery = "workspaceVersionId:" + this.versionId + ",deleted:false, id>" + this.testCase.id;
    this.testCaseService.findAll(nextQuery, undefined, this.pageable).subscribe(res => {
      this.testCaseNextPage = res;
      if (isInit) {
        this.nextResults = res;
      }
      this.testCaseArray.push(...res.content);
      this.testCaseList = of(this.testCaseArray);
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
