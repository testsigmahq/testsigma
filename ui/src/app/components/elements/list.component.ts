import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from "../../shared/components/base.component";
import {ElementService} from "../../shared/services/element.service";
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {TooltipPosition} from "@angular/material/tooltip";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {ElementFormComponent} from "../webcomponents/element-form.component";
import {ElementFilterService} from "../../services/element-filter.service";
import {Page} from "../../shared/models/page";
import {ElementFilter} from "../../models/element-filter.model";
import {FilterableInfiniteDataSource} from "../../data-sources/filterable-infinite-data-source";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {AgentService} from "../../agents/services/agent.service";

import {Element} from "../../models/element.model";
import {AgentInfo} from "../../agents/models/agent-info.model";
import {ElementFiltersListComponent} from "../webcomponents/element-filters-list.component";
import {ElementFiltersComponent} from "../webcomponents/element-filters.component";
import {ElementFilterFormComponent} from "../webcomponents/element-filter-form.component";
import {ElementAddTagComponent} from "../webcomponents/element-add-tag.component";
import {ElementLocatorType} from "../../enums/element-locator-type.enum";
import {DetailsHeaderComponent} from "./details-header.component";
import {TestCaseService} from "../../services/test-case.service";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {ElementBulkUpdateComponent} from "../webcomponents/element-bulk-update.component";
import {MobileRecordingComponent} from "../../agents/components/webcomponents/mobile-recording.component";
import {MirroringData} from "../../agents/models/mirroring-data.model";
import * as moment from 'moment';
import {ChromeRecorderService} from "../../services/chrome-recoder.service";
import {ElementStatus} from "../../enums/element-status.enum";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
  styles: []
})
export class ElementsListComponent extends BaseComponent implements OnInit {
  @ViewChild('filterListBtn') public filterListBtn: ElementRef;
  public elements: InfiniteScrollableDataSource;
  public versionId: number;
  public version: WorkspaceVersion;
  public filters: Page<ElementFilter>;
  public filterId: number;
  public currentFilter: ElementFilter;
  public elementCapture: Boolean;
  public agentInstalled: Boolean = false;
  public filterDialogRef: MatDialogRef<ElementFiltersComponent, any>;
  public filterListDialogRef: MatDialogRef<ElementFiltersListComponent, any>;
  public filterFormDialogRef: MatDialogRef<ElementFilterFormComponent, ElementFilter>;
  public query: string;
  public selectAll: Boolean;
  public selectedElements = [];
  public sortByColumns = ["name", "createdDate", "updatedDate"];
  public direction = ",desc";
  public sortedBy = "createdDate";
  public tooltipPositionLeft: TooltipPosition = 'left';

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public elementService: ElementService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private matDialog: MatDialog,
    private workspaceVersionService: WorkspaceVersionService,
    private elementFilterService: ElementFilterService,
    private router: Router,
    public agentService: AgentService,
    public testCaseService: TestCaseService,
    public chromeRecorderService: ChromeRecorderService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get locatorType() {
    return Object.keys(ElementLocatorType).filter(type => {
      return (type == ElementLocatorType.accessibility_id ||
        type == ElementLocatorType.class_name ||
        type == ElementLocatorType.xpath ||
        type == ElementLocatorType.id_value ||
        type == ElementLocatorType.name)
    })
  }

  get submitReviewDisabled(): boolean{
    return Boolean(this.elements.cachedItems.filter(element => element['isSelected']).find(element =>
      element['status'] == ElementStatus.IN_REVIEW || element['createdById'] != this.authGuard?.user?.id
    ));
  }

  get reviewDisabled(): boolean{
    return Boolean(this.elements.cachedItems.filter(element => element['isSelected']).find(element =>
      element['status'] != ElementStatus.IN_REVIEW || element['reviewedById'] != this.authGuard?.user?.id
    ));
  }



  ngOnInit() {
    this.pushToParent(this.route, this.route.parent.parent.parent.snapshot.params);
    let queryKey = Object.keys(this.route.queryParams['value']);
    let query =  queryKey? queryKey[0]: null;
    this.versionId = this.route.parent.parent.parent.snapshot.params.versionId;
    this.fetchVersion();
    this.route.params.subscribe(res => {
      this.filterId = res.filterId;
      this.query = query? query : res.q ;
      if(query) this.navigateToQueryBasedUrl();
      this.fetchFilters();
    });
    setTimeout(() => this.chromeRecorderService.pingRecorder(), 200);
  }

  openDetails(element){
    this.selectAllToggle(false);
    const dialogRef = this.matDialog.open(DetailsHeaderComponent , {
      width: '60%',
      height: '100vh',
      data: {
        versionId: this.versionId,
        elementId: element.id
      },
      position: {top: '0', right: '0', bottom: '0'},
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe(result => {
        this.fetchElements();
      });
  }
  fetchVersion() {
    this.workspaceVersionService.show(this.versionId).subscribe(res => {
      this.version = res;
      if (this.version.workspace.isWebMobile) {
        this.chromeRecorderService.isChromeBrowser();
        //this.fetchChromeExtensionDetails();
        this.chromeRecorderService.pingRecorder();
      } else if (this.version.workspace.isMobileNative) {
        this.pingAgent();
      }
    });
  }


  hasInspectorFeature() {
    return (
      this.version && this.version.workspace.isAndroidNative)
    || (
      this.version && this.version.workspace.isIosNative
    );
  }

  pingAgent() {
    this.agentService.ping().subscribe((res: AgentInfo) => this.agentInstalled = res.isRegistered);
  }

  fetchElements() {
    let sortBy = this.sortedBy + this.direction;
    let query = ""
    if (this.query) {
      this.query +=  (this.currentFilter.normalizedQuery? this.currentFilter.queryString: '');
      query = this.byPassSpecialCharacters(this.query);
      this.query = query;
      if(!query.includes('workspaceVersionId:')) {
        query += ',workspaceVersionId:'+this.versionId;
      }
    }
    this.elements = new FilterableInfiniteDataSource(this.elementService, this.query, sortBy, 50, this.filterId, this.versionId);
    this.selectAllToggle(false);
  }


  openAddEditElement(elementId) {
    const dialogRef = this.matDialog.open(ElementFormComponent, {
      height: "100vh",
      width: '60%',
      position: {top: '0px', right: '0px'},
      data: {
        versionId: this.versionId,
        elementId: elementId
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        if(res && res instanceof MirroringData){
          this.matDialog.openDialogs?.find(dialog => dialog.componentInstance instanceof MobileRecordingComponent)
            ?.afterClosed().subscribe(r=>this.fetchElements());
        } else if (res)
          this.fetchElements();
      });

  }

  destroyElement(id) {
    this.elementService.destroy(id).subscribe(
      () => {
        this.translate.get('element.notification.delete.success')
          .subscribe(res => {
            this.showNotification(NotificationType.Success, res);
          });
        this.fetchElements();
        this.selectedElements = [];
      },
      (err) => {
        this.translate.get('element.notification.delete.failure')
          .subscribe(res => {
            this.showNotification(NotificationType.Success, res);
          })
      }
    );
  }

  checkForLinkedTestCases(element?) {
    let testCases: InfiniteScrollableDataSource;
    let query = "workspaceVersionId:" + this.versionId + ",deleted:false,element:" + encodeURI(element.name)
    query = this.byPassSpecialCharacters(query);
    testCases = new InfiniteScrollableDataSource(this.testCaseService,query);

    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testCases.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testCases.isEmpty)
          _this.openDeleteDialog(element);
        else
          _this.openLinkedTestCasesDialog(testCases);
      }
    }
  }

  openDeleteDialog(element?) {
    let message = element?.id ? "element.delete.confirmation.message" : "element.bulk_delete.confirmation.message";
    this.translate.get(message, {selectedElements: this.selectedElements.length}).subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            if (element?.id)
              this.destroyElement(element?.id);
            else
              this.multipleDelete()
          }
        });
    })
  }
  startCapture() {
    this.elementCapture = true;
    this.chromeRecorderService.recorderVersion = this.version;
    this.registerListener();
    this.chromeRecorderService.postGetElementMessage( true);
  }

  stopCapture() {
    this.chromeRecorderService.stopSpying();
    this.elementCapture = false;
    this.fetchElements();
  }

  registerListener() {
    this.chromeRecorderService.multiElementCallBack = this.saveMultipleElements;
    this.chromeRecorderService.multiElementCallBackContext = this;
  }

  saveMultipleUiIdentifiers(elements: Element[]) {
    if(elements?.length) {
      elements.forEach(element => element.workspaceVersionId = this.versionId);
      this.elementService.bulkCreate(elements).subscribe((res) => {
        this.translate.get('elements.multiple_save.success.message').subscribe(msg =>
          this.sendNotificationResponseToPlugin(msg, "SUCCESS")
        );
        this.fetchElements();
      }, error => {
        this.translate.get('elements.multiple_save.failure.message').subscribe(msg =>
          this.sendNotificationResponseToPlugin(msg)
        );
      });
    } else {
      this.elementCapture = false;
      this.fetchElements();
    }
  }

  sendNotificationResponseToPlugin(msg, status?: string) {
    this.translate.get(msg).subscribe(
      message => {
        let result = {};
        result["isListupdate"] = true;
        result["message"] = message;
        result['status'] = status
        this.chromeRecorderService.sendResponseElement(result);
      });
  }

  saveMultipleElements() {
    this.elementCapture = false;
    this.fetchElements();
  }

  discard() {
    this.query = undefined;
    this.router.navigate(['/td', this.version.id, 'elements', 'filter', this.currentFilter.id]);
    this.fetchElements();
  }

  saveViewAs() {
    this.openFilterForm(undefined);
  }

  saveView() {
    this.openFilterForm(this.currentFilter);
  }

  saveOrEditFilter() {
    this.openFilterForm(this.currentFilter);
  }

  openFiltersList() {
    this.filterListDialogRef = this.matDialog.open(ElementFiltersListComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      height: '100%',
      width: '364px',
      panelClass: ['mat-overlay', 'rds-none'],
      data: {list: this.filters, currentFilter: this.currentFilter, version: this.version}
    });

    const matDialogConfig = new MatDialogConfig();
    const rect: DOMRect = this.filterListBtn.nativeElement.getBoundingClientRect();
    matDialogConfig.position = {left: `${rect.left - 42}px`, top: `${rect.top}px`}
    this.filterListDialogRef.updatePosition(matDialogConfig.position);
  }

  openFilter() {
    this.filterDialogRef = this.matDialog.open(ElementFiltersComponent, {
      width: '25%',
      height: '100vh',
      position: {top: '0', right: '0', bottom: '0'},
      panelClass: ['mat-overlay'],
      data: {version: this.version, filter: this.currentFilter, query: this.query}
    });
    this.filterDialogRef.componentInstance.filterAction.subscribe(query => {
      if (query) {
        this.query = query;
        this.navigateToQueryBasedUrl();
        this.fetchElements();
      } else
        this.discard();
    });
  }

  navigateToQueryBasedUrl() {
    let routerUrl:String = this.route.snapshot['_routerState'].url;
    routerUrl = routerUrl.includes('?') ? routerUrl.slice(0, routerUrl.indexOf('?')) : routerUrl;
    this.router.navigateByUrl(routerUrl+ '?'+this.query);
  }

  openFilterForm(filter: ElementFilter) {
    this.filterFormDialogRef = this.matDialog.open(ElementFilterFormComponent, {
      width: '40%',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {query: this.query, version: this.version, filter: filter}
    });

    this.filterFormDialogRef.afterClosed().subscribe((result: ElementFilter) => {
      if (result) {
        this.filterId = result.id;
        this.query = undefined;
        this.currentFilter = result;
        this.router.navigate(['/td', this.version.id, 'elements', 'filter', result.id]);
        this.fetchFilters();
      }
    });
  }

  deleteFilter() {
    this.elementFilterService.destroy(this.currentFilter.id).subscribe(() => {
      this.translate.get('filter.delete.success', {name: this.currentFilter.name})
        .subscribe((res) => this.showNotification(NotificationType.Success, res));
      this.filters.content = this.filters.content.filter(filter => filter.id != this.currentFilter.id)
      this.currentFilter = this.filters.content[0];
      this.filterId = this.currentFilter.id;
      this.router.navigate(['/td', this.version.id, 'elements', 'filter', this.currentFilter.id]);;
      this.fetchFilters();
    });
  }

  selectAllToggle(select) {
    let elements = this.elements['cachedItems'];
    if(!select){
      this.selectedElements = [];
      this.selectAll = false;
    }
    for (let i = 0; i < elements.length; i++) {
      if (select && this.selectedElements.indexOf(elements[i]["id"]) == -1) {
        this.selectedElements.push(elements[i]["id"]);
        this.elements['cachedItems'][i]['isSelected'] = true;
      } else if (!select) {
        this.elements['cachedItems'][i]['isSelected'] = false;
      }
    }
  }

  setSelectedList(id, isSelected: Boolean) {
    if (isSelected)
      this.selectedElements.push(id);
    else
      this.selectedElements.splice(this.selectedElements.indexOf(id), 1);
  }

  multipleDelete() {
    this.elementService.bulkDestroy(this.selectedElements, this.versionId).subscribe(
      () => {
        this.translate.get("element.notification.bulk_delete.success").subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchElements();
        this.selectAllToggle(false);
      },
      (err) => {
        this.translate.get("element.notification.bulk_delete.failure").subscribe(res => this.showAPIError(err, res,"Elements","Test Case"));
        this.fetchElements();
        this.selectAllToggle(false);
      })
  }

  addLabelsDialog(id) {
    this.matDialog.open(ElementAddTagComponent, {
      width: '450px',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        elementId: id,
        versionId: this.version
      }
    });
  }

  bulkUpdateDialog(ids) {
    let bulkUpdateComponent = this.matDialog.open(ElementBulkUpdateComponent, {
      width: '450px',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        elementIds: ids,
        versionId: this.version
      }
    });
    bulkUpdateComponent.afterClosed().subscribe(res => {
      if(res){
        this.fetchElements();
        this.selectAllToggle(false);
      }
    });
  }

  sortBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.fetchElements();
  }

  private fetchFilters() {
    this.elementFilterService.findAll(this.versionId).subscribe(res => {
      this.filters = res;
      this.currentFilter = this.filters.content.find(filter => filter.id == this.filterId);
      this.fetchElements();
    });
  }

  private openLinkedTestCasesDialog(list) {
    this.translate.get("elements_linked_with_cases").subscribe((res) => {
      this.matDialog.open(LinkedEntitiesModalComponent, {
        width: '568px',
        height: 'auto',
        data: {
          description: res,
          linkedEntityList: list,
        },
        panelClass: ['mat-dialog', 'rds-none']
      });
    });
  }

  filterByScreenName(screenName:string) {
    let sortBy = this.sortedBy + this.direction;
    let encodedScreenName = encodeURIComponent(screenName)
    this.query = "workspaceVersionId:" + this.version.id + ",screenName:" + encodedScreenName ;
    this.navigateToQueryBasedUrl();
    let encodedQuery = ",workspaceVersionId:" + this.version.id + ",screenName:" + encodedScreenName;
    if(this.query===undefined)
      this.query = ",workspaceVersionId:" + this.version.id + ",screenName:" + screenName;
    else if(this.query.indexOf("screenName")==-1)
      this.query += ",workspaceVersionId:" + this.version.id + ",screenName:" + screenName;
    else if(this.router.url.indexOf(encodedScreenName)!==-1)
      this.translate.get("element.name.already.filtered").subscribe((res: string) => {
        this.showNotification(NotificationType.Error, res);
      });
    this.elements = new FilterableInfiniteDataSource(this.elementService, encodedQuery, sortBy, 50,1, this.versionId);
  }

  humanizedDate(date) {
    return moment.duration(moment().diff(date)).humanize() + ' ago';
  }
}
