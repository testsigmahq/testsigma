import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Element} from '../../models/element.model';
import {Base} from '../../shared/models/base.model';
import {ActivatedRoute} from '@angular/router';
import {ConfirmationModalComponent} from '../../shared/components/webcomponents/confirmation-modal.component';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {AuthenticationGuard} from '../../shared/guards/authentication.guard';
import {ElementService} from '../../shared/services/element.service';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from '../../shared/components/base.component';
import {ElementFormComponent} from '../webcomponents/element-form.component';
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {TestCaseService} from "../../services/test-case.service";
import {ElementTagService} from '../../services/element-tag.service';
import * as moment from 'moment';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-elements-details-header',
  templateUrl: './details-header.component.html',
})
export class DetailsHeaderComponent extends BaseComponent implements OnInit {
  public element: Element;
  public elementOne: Base;
  public activeTab: String = 'details';
  public elementTag:any;
  public elementId: number;
  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public elementService: ElementService,
    public elementTagService: ElementTagService,
    private testCaseService: TestCaseService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    // private userService: UserService,
    public route: ActivatedRoute,
    private matDialog: MatDialog,
    private dialogRef: MatDialogRef<DetailsHeaderComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { elementId: number, versionId: number}
  ) {
    super(authGuard, notificationsService, translate, toastrService);
    this.elementId = this.data.elementId;
  }

  ngOnInit() {
    this.fetchElement();
  }

  get hasDetails() {
    return this.element?.elementDetails?.attributes?.length
      || this.element?.metadata?.parents?.length
      || this.element?.metadata?.followingSiblings?.length
      || this.element?.metadata?.precedingSiblings?.length
      || this.element?.metadata?.firstLevelChildren?.length
      || this.element?.metadata?.secondLevelChildren?.length;
  }

  fetchElement(){
    this.elementService.show(this.data.elementId).subscribe(res => {
      this.element = res;
      if(!this.hasDetails)
        this.activeTab = 'comments';
    });
  }

  get dataIsSame() {
    return moment(this.element.updatedAt).isSame(this.element.createdAt);
  }

  openDeleteDialog() {
    const message = 'element.delete.confirmation.message';
    this.translate.get(message, {FieldName: 'Element'}).subscribe((res) => {
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
            this.destroyElement();
          }
        });
    });
  }

  destroyElement() {
    this.elementService.destroy(this.element.id).subscribe(
      () => {
        this.translate.get('element.notification.delete.success')
          .subscribe(res => {
            this.showNotification(NotificationType.Success, res);
            this.dialogRef.close();
          });
      },
      (err) => {
        this.translate.get('element.notification.delete.failure')
          .subscribe(res => {
            this.showAPIError(err, res);
          });
      }
    );
  }

  openAddEditElement() {
    const dialogRef = this.matDialog.open(ElementFormComponent, {
      height: '100vh',
      width: '60%',
      position: {top: '0px', right: '0px'},
      data: {
        versionId: this.data.versionId,
        elementId: this.element.id
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((_res) => {
        this.fetchElement();
      });

  }

  checkForLinkedTestCases() {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, "workspaceVersionId:" +
      this.element.workspaceVersionId + ",deleted:false,element:" + this.element.name);
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
    this.translate.get("elements.linked_with_cases").subscribe((res) => {
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
