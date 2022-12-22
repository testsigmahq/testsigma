import {Component, OnInit} from '@angular/core';
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanService} from "../../services/test-plan.service";
import {ActivatedRoute, Router} from '@angular/router';
import {EnvironmentService} from "../../services/environment.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import { MatDialog } from '@angular/material/dialog';
import {ToastrService} from "ngx-toastr";
import {EntityType} from "../../enums/entity-type.enum";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {EntityExternalMappingService} from "../../services/entity-external-mapping.service";
import {XrayKeyWarningComponent} from "../../agents/components/webcomponents/xray-key-warning-component";
import {TestSuiteService} from "../../services/test-suite.service";

@Component({
  selector: ' app-details-header',
  templateUrl: './details-header.component.html',
  styles: []
})
export class DetailsHeaderComponent extends BaseComponent implements OnInit {
  public testPlan: TestPlan;
  public testPlanId: number;
  public version: WorkspaceVersion;
  public isFetchingCompleted: Boolean;
  public entityType: EntityType = EntityType.TEST_PLAN;
  public entityExternalMapping: EntityExternalMapping;

  constructor(
    private testPlanService: TestPlanService,
    private environmentService: EnvironmentService,
    private versionService: WorkspaceVersionService,
    private route: ActivatedRoute,
    private matModal: MatDialog,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    public entityExternalMappingService : EntityExternalMappingService,
    public testSuiteService: TestSuiteService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.testPlanId = this.route.snapshot.params.testPlanId;
    this.pushToParent(this.route, this.route.snapshot.params);
    this.fetchTestPlan();
  }

  fetchTestPlan() {
    this.testPlanService.find(this.testPlanId).subscribe(res => {
      this.testPlan = res;
      this.fetchVersion();
      this.isFetchingCompleted = true;
      if (this.testPlan.environmentId)
        this.fetchEnvironment();
    });
  }

  fetchVersion() {
    this.versionService.show(this.testPlan.workspaceVersionId).subscribe(res => this.version=res);
  }

  fetchEnvironment() {
    this.environmentService.show(this.testPlan.environmentId).subscribe(res => this.testPlan.environment = res);
  }

  deleteConfirmation() {
    this.translate.get("message.common.confirmation.message", {FieldName: 'Test Plan'}).subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res,
          isPermanentDelete: true,
          title: 'Test Plan',
          item: 'test plan',
          name: this.testPlan.name,
          note: this.translate.instant('message.common.confirmation.test_plan_des', {Item:'test plan'})
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result)
          this.delete()
      });
    })
  }

  delete() {
    this.testPlanService.destroy(this.testPlan.id).subscribe(()=> {
      this.translate.get('message.common.deleted.success', {FieldName: 'Test Plan'})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
      this.router.navigate(['/td', this.testPlan.workspaceVersionId, 'plans']);
    })
  }

  linkXrayId(mapping: EntityExternalMapping){
    this.entityExternalMappingService.findAll("externalId:"+mapping.externalId).subscribe(res =>{
      if(res.content.length > 0 && res.content[0].entityId!=this.testPlanId){
        this.showWarning(mapping);
      }else{
        this.checkAllTestCasesAreLinked(mapping);
      }
    })
  }

  checkAllTestCasesAreLinked(mapping : EntityExternalMapping){
    let ids: number[] = [];
    let checkPass = false;
    this.testSuiteService.findAll("executionId:" + this.testPlanId).subscribe(res=>{
      res.content.forEach((testSuite)=> {ids.push(testSuite.id)});
      this.entityExternalMappingService.checkAllEntitiesAreLinked(ids, EntityType.TEST_SUITE).subscribe(
        (res) =>{
          this.linkXrayWithTestCase(mapping);
        },
        (err)=>{
          this.translate.get("message.common.xray_link.pre_failure", {EntityName: "Test Suites" }).subscribe((res: string) => {
            this.showNotification(NotificationType.Alert, res);
            this.linkXrayWithTestCase(mapping);
          });
        }
      );
    })
  }

  showWarning(mapping: EntityExternalMapping){
    const dialogRef = this.matModal.open(XrayKeyWarningComponent, {
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
}
