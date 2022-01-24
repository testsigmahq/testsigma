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

@Component({
  selector: 'app-test-case-priorities',
  templateUrl: './test-case-priorities.component.html',
  host: {'class': 'page-content-container', 'style':'flex: 1'}
})
export class TestCasePrioritiesComponent extends BaseComponent implements OnInit {
  public testCasePriorities: InfiniteScrollableDataSource;
  public rowNameControl = new FormControl('', [Validators.required]);
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
    if (this.rowNameControl.invalid) return;
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
    if (this.rowNameControl.invalid) return;
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
        .subscribe(res => this.showAPIError(_err, res))
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
