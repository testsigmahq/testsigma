import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Integrations} from "../../shared/models/integrations.model";
import {TestCaseResult} from "../../models/test-case-result.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {EntityExternalMappingService} from "../../services/entity-external-mapping.service";
import {IntegrationsService} from "../../shared/services/integrations.service";
import {MatDialog} from "@angular/material/dialog";
import {BaseComponent} from "../../shared/components/base.component";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {AzureIssueType} from "../../models/azure-issue-type.model";
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {YoutrackProject} from "../../models/youtrack-project.model";

@Component({
  selector: 'app-youtrack-issue-form',
  templateUrl: './youtrack-issue-form.component.html',
  styles: []
})
export class YoutrackIssueFormComponent extends BaseComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Output('onCreate') createCallBack = new EventEmitter<EntityExternalMapping>();
  public formYT: FormGroup;
  public projects: YoutrackProject[];
  public selectedProject: YoutrackProject;
  public isProjectShow: boolean = false;
  public issueList: any;
  public selectedIssue: any;
  public isLinkToIssue: boolean = false;
  public isFetchingIssues: Boolean = false;
  public description: String;
  public title: String;
  public isIssueTypeShow: Boolean = false;
  public isButtonClicked: Boolean = false;

  @ViewChild('searchInput', {static: false}) searchInput: ElementRef;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private mappingService: EntityExternalMappingService,
    private applicationService: IntegrationsService,
    public dialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
    this.applicationService.getYoutrackProjects(this.application.id).subscribe(res => {
      this.projects = res;
      this.selectedProject = this.projects[0];
      if (this.isLinkToIssue) {
        this.fetchIssues();
      }
      this.initFormControl();
      this.attachSearchEvents();
      this.description="Test Case Name: " +this.testCaseResult.testCase.name + ", " +
        "\nResult URL: " + window.location.origin + "/ui/td/test_case_results/"+ this.testCaseResult.id+"\n";
    });
  }


  initFormControl() {
    this.formYT = new FormGroup({
      title: new FormControl(this.title, [
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
      mapping.fields = new Map<String, Object>();
      mapping.fields["project"] = this.selectedProject.name;
      mapping.applicationId = <number>this.application.id;
    } else {
      mapping.applicationId = <number>this.application.id;
      mapping.fields = new Map<String, Object>();
      mapping.fields['title'] = this.title;
      mapping.fields['description'] = this.description;
      mapping.fields["project"] = this.selectedProject.name;
      mapping.fields["projectId"] = this.selectedProject.id;
    }
    this.isButtonClicked = true;
    this.createCallBack.emit(mapping);
  }

  fetchIssues(term?: string) {
    this.issueList = [];
      this.applicationService.searchYoutrackIssues(this.application.id,  term).subscribe(res => {
        this.issueList = res;
      })
  }

  toggleLinkToIssue() {
    this.isLinkToIssue = !this.isLinkToIssue;
    if (this.isLinkToIssue) {
      this.fetchIssues();
    }
  }

  setSelectedItem(setItem, selectedItem, isDropDownClose?) {
    this[setItem] = selectedItem;

    if (this.isLinkToIssue) {
      this.fetchIssues();
    }

    this.initFormControl()
    this.toggleDropdown(isDropDownClose)
  }

  toggleDropdown(dropDown: string) {
    this[dropDown] = !this[dropDown];
  }

  attachSearchEvents() {
    if (this.searchInput?.nativeElement) {
      this.attachDebounceEvent();
      setTimeout(() => this.searchInput?.nativeElement.focus(), 10);
    } else
      setTimeout(() => this.attachSearchEvents(), 100);
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
