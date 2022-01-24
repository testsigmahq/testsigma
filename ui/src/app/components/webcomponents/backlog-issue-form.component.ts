import {Component, EventEmitter, Input, OnInit, Output, ViewChild, ElementRef} from '@angular/core';
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
import {TestCaseResultExternalMapping} from '../../models/test-case-result-external-mapping.model';
import { fromEvent } from 'rxjs';
import { filter, debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';

@Component({
  selector: 'app-backlog-issue-form',
  templateUrl: './backlog-issue-form.component.html',
  styles: []
})
export class BackLogIssueFormComponent extends BaseComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Output('onCreate') createCallBack = new EventEmitter<TestCaseResultExternalMapping>();
  public formFR: FormGroup;
  public projects: JSON;
  public issueTypes: JSON;
  public selectedProject;
  public selectedIssueType;
  public isProjectShow: boolean = false;
  public issueList: any;
  public selectedIssue: any;
  public prioritiesList: any;
  public selectedPriority: any;
  public isLinkToIssue: boolean = false;
  public isFetchingIssues: Boolean = false;
  public description: String;
  public summary: String;
  public isIssueTypeShow: Boolean = false;
  public isPriorityShow: Boolean = false;
  public isButtonClicked: Boolean = false;

  @ViewChild('searchInput', {static: false}) searchInput: ElementRef;

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
    this.applicationService.getBackLogProjects(this.application.id).subscribe(res => {
      this.projects = res;
      this.selectedProject = this.projects[0];
      this.fetchIssueTypes();
      this.fetchPriorities();
      this.attachSearchEvents();
      this.description="Test Case Name: " +this.testCaseResult.testCase.name + ", " +
        "\nResult URL: " + window.location.origin + "/ui/td/test_case_results/"+ this.testCaseResult.id+"\n";
    });
  }

  fetchIssueTypes() {
    this.applicationService.getBackLogIssueTypes(this.application.id, this.selectedProject.id).subscribe(res => {
      this.issueTypes = res;
      this.selectedIssueType = this.issueTypes[0];
    });
    this.initFormControl();
  }
  fetchPriorities() {
      this.applicationService.getBackLogPriorities(this.application.id).subscribe(res => {
        this.prioritiesList = res;
        this.selectedPriority = this.prioritiesList[0];
      });

    this.initFormControl();
  }

  fetchIssues(term?: string) {
    this.applicationService
      .searchBackLogIssues(this.application.id, this.selectedProject.id, this.selectedIssueType.id, this.selectedPriority.id, term)
      .subscribe(res => {
      this.issueList = res;
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
      mapping.workspaceId = <number>this.application.id;
      mapping.testCaseResultId = this.testCaseResult.id;
    } else {
      mapping.workspaceId = <number>this.application.id;
      mapping.testCaseResultId = this.testCaseResult.id;
      mapping.fields = new Map<String, Object>();
      mapping.fields['summary'] = this.summary;
      mapping.fields['description'] = this.description;
      mapping.fields['projectId'] = this.selectedProject.id;
      mapping.fields['issueTypeId'] = this.selectedIssueType.id;
      mapping.fields['priorityId'] = this.selectedPriority.id;
    }
    this.isButtonClicked = true;
    this.createCallBack.emit(mapping);
  }


  toggleLinkToIssue() {
    this.isLinkToIssue = !this.isLinkToIssue;
    if (this.isLinkToIssue) {
      this.fetchIssues();
    }
  }
  setSelectedItem(setItem, selectedItem, isDropDownClose?) {
    this[setItem] = selectedItem;
    if (this.isLinkToIssue && setItem!= 'selectedIssue') {
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

  attachSearchEvents() {
    if(this.searchInput?.nativeElement){
      this.attachDebounceEvent();
      setTimeout(() => this.searchInput?.nativeElement.focus(), 10);
    }
    else
      setTimeout(()=> this.attachSearchEvents(), 100);
  }

  attachDebounceEvent() {
    fromEvent(this.searchInput.nativeElement, 'keyup')
      .pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((_event: KeyboardEvent) => {
          let value;
          if (this.searchInput.nativeElement.value) {
            value = this.searchInput.nativeElement.value;
          }
          this.fetchIssues(value)
        })
      )
      .subscribe();
  }

}
