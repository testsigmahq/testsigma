import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from '../../shared/models/integrations.model';
import {TestCaseResult} from '../../models/test-case-result.model';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AuthenticationGuard} from '../../shared/guards/authentication.guard';
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestCaseResultExternalMappingService} from '../../services/test-case-result-external-mapping.service';
import {IntegrationsService} from '../../shared/services/integrations.service';
import {MatDialog} from '@angular/material/dialog';
import {BaseComponent} from '../../shared/components/base.component';
import {EntityExternalMapping} from '../../models/entity-external-mapping.model';
import { debounceTime, tap, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-bugzilla-issue-form',
  templateUrl: './bugzilla-issue-form.component.html',
  styles: []
})
export class BugZillaIssueFormComponent extends BaseComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Output('onCreate') createCallBack = new EventEmitter<EntityExternalMapping>();
  public formFR: FormGroup;
  public projects: JSON;
  public issueTypes: JSON;
  public selectedProject;
  public selectedIssueType;
  public versions;
  public selectedVersion;
  public isProjectShow: boolean = false;
  public issueList: any;
  public selectedIssue: any;
  public isLinkToIssue: boolean = false;
  public searchIssuesFormCtrl: FormControl = new FormControl();
  public isFetchingIssues: Boolean = false;
  public description: String;
  public summary: String;
  public isIssueTypeShow: Boolean = false;
  public isVersionShow: Boolean = false;
  public isButtonClicked = false;

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
    this.applicationService.getBugZillaProjects(this.application.id).subscribe(res => {
      this.projects = res['products'];
      this.selectedProject = this.projects[0];
      this.fetchIssueTypes();
      this.fetchVersions();
    });
  }

  fetchIssueTypes() {
    this.issueTypes = this.selectedProject['components'];
    this.selectedIssueType = this.issueTypes[0];
    this.initFormControl();
    this.description="Test Case Name: " +this.testCaseResult.testCase.name + ", " +
      "\nResult URL: " + window.location.origin + "/ui/td/test_case_results/"+ this.testCaseResult.id+"\n";
  }

  fetchVersions(){
    this.versions = this.selectedProject['versions'];
    this.selectedVersion = this.versions[0];
  }
  fetchIssues(term?) {
    this.applicationService.searchBugZillaIssues(this.application.id, this.selectedProject.name, this.selectedIssueType.name, this.selectedVersion.name)
      .subscribe(res => {
      this.isFetchingIssues = false;
      this.issueList = term ? res['bugs'].filter(issue => (issue.summary.includes(term))) : res['bugs'];
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
    let mapping = new EntityExternalMapping();
    if (this.selectedIssue) {
      mapping.linkToExisting = true;
      mapping.externalId = this.selectedIssue.id;
      mapping.applicationId = <number>this.application.id;
    } else {
      mapping.applicationId = <number>this.application.id;
      mapping.fields = new Map<String, Object>();
      mapping.fields['summary'] = this.summary;
      mapping.fields['description'] = this.description;
      mapping.fields['project'] = this.selectedProject.name;
      mapping.fields['issueType'] = this.selectedIssueType.name;
      mapping.fields['version'] = this.selectedVersion.name;

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
          this.fetchIssues(this.searchIssuesFormCtrl.value);
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
