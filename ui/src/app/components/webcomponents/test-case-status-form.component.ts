import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {ActivatedRoute, Params, Router} from "@angular/router";

import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import {BaseComponent} from "../../shared/components/base.component";
import {TestCase} from "../../models/test-case.model";
import {TestCaseStatus} from "../../enums/test-case-status.enum";
import {TestCaseService} from "../../services/test-case.service";

@Component({
  selector: 'app-test-case-status-form',
  templateUrl: './test-case-status-form.component.html',
  styles: []
})
export class TestCaseStatusFormComponent extends BaseComponent implements OnInit {

  public testcaseStatusForm: FormGroup;
  public formSubmitted: boolean = false;
  public testcase: TestCase;
  public testcaseId: number;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
    private matDialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public options: { testcase?: TestCase },
    private testcaseService: TestCaseService) {
    super(authGuard, notificationsService, translate, toastrService);
    this.testcase = this.options.testcase;
  }

  get statuses() {
    return Object.keys(TestCaseStatus)
  }

  ngOnInit(): void {

    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      this.testcaseId = params.scheduledPlanId;
      if (this.testcaseId) {
        //this.fetchScheduledPlan();
      } else {
        this.addValidations();
      }
    })
  }

  addValidations() {
    this.testcaseStatusForm = new FormGroup({
      status: new FormControl(this.testcase.status)
    });
  }

  updateTestCaseStatus() {
    this.testcaseService.update(this.testcase).subscribe(() => {
      this.translate.get('message.common.update.success', {FieldName: 'Test Case Status'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);

        this.matDialog.closeAll();
      })
    })
  }
}
