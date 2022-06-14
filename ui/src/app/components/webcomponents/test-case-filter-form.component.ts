/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCaseFilter} from "../../models/test-case-filter.model";
import {TestCaseFilterService} from "../../services/test-case-filter.service";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {StepGroupFilter} from "../../models/step-group-filter.model";
import {StepGroupFilterService} from "../../services/step-group-filter.service";

@Component({
  selector: 'app-test-case-filter-form',
  templateUrl: './test-case-filter-form.component.html',
  styles: []
})
export class TestCaseFilterFormComponent extends BaseComponent implements OnInit {

  public filter: TestCaseFilter|StepGroupFilter;
  @ViewChild('name') public filterNameInput: ElementRef;
  public filterForm: FormGroup;
  public formSubmitted: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<TestCaseFilterFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { version: WorkspaceVersion, filter: TestCaseFilter|StepGroupFilter, query: string },
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public testCaseFilterService: TestCaseFilterService,
    private stepGroupFilterService: StepGroupFilterService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get isStepGroup() {
    return this.data.filter instanceof StepGroupFilter;
  }

  ngOnInit(): void {
    if (this.isStepGroup) {
      this.filter = Object.assign(new StepGroupFilter(), this.data.filter);
    } else
      this.filter = Object.assign(new TestCaseFilter(), this.data.filter);
    if (this.data.query) {
      this.filter.normalizeCustomQuery(this.data.query);
      this.filter.queryHash = this.filter.normalizedQuery;
    }
    console.log(this.filter.queryHash);
    this.focusFilterInput();
    this.filterForm = new FormGroup({
      name: new FormControl(this.filter.name, [
        Validators.required,
        Validators.minLength(4),
        this.noWhitespaceValidator
      ]),
      isPublic: new FormControl(this.filter.isPublic, [
        Validators.required
      ]),
    });
  }

  focusFilterInput() {
    if (this.filterNameInput && this.filterNameInput.nativeElement)
      this.filterNameInput.nativeElement.focus();
    else
      setTimeout(() => this.focusFilterInput(), 200);
  }

  save(): void {
    this.formSubmitted = true;
    if (this.filterForm.invalid)
      return;
    if(this.isStepGroup) {
      if (this.filter.id)
        this.saveStepGroupFilter();
      else
        this.createStepGroupFilter();
    } else {
      if (this.filter.id)
        this.saveFilter();
      else
        this.createFilter();
    }
  }

  private saveFilter() {
    this.testCaseFilterService.save(this.filter.id, <TestCaseFilter>this.filter).subscribe((res) => {
      this.translate.get('filter.saved.success', {name: this.filter.name}).subscribe((key: string) => {
        this.showNotification(NotificationType.Success, key);
        this.dialogRef.close(res);
      });
    }, error => {
      this.translate.get('message.common.update.failure', {FieldName:'Test Case View '}).subscribe((key: string) => {
        this.showAPIError(error, key);
      });
    });
  }

  private createFilter() {
    this.testCaseFilterService.create(<TestCaseFilter>this.filter).subscribe((res) => {
      this.translate.get('filter.created.success', {name: this.filter.name}).subscribe((key: string) => {
        this.showNotification(NotificationType.Success, key);
        this.dialogRef.close(res);
      });
    }, error => {
      this.translate.get('message.common.created.failure', {FieldName:'Test Case View '}).subscribe((key: string) => {
        this.showAPIError(error, key);
      });
    });
  }

  private saveStepGroupFilter() {
    this.stepGroupFilterService.save(this.filter.id, <StepGroupFilter>this.filter).subscribe((res) => {
      this.translate.get('filter.saved.success', {name: this.filter.name}).subscribe((key: string) => {
        this.showNotification(NotificationType.Success, key);
        this.dialogRef.close(res);
      });
    }, error => {
      this.translate.get('message.common.update.failure', {FieldName:'Test Case View '}).subscribe((key: string) => {
        this.showAPIError(error, key);
      });
    });
  }

  private createStepGroupFilter() {
    this.stepGroupFilterService.create(<StepGroupFilter>this.filter).subscribe((res) => {
      this.translate.get('filter.created.success', {name: this.filter.name}).subscribe((key: string) => {
        this.showNotification(NotificationType.Success, key);
        this.dialogRef.close(res);
      });
    }, error => {
      this.translate.get('message.common.created.failure', {FieldName:'Test Case View '}).subscribe((key: string) => {
        this.showAPIError(error, key);
      });
    });
  }
}
