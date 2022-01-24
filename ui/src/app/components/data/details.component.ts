import {Component, OnInit} from '@angular/core';
import {TestDataService} from "../../services/test-data.service";
import {TestData} from "../../models/test-data.model";
import {ActivatedRoute, Router} from '@angular/router';
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {MatDialog} from '@angular/material/dialog';

import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {TestCaseService} from "../../services/test-case.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-data-view',
  templateUrl: './details.component.html'
})
export class DetailsComponent extends BaseComponent implements OnInit {
  public testData: TestData;
  public versionId: number;
  public testDataId: number;
  public isFetchCompleted: Boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testDataService: TestDataService,
    private testCaseService: TestCaseService,
    private route: ActivatedRoute,
    private router: Router,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.pushToParent(this.route, this.route.snapshot.params);
    this.testDataId = this.route.snapshot.params.testDataId;
    this.fetchTestData();
  }

  private fetchTestData() {
    this.testDataService.show(this.testDataId).subscribe(res => {
      this.testData = res;
      this.isFetchCompleted = true;
      this.versionId = this.testData.versionId;
    });
  }

  openDeleteDialog() {
    this.translate.get("message.common.confirmation.default").subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {description: res},
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result)
            this.destroyDataProfile();
        });
    })
  }

  private destroyDataProfile() {
    this.testDataService.delete(this.testDataId).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Test Data Profile"})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
        this.router.navigate(['/td', this.versionId, 'data']);
      },
      (err) => {
        this.translate.get('message.common.deleted.failure', {FieldName: "Test Data Profile"})
          .subscribe(res => this.showAPIError(err, res))
      }
    );
  }

  public fetchLinkedCases() {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, "workspaceVersionId:" + this.versionId + ",deleted:false,testDataId:" + this.testDataId);
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testCases.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testCases.isEmpty)
          _this.openDeleteDialog();
        else
          _this.openLinkedTestCasesDialog(testCases);
      }
    }
  }

  private openLinkedTestCasesDialog(list) {
    this.translate.get("test_data_profiles.linked_with_cases").subscribe((res) => {
      this.matDialog.open(LinkedEntitiesModalComponent, {
        width: '568px',
        height: '55vh',
        data: {
          description: res,
          linkedEntityList: list,
        },
        panelClass: ['mat-dialog', 'rds-none']
      });
    });
  }

}
