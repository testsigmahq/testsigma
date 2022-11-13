import {Component, OnInit} from '@angular/core';
import {TestSuite} from "../../models/test-suite.model";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {TestSuiteService} from "../../services/test-suite.service";
import {ActivatedRoute, Router} from '@angular/router';

import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {MatDialog} from '@angular/material/dialog';
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestCaseService} from "../../services/test-case.service";
import {ToastrService} from "ngx-toastr";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {TestPlanService} from "../../services/test-plan.service";
import {EntityType} from "../../enums/entity-type.enum";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {EntityExternalMappingService} from "../../services/entity-external-mapping.service";
import {XrayKeyWarningComponent} from "../../agents/components/webcomponents/xray-key-warning-component";

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  host: {'class': 'page-content-container flex-wrap'}
})
export class DetailsComponent extends BaseComponent implements OnInit {
  suite = new TestSuite();
  activeTab: string;
  public testSuiteId: number;
  public versionId: number;
  public testCases: InfiniteScrollableDataSource;
  public entityType: EntityType = EntityType.TEST_SUITE;
  public entityExternalMapping: EntityExternalMapping;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testSuiteService: TestSuiteService,
    private testPlanService: TestPlanService,
    private route: ActivatedRoute,
    private router: Router,
    private testCaseService: TestCaseService,
    private matDialog: MatDialog,
    public entityExternalMappingService : EntityExternalMappingService) {
    super(authGuard, notificationsService, translate,toastrService);
  }

  ngOnInit(): void {
    this.testSuiteId = this.route.snapshot.params.testSuiteId;
    this.pushToParent(this.route, this.route.snapshot.params);
    this.fetchTestsuiteDetails();
    this.fetchTestCases();
  }

  fetchTestsuiteDetails() {
    this.testSuiteService.show(this.testSuiteId).subscribe(res => {
      this.suite = res;
      this.versionId = this.suite.workspaceVersionId;
    });
  }

  fetchTestCases() {
    this.testCases =new InfiniteScrollableDataSource(this.testCaseService, "suiteId:" + this.testSuiteId, undefined);
  }

  openDeleteDialog() {
    this.translate.get('message.common.confirmation.default').subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res,
          isPermanentDelete: true,
          title: 'Test Suite',
          item: 'test suite',
          name: this.suite.name,
          note: this.translate.instant('message.common.confirmation.test_plan_des', {Item:'test suite'})
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result)
            this.destroyTestSuite(this.suite.id);
        });
    })
  }

  showWarning(mapping: EntityExternalMapping){
    const dialogRef = this.matDialog.open(XrayKeyWarningComponent, {
      width: '450px',
      panelClass: ['matDialog', 'rds-none'],
      data: {entityType: this.entityType}
    });
    dialogRef.afterClosed().subscribe(res =>{
      if(res===false){
        this.checkAllTestCasesAreLinked(mapping);
      }
    })
  }

  linkXrayWithTestCase(mapping: EntityExternalMapping){
    this.entityExternalMappingService.create(mapping).subscribe(
      (res) => {
        this.entityExternalMapping = res;
        this.translate.get("message.common.xray_link.success", {EntityName: "Test Suite" }).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
        });
      },
      (error) => {
        if (error.status == "400") {
          this.showNotification(NotificationType.Error, error.error);
        } else {
          this.translate.get("message.common.xray_link.failure", {EntityName: "Test Suite" }).subscribe((res: string) => {
            this.showNotification(NotificationType.Error, res);
          });
        }
      }
    )
  }

  linkXrayId(mapping: EntityExternalMapping){
    this.entityExternalMappingService.findAll("externalId:"+mapping.externalId).subscribe(res =>{
      if(res.content.length > 0 && res.content[0].entityId!=this.testSuiteId){
        this.showWarning(mapping);
      }else{
        this.checkAllTestCasesAreLinked(mapping);
      }
    })
  }

  checkAllTestCasesAreLinked(mapping : EntityExternalMapping){
    let ids: number[] = [];
    this.testCaseService.findAll("suiteId:" + this.testSuiteId).subscribe(res=>{
      res.content.forEach((testCase)=> {ids.push(testCase.id)});
      this.entityExternalMappingService.checkAllEntitiesAreLinked(ids, EntityType.TEST_CASE).subscribe(
        (res) =>{
          this.checkPreRequisiteLinkedToXray(mapping);
        },
        (err)=>{
          this.translate.get("message.common.xray_link.pre_failure", {EntityName: "test cases" }).subscribe((res: string) => {
            this.showNotification(NotificationType.Warn, res);
            this.checkPreRequisiteLinkedToXray(mapping);
          });
        }
      );
    })
  }

  checkPreRequisiteLinkedToXray(mapping: EntityExternalMapping){
    this.testSuiteService.show(this.testSuiteId).subscribe(res =>{
      if(res?.preRequisiteSuite){
        this.entityExternalMappingService.findAll("entityId:"+ res?.preRequisiteSuite.id).subscribe(res =>{
          if(res.content.length == 0){
            this.translate.get("message.common.prerequisite_not_linked", {EntityName: "test suite" }).subscribe((res: string) => {
              this.showNotification(NotificationType.Warn, res);
            });
          }
        })
      }
      this.linkXrayWithTestCase(mapping);
    })
  }

  private destroyTestSuite(id: any) {
    this.testSuiteService.destroy(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Test Suite"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', this.versionId, 'suites'])
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: "Test Suite"})
        .subscribe(res => this.showNotification(NotificationType.Error, res))
    );
  }

  public fetchLinkedPlans() {
    let testPlans: InfiniteScrollableDataSource;
    testPlans = new InfiniteScrollableDataSource(this.testPlanService, "suiteId:" + this.testSuiteId , "name,asc" );
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testPlans.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testPlans.isEmpty)
          _this.openDeleteDialog();
        else
          _this.openLinkedTestPlansDialog(testPlans);
      }
    }
  }

  private openLinkedTestPlansDialog(list) {
    this.translate.get("suite.linked_with_test_plans").subscribe((res) => {
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

}
