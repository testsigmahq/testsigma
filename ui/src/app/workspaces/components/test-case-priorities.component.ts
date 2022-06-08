import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import {TestCasePrioritiesService} from "../../services/test-case-priorities.service";
import {BaseComponent} from "../../shared/components/base.component";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import { FormControl, Validators } from '@angular/forms';
import { NotificationsService, NotificationType } from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import {TestCasePriority} from "../../models/test-case-priority.model";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {TestCaseService} from "../../services/test-case.service";

@Component({
  selector: 'app-test-case-priorities',
  templateUrl: './test-case-priorities.component.html',
  host: {'class': 'page-content-container', 'style':'flex: 1'}
})
export class TestCasePrioritiesComponent extends BaseComponent implements OnInit {
  public testCasePriorities: InfiniteScrollableDataSource;
  public rowNameControl = new FormControl('', [Validators.required, this.noWhitespaceValidator]);
  public editMode = false;
  public submitted = false;
  public newRow = false;
  @ViewChild('priorityNameField', {static: false}) priorityNameField: ElementRef;
  private workspaceId: number;
  public isFiltered: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testCaseService: TestCaseService,
    private matDialog: MatDialog,
    private testCasePrioritiesServices: TestCasePrioritiesService,
    private route: ActivatedRoute) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.workspaceId = this.route.parent.snapshot.params.workspaceId;
    this.fetchTestCasePriorities();
  }

  fetchTestCasePriorities(searchName?) {
    let query = "workspaceId:" + this.workspaceId;
    if (searchName) {
      this.isFiltered = true;
      query += ",name:*" + searchName + "*";
    } else {
      this.isFiltered = false;
    }
    this.testCasePriorities = new InfiniteScrollableDataSource(this.testCasePrioritiesServices, query);
  }

  editTestCasePriority(id) {
    this.testCasePriorities['cachedItems'][id]['isEdit'] = true;
    this.rowNameControl.setValue(this.testCasePriorities['cachedItems'][id]["name"]);
    this.editMode = true;
    this.focusInput();
  }

  focusInput(){
    if(this.priorityNameField?.nativeElement)
      this.priorityNameField.nativeElement.focus();
    else
      setTimeout(()=> this.focusInput(), 100);
  }

  openDeleteDialog(id) {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, "priority:" + id , "name,asc" );
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testCases.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testCases.isEmpty)
          _this.deleteConfirmation(id);
        else
          _this.openLinkedTestCasesDialog(testCases);
      }
    }
  }

  private openLinkedTestCasesDialog(list) {
    this.translate.get("test_case_type.linked_with_test_cases").subscribe((res) => {
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

  deleteConfirmation(id){
    this.translate.get("message.common.confirmation.default").subscribe((res) => {
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
            this.destroyTestCasePriority(id);
          }
        });
    })
  }

  updateTestCasePriority(testCasePriority: TestCasePriority) {
    this.submitted = true;
    if (this.rowNameControl.invalid) {
      this.translate.get('form.validation.cannot_have_white_spaces', {FieldName: "Test Case Priority"})
        .subscribe(res => this.showNotification(NotificationType.Error, res));
      return;
    }
    this.editMode = false;
    this.submitted = false;
    testCasePriority.name = this.rowNameControl.value;
    testCasePriority.displayName = testCasePriority.name;
    testCasePriority.workspaceId = this.workspaceId;
    this.testCasePrioritiesServices.update(testCasePriority).subscribe(
      () => {
        this.translate.get('message.common.update.success', {FieldName: "Test Case Priority"})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
        this.ngOnInit();
      },
      _err => this.translate.get('message.common.update.failure', {FieldName: "Test Case Priority"})
        .subscribe(res => this.showAPIError(_err, res))
    )
  }

  createTestCasePriority() {
    this.submitted = true;
    if (this.rowNameControl.invalid) {
      this.translate.get('form.validation.cannot_have_white_spaces', {FieldName: "Test Case Priority"})
        .subscribe(res => this.showNotification(NotificationType.Error, res));
      return;
    }
    this.submitted = false;
    this.newRow = false;
    let newTestCasePriority = new TestCasePriority();
    newTestCasePriority.name = this.rowNameControl.value;
    newTestCasePriority.displayName = newTestCasePriority.name;
    newTestCasePriority.workspaceId = this.workspaceId;
    this.rowNameControl.setValue("");
    this.testCasePrioritiesServices.create(newTestCasePriority).subscribe(
      () => {
        this.translate.get('message.common.created.success', {FieldName: "Test Case Priority"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.ngOnInit();
      },
      _err => this.translate.get('message.common.created.failure', {FieldName: "Test Case Priority"})
        .subscribe(res => this.showAPIError(_err, res, 'Test Case Priority'))
    )
  }

  private destroyTestCasePriority(id) {
    this.testCasePrioritiesServices.destroy(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Test Case Priority"})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
        this.ngOnInit();
      },
      _err => this.translate.get('message.common.deleted.failure', {FieldName: "Test Case Priority"})
        .subscribe(res => this.showAPIError(_err, res))
    )
  }

  addRow() {
    this.newRow=true;
    this.rowNameControl.setValue('');
    this.focusInput();
  }

}
