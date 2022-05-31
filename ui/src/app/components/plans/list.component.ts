import {Component, OnInit} from '@angular/core';
import {Page} from "../../shared/models/page";
import {Pageable} from "../../shared/models/pageable";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute, Params} from '@angular/router';
import {TestPlanService} from "../../services/test-plan.service";
import {TestPlan} from "../../models/test-plan.model";
import {TestDeviceService} from "../../services/test-device.service";
import {TestPlanType} from "../../enums/execution-type.enum";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
  styles: []
})
export class TestPlanListComponent extends BaseComponent implements OnInit {
  public testPlans: Page<TestPlan>;
  public currentPage: Pageable = new Pageable();
  public versionId: number;
  public searchQuery = "";
  public isFiltered: boolean;
  public fetchingCompleted: Boolean = false;
  public isSearchEnable: boolean;
  public sortByColumns = ['name'];
  public sortedBy: string = 'name';
  public direction: string = ",asc";


  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private testPlanService: TestPlanService,
    private testDeviceService: TestDeviceService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.route.parent.parent.params.subscribe((params: Params) => {
      this.versionId = params.versionId;
      this.pushToParent(this.route, params);
      this.fetchTestPlans();
    });

  }

  getCrossBrowser(){
    return TestPlanType.CROSS_BROWSER
  }

  fetchTestPlans(search?: string, pageable?: Pageable) {
    this.fetchingCompleted = false;
    let query = "workspaceVersionId:" + this.versionId + (search ? search : '');
    this.testPlanService.findAll(query, this.sortedBy + this.direction, pageable || this.currentPage)
      .subscribe(res => {
        this.testPlans = res;
        this.currentPage = res.pageable;
        if(this.testPlans?.content?.length)
          this.fetchExecutionEnvironments();
        else
          this.fetchingCompleted = true;
      });
  }

  sortBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.fetchTestPlans(this.searchQuery);
  }


  fetchExecutionEnvironments() {
    let pageable = new Pageable();
    pageable.pageSize = 200;
    let query = "testPlanId@" + this.testPlans.content.map((exe) => exe.id).join("#");
    this.testDeviceService.findAll(query, undefined, pageable).subscribe((environments) => {
      this.testPlans.content.forEach((exe) => {
        let filteredEnvs = environments.content.filter((exeEnv) => exeEnv.testPlanId === exe.id);
        if (filteredEnvs)
          exe.testDevices = filteredEnvs;
      });
      this.fetchingCompleted = true;
    })
  }


  search(term: string) {
    if (term) {
      this.isFiltered = true;
      this.searchQuery = ",name:*" + term + "*";
    } else {
      this.isFiltered = false;
      this.searchQuery = "";
    }
    this.fetchTestPlans(this.searchQuery)
  }
}
