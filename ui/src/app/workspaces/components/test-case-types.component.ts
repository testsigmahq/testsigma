import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
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
import {TestCaseType} from "../../models/test-case-type.model";
import {TestCaseTypesService} from "../../services/test-case-types.service";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {TestCaseService} from "../../services/test-case.service";

@Component({
  selector: 'app-test-case-types',
  templateUrl: './test-case-types.component.html',
  host: {'class': 'page-content-container', 'style':'flex: 1'}
})
export class TestCaseTypesComponent extends BaseComponent implements OnInit {
  public testCaseTypes: InfiniteScrollableDataSource;
  public rowNameControl = new FormControl('', [Validators.required]);
  public editMode = false;
  public submitted = false;
  public newRow = false;
  @ViewChild('testCaseTypeNameField', {static: false}) testCaseTypeNameField: ElementRef;
  private workspaceId: number;
  public isFiltered: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private matDialog: MatDialog,
    private testCaseTypesService: TestCaseTypesService,
    private testCaseService: TestCaseService,
    private route: ActivatedRoute) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.workspaceId = this.route.parent.snapshot.params.workspaceId;
    this.fetchTestCaseTypes();
  }

  fetchTestCaseTypes(searchName?) {
    let query = "workspaceId:" + this.workspaceId;
    if (searchName) {
      this.isFiltered = true;
      query += ",name:*" + searchName + "*";
    } else {
      this.isFiltered = false;
    }
    this.testCaseTypes = new InfiniteScrollableDataSource(this.testCaseTypesService, query);
  }

  editTestCaseType(id) {
    this.testCaseTypes['cachedItems'][id]['isEdit'] = true;
    this.rowNameControl.setValue(this.testCaseTypes['cachedItems'][id]["name"]);
    this.editMode = true;
    this.focusInput();
  }

  focusInput() {
    if(this.testCaseTypeNameField?.nativeElement)
      this.testCaseTypeNameField.nativeElement.focus();
    else
      setTimeout(() => this.focusInput(), 100);
  }

  openDeleteDialog(id) {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, "type:" + id , "name,asc" );
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
            this.destroyTestCaseType(id);
          }
        });
    })
  }

  public updateTestCaseType(testCaseType: TestCaseType) {
    this.submitted = true;
    if (this.rowNameControl.invalid) return;
    this.editMode = false;
    this.submitted = false;
    testCaseType.name = this.rowNameControl.value;
    testCaseType.displayName = testCaseType.name;
    testCaseType.workspaceId = this.workspaceId;
    this.testCaseTypesService.update(testCaseType).subscribe(
      () => {
        this.translate.get('message.common.update.success', {FieldName: "Test Case Type"})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
        this.ngOnInit();
      },
      _err => this.translate.get('message.common.update.failure', {FieldName: "Test Case Type"})
        .subscribe(res => this.showAPIError(_err, res))
    )
  }

  createTestCaseType() {
    this.submitted = true;
    if (this.rowNameControl.invalid) return;
    this.submitted = false;
    this.newRow = false;
    let newTestCaseType = new TestCaseType();
    newTestCaseType.name = this.rowNameControl.value;
    newTestCaseType.displayName = newTestCaseType.name;
    newTestCaseType.workspaceId = this.workspaceId;
    this.rowNameControl.setValue("");
    this.testCaseTypesService.create(newTestCaseType).subscribe(
      () => {
        this.translate.get('message.common.created.success', {FieldName: "Test Case Type"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.ngOnInit();
      },
      _err => this.translate.get('message.common.created.failure', {FieldName: "Test Case Type"})
        .subscribe(res => this.showAPIError(_err, res))
    )
  }

  private destroyTestCaseType(id) {
    this.testCaseTypesService.destroy(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Test Case Type"})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
        this.ngOnInit();
      },
      _err => this.translate.get('message.common.deleted.failure', {FieldName: "Test Case Type"})
        .subscribe(res => this.showAPIError(_err, res))
    )
  }

  addRow() {
    this.newRow=true;
    this.rowNameControl.setValue('');
    this.focusInput();
  }

}
