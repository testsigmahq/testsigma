import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from "../../shared/models/integrations.model";
import {TestCaseResult} from "../../models/test-case-result.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestCaseResultExternalMappingService} from "../../services/test-case-result-external-mapping.service";
import {IntegrationsService} from "../../shared/services/integrations.service";
import {MatDialog} from "@angular/material/dialog";
import {BaseComponent} from "../../shared/components/base.component";
import {TestCaseResultExternalMapping} from "../../models/test-case-result-external-mapping.model";
import {FreshReleaseIssueType} from "../../models/fresh-release-issue-type.model";
import { debounceTime, tap, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-mantis-issue-form',
  templateUrl: './mantis-issue-form.component.html',
  styles: []
})
export class MantisIssueFormComponent extends BaseComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Output('onCreate') createCallBack = new EventEmitter<TestCaseResultExternalMapping>();
  public formFR: FormGroup;
  public projects: JSON;
  public issueTypes: JSON;
  public selectedProject;
  public selectedIssueType: FreshReleaseIssueType;
  public isProjectShow: boolean = false;
  public issueList: any;
  public selectedIssue: any;
  public isLinkToIssue: boolean = false;
  public searchIssuesFormCtrl: FormControl = new FormControl();
  public isFetchingIssues: Boolean = false;
  public description: String;
  public summary: String;
  public isIssueTypeShow: Boolean = false;
  public isButtonClicked: Boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private mappingService: TestCaseResultExternalMappingService,
    private applicationService: IntegrationsService,
    public dialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
    this.applicationService.getMantisProjects(this.application.id).subscribe(res => {
      this.projects = res['projects'];
      this.selectedProject = this.projects[0];
      this.fetchIssueTypes();
    });
  }

  fetchIssueTypes() {
    this.issueTypes = this.selectedProject['categories'];
    this.selectedIssueType = this.issueTypes[0];
    this.initFormControl();
    this.description="Test Case Name: " +this.testCaseResult.testCase.name + ", " +
      "\nResult URL: " + window.location.origin + "/ui/td/test_case_results/"+ this.testCaseResult.id+"\n";
  }

  fetchIssues(term?) {
    this.applicationService.searchMantisIssues(this.application.id, this.selectedProject.id).subscribe(res => {
      this.isFetchingIssues = false;
      this.issueList = res['issues'].filter(issue => issue.category.name == this.selectedIssueType.name || (!!term && issue.summary.includes(term)));
    });
  }

  initFormControl() {
    this.formFR = new FormGroup({
      summary: new FormControl(this.summary, [
        Validators.required
      ]),
      description: new FormControl(this.description, [
        Validators.required]
      )
    });
  }

  onSubmit() {
    let mapping = new TestCaseResultExternalMapping();
    if (this.selectedIssue) {
      mapping.linkToExisting = true;
      mapping.externalId = this.selectedIssue.id;
      mapping.fields = new Map<String, Object>();
      mapping.fields["project"] = this.selectedProject.name;
      mapping.workspaceId = <number>this.application.id;
      mapping.testCaseResultId = this.testCaseResult.id;
    } else {
      mapping.workspaceId = <number>this.application.id;
      mapping.testCaseResultId = this.testCaseResult.id;
      mapping.fields = new Map<String, Object>();
      mapping.fields['summary'] = this.summary;
      mapping.fields['description'] = this.description;
      mapping.fields["project"] = this.selectedProject.name;
      mapping.fields["category"] = this.selectedIssueType.name;

    }
    this.isButtonClicked = true;
    this.createCallBack.emit(mapping);
  }


  toggleLinkToIssue() {
    this.isLinkToIssue = !this.isLinkToIssue;
    if (this.isLinkToIssue) {
      this.fetchIssues();
      this.searchIssuesFormCtrl.valueChanges
        .pipe(
          debounceTime(500),
          tap(() => {
            this.issueList = [];
            this.isFetchingIssues = true;
          })
        )
        .subscribe(() => {
          this.fetchIssues(this.searchIssuesFormCtrl.value)
        });
    }
  }
  setSelectedItem(setItem, selectedItem, isDropDownClose?) {
    this[setItem] = selectedItem;
    if (this.isLinkToIssue) {
      this.fetchIssues();
    }

    if (setItem == 'selectedProject') {
      this.fetchIssueTypes();
    }

    this.initFormControl();
    this.toggleDropdown(isDropDownClose);
  }

  toggleDropdown(dropDown: string) {
    this[dropDown] = !this[dropDown];
  }

}
