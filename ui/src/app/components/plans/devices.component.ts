import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {TestDeviceService} from "../../services/test-device.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {ActivatedRoute} from '@angular/router';
import {fromEvent} from 'rxjs/internal/observable/fromEvent';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {TestPlanService} from "../../services/test-plan.service";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestDevice} from "../../models/test-device.model";
import {TestSuiteService} from "../../services/test-suite.service";
import {TestPlan} from "../../models/test-plan.model";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {TestPlanTestMachineSelectFormComponent} from "../webcomponents/test-plan-test-machine-select-form.component";
import {MatDialog} from '@angular/material/dialog';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-devices-list',
  templateUrl: './devices.component.html',
  host: {
    'class': 'd-flex ts-col-100 h-100'
  }
})
export class DevicesComponent extends BaseComponent implements OnInit {
  public executionEnvironments: InfiniteScrollableDataSource;
  public version: WorkspaceVersion;
  public testPlan: TestPlan;
  public activeEnvironment: TestDevice;
  @ViewChild('searchInput', {static: true}) searchInput: ElementRef;
  private testPlanId: number;
  public isEnvironmentAvailable = true;

  constructor(
    private testSuiteService: TestSuiteService,
    private route: ActivatedRoute,
    private testPlanService: TestPlanService,
    private versionService: WorkspaceVersionService,
    private executionEnvironmentService: TestDeviceService,
    private matDialog: MatDialog,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public toastrService: ToastrService,
    public translate: TranslateService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get isMobile() {
    return this.version?.workspace?.isMobile;
  }

  get isCustomPlan() {
    return this.testPlan?.isCustomPlan;
  }

  ngOnInit(): void {
    this.testPlanId = this.route.parent.snapshot.params.testPlanId;
    this.fetchEnvironments();
    this.attachSearchEvents();
  }

  fetchExecution() {
    this.testPlanService.find(this.testPlanId).subscribe(res => {
      this.testPlan = res;
      this.versionService.show(res.workspaceVersionId).subscribe(version => this.version = version);
    })
  }

  fetchEnvironments(term?: string) {
    let query = "testPlanId:" + this.testPlanId;
    if (term)
      query += ",title:*" + term + "*";
    this.executionEnvironments = new InfiniteScrollableDataSource(this.executionEnvironmentService, query, undefined, 50);
    this.environmentEnableCount()
    this.fetchExecution();
  }

  attachSearchEvents() {
    if (this.searchInput) {
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            this.fetchEnvironments(this.searchInput.nativeElement.value);
          })
        )
        .subscribe();
    } else {
      setTimeout(() => this.attachSearchEvents(), 100);
    }
  }

  expand(environment: TestDevice) {
    this.activeEnvironment = environment;
    if (environment.testSuites?.length)
      return;
    this.testSuiteService.findAll("testDeviceId:" + environment.id)
      .subscribe(res => environment.testSuites = res.content);
  }

  openSelectTestMachineForm(environment?, isEdit?:boolean) {
    this.matDialog.open(TestPlanTestMachineSelectFormComponent, {
      width: this.testPlan.workspaceVersion.workspace.isWeb ? '875px' : 'auto',
      height: '100vh',
      data: {testPlan: this.testPlan, executionEnvironment: environment, isEdit: isEdit},
      position: {top: '0px', right: '0px'},
      panelClass: ['mat-dialog', 'rds-none']
    }).afterClosed().subscribe((res: TestDevice) => {
      if (res) {
        if (  isEdit) {
          let editedEnvironmentIndex = this.executionEnvironments['cachedItems'].indexOf(environment);
          this.executionEnvironments['cachedItems'][editedEnvironmentIndex] = res;
        } else {
          this.executionEnvironments['cachedItems'].push(res);
        }
        this.updateEnvironments()
      }
    });
  }

  deleteEnvironment(environment) {
    this.executionEnvironments['cachedItems'].splice(this.executionEnvironments['cachedItems'].indexOf(environment), 1);
    this.testPlan.testDevices = <TestDevice[]>this.executionEnvironments['cachedItems'];
    this.updateEnvironments(true);
  }

  updateEnvironments(isDelete?) {
    this.testPlan.testDevices = <TestDevice[]>this.executionEnvironments['cachedItems'];
    let fieldName = this.testPlan.workspaceVersion.workspace.isWeb ?'Test Machine':'Test Device';
    let typeOfOperation;
    if (isDelete){
      typeOfOperation = 'deleted';
    } else {
      fieldName += 's';
      typeOfOperation = 'update';
    }
    this.testPlanService.update(this.testPlan).subscribe(() => {
      this.translate.get('message.common.' + typeOfOperation +'.success', {FieldName: fieldName})
        .subscribe(res => this.showNotification(NotificationType.Success, res));
      this.fetchEnvironments();
    });
  }


  toggleMachineEnable(environment: TestDevice) {
    environment.disable = !environment.disable;
    let editedEnvironmentIndex = this.executionEnvironments['cachedItems'].indexOf(environment);
    this.executionEnvironments['cachedItems'][editedEnvironmentIndex] = environment;
    this.updateEnvironments();
  }

  environmentEnableCount() {
    this.executionEnvironmentService.findAll("disable:false,testPlanId:" + this.testPlanId).subscribe(res => {
      this.isEnvironmentAvailable = res.totalElements >= 2;
    })
  }
}
