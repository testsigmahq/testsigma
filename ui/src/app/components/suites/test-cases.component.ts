import {Component, OnInit} from '@angular/core';
import {TestCaseService} from "../../services/test-case.service";
import {ActivatedRoute, Router} from '@angular/router';
import {TestCase} from 'app/models/test-case.model';
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestSuiteAddCaseFormComponent} from "../webcomponents/test-suite-add-case-form.component";
import {MatDialog} from '@angular/material/dialog';
import {TestSuiteService} from "../../services/test-suite.service";
import {TestSuite} from "../../models/test-suite.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {TestCaseStatus} from "../../enums/test-case-status.enum";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {Pageable} from "../../shared/models/pageable";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-suite-test-case',
  templateUrl: './test-cases.component.html',
  host: {'class': 'page-content-container'},
})
export class TestCasesComponent extends BaseComponent implements OnInit {
  public testCases: InfiniteScrollableDataSource;
  private testSuiteId: number;
  private testSuite: TestSuite;
  private version: WorkspaceVersion;
  private activeTestCase: TestCase[]
  public currentPage = new Pageable();

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testCaseService: TestCaseService,
    private route: ActivatedRoute,
    private matDialog: MatDialog,
    private router: Router,
    private testSuiteService: TestSuiteService,
    private workspaceVersionService: WorkspaceVersionService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.testSuiteId = this.route.parent.snapshot.params.testSuiteId;
    this.pushToParent(this.route, this.route.parent.snapshot.params);
    this.fetchTestCases();
    this.fetchTestsuite()
  }

  fetchTestCases() {
    this.testCases = new InfiniteScrollableDataSource(this.testCaseService, "suiteId:" + this.testSuiteId, undefined);
    this.currentPage.pageSize = 1000;
    this.testCaseService.findAll("suiteId:" + this.testSuiteId, '', this.currentPage).subscribe(res => {
      this.activeTestCase = res.content;
    })
  }

  fetchTestsuite() {
    this.testSuiteService.show(this.testSuiteId).subscribe(res => {
      this.testSuite = res;

      this.workspaceVersionService.show(res.workspaceVersionId).subscribe((res) => {
        this.version = res;
      })
    })
  }

  addSuites() {
    this.matDialog.open(TestSuiteAddCaseFormComponent, {
      width: '85vw',
      height: '90vh',
      data: {
        versionFilter: "workspaceVersionId:" + this.version.id,
        applicationFilter: "workspaceId:" + this.version.workspaceId,
        allTestCasesFilter: "workspaceVersionId:" + this.version.id + ",status:"+TestCaseStatus.READY+",deleted:false,isStepGroup:false",
        activeTestCases: this.activeTestCase,
      },
      panelClass: ['mat-dialog', 'full-width', 'rds-none']
    }).afterClosed().subscribe((res: TestCase[]) => {
      if (res?.length) {
        this.testSuite.testCaseIds = res.map(testCase => testCase.id);
        this.testSuiteService.update(this.testSuite).subscribe(res => {
          const shouldReuseMethod = this.router.routeReuseStrategy.shouldReuseRoute;
          this.router.routeReuseStrategy.shouldReuseRoute = () => false;
          this.router.onSameUrlNavigation = 'reload';
          this.router.navigate(['/td', 'suites', this.testSuiteId, 'cases']);
          setTimeout(() => {
            this.router.navigate(['/td', 'suites', this.testSuiteId, 'cases']);
            this.router.routeReuseStrategy.shouldReuseRoute = shouldReuseMethod;
            this.router.onSameUrlNavigation = 'ignore';
          }, 100);
        })
      }
    });
  }

}
