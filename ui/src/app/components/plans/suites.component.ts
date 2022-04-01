import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {TestSuiteService} from "../../services/test-suite.service";
import {ActivatedRoute} from '@angular/router';
import {fromEvent} from 'rxjs/internal/observable/fromEvent';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {TestPlanAddSuiteFormComponent} from "../webcomponents/test-plan-add-suite-form.component";
import {MatDialog} from '@angular/material/dialog';
import {TestDevice} from "../../models/test-device.model";
import {TestPlanService} from "../../services/test-plan.service";
import {TestPlan} from "../../models/test-plan.model";
import {TestDeviceService} from "../../services/test-device.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {TestSuite} from "../../models/test-suite.model";
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {Page} from "../../shared/models/page";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {Pageable} from "../../shared/models/pageable";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-suites-list',
  templateUrl: './suites.component.html',
  styles: [],
  host: {
    'class': 'd-flex ts-col-100 h-100'
  }
})
export class SuitesComponent extends BaseComponent implements OnInit {
  public testSuites: Page<TestSuite>;
  public testPlanId: number;
  @ViewChild('searchInput', {static: true}) searchInput: ElementRef;
  public isFetching: boolean = false;
  private testPlan: TestPlan;
  private executionEnvironment = new TestDevice();
  private executionEnvironments: TestDevice[];
  public suitesList: InfiniteScrollableDataSource;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private testSuiteService: TestSuiteService,
    private executionEnvironmentService: TestDeviceService,
    private testPlanService: TestPlanService,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.testPlanId = this.route.parent.snapshot.params.testPlanId;
    this.fetchSuites();
    this.fetchExecution();
    this.attachSearchEvents();
  }

  fetchSuites(term?: string) {
    this.isFetching = true
    let query = "testPlanId:" + this.testPlanId;
    if (term)
      query += ",name:*" + term + "*";
    this.suitesList = new InfiniteScrollableDataSource(this.testSuiteService, query);
    this.isFetching = false;
  }

  attachSearchEvents() {
    if (this.searchInput) {
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((_event: KeyboardEvent) => {
            this.fetchSuites(this.searchInput.nativeElement.value);
          })
        )
        .subscribe();
    } else {
      setTimeout(() => this.attachSearchEvents(), 100);
    }
  }

  selectSuites() {
    let page = new Pageable();
    page.pageSize = 1000;
    this.testSuiteService.findAll("testPlanId:" + this.testPlanId, undefined, page).subscribe(res => {
      this.executionEnvironment.testSuites = res.content;
      this.matDialog.open(TestPlanAddSuiteFormComponent, {
        width: '65vw',
        height: '85vh',
        data: {
          executionEnvironment: this.executionEnvironment,
          version: this.testPlan.workspaceVersion,
          execution: this.testPlan
        },
        panelClass: ['mat-dialog', 'full-width', 'rds-none']
      }).afterClosed().subscribe(res => {
        if (res) {
          this.updateSuites();
        }
      });
    })
  }

  drop(event: CdkDragDrop<TestSuite[]>) {
    if (event.previousIndex != event.currentIndex) {
      moveItemInArray(this.testSuites.content, event.previousIndex, event.currentIndex);
      this.executionEnvironment.testSuites = this.testSuites.content;
      this.updateSuites();
    }
  }

  private fetchExecution() {
    this.testPlanService.find(this.route.parent.snapshot.params.testPlanId).subscribe(res => {
      this.testPlan = res;
      this.fetchEnvironments();
    });
  }

  private fetchEnvironments() {
    let query = "testPlanId:" + this.testPlan.id;
    this.executionEnvironmentService.findAll(query).subscribe(res => {
      this.executionEnvironments = res.content;
      this.testPlan.environments = this.executionEnvironments
    });
  }

  private updateSuites() {
    let suiteIds =this.executionEnvironment.testSuites.map(suite => suite.id);
    for(let i in this.testPlan.environments)
      this.testPlan.environments[i].suiteIds =suiteIds;
    this.testPlanService.update(this.testPlan).subscribe(
      () => {
        this.translate.get('message.common.update.success', {FieldName: 'Test Suites'})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchSuites();
      },
      err => {
        this.translate.get('message.common.update.failure', {FieldName: 'Test Suites'})
          .subscribe(res => this.showNotification(NotificationType.Error, res));
      }
    );
  }
}
