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
import { debounceTime, tap } from 'rxjs/operators';

@Component({
  selector: 'app-trello-issue-form',
  templateUrl: './trello-issue-form.component.html',
  styles: []
})
export class TrelloIssueFormComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Output('onCreate') createCallBack = new EventEmitter<EntityExternalMapping>();
  public formFR: FormGroup;
  public projects;
  public issueTypes;
  public selectedProject;
  public selectedIssueType;
  public isProjectShow: boolean = false;
  public issueList: any;
  public selectedIssue: any;
  public isLinkToIssue: boolean = false;
  public searchIssuesFormCtrl: FormControl = new FormControl();
  public isFetchingIssues: Boolean = false;
  public description: String;
  public name: String;
  public isIssueTypeShow: Boolean = false;
  public isButtonClicked = false;
  public tokenExpireError = false;

  constructor(
    private mappingService: TestCaseResultExternalMappingService,
    private applicationService: IntegrationsService) {
  }

  ngOnInit(): void {
    this.applicationService.getTrelloProjects(this.application.id).subscribe(  res => {
      if (res['status_code'] != 401) {
        this.projects = res['data'];
        this.selectedProject = this.projects[0];
        this.fetchIssueTypes();
      }
      else {
        this.tokenExpireError = true;
      }
    });
  }

  fetchIssueTypes() {
    this.applicationService.getTrelloIssueTypes(this.application.id, this.selectedProject.shortLink).subscribe(res => {
      this.issueTypes = res;
      this.selectedIssueType = this.issueTypes[0];
    });
    this.initFormControl();
    this.description = 'Test Case Name: ' + this.testCaseResult.testCase.name+
      '\nResult URL: '+ window.location.origin +'/ui/td/test_case_results/'+ this.testCaseResult.id + '\n';
  }

  fetchIssues(term?) {
    this.applicationService.searchTrelloIssues(this.application.id,  this.selectedIssueType.id)
      .subscribe(res => {
        this.isFetchingIssues = false;
        this.issueList = term ? res.filter(issue => (issue.name.includes(term))) : res;
      });
  }

  initFormControl() {
    this.formFR = new FormGroup({
      name: new FormControl(this.name, [
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
      mapping.fields['name'] = this.name;
      mapping.fields['description'] = this.description;
      mapping.fields['issueTypeId'] = this.selectedIssueType.id;
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
