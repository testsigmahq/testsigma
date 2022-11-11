import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TestCaseResult} from '../../models/test-case-result.model';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AuthenticationGuard} from '../../shared/guards/authentication.guard';
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestCaseResultExternalMappingService} from '../../services/test-case-result-external-mapping.service';
import {MatDialog} from '@angular/material/dialog';
import {BaseComponent} from '../../shared/components/base.component';
import {EntityExternalMapping} from '../../models/entity-external-mapping.model';
import { debounceTime, tap} from 'rxjs/operators';
import {Integrations} from "../../shared/models/integrations.model";
import {IntegrationsService} from "../../shared/services/integrations.service";

@Component({
  selector: 'app-click-up-issue-form',
  templateUrl: './click-up-issue-form.component.html',
  styles: []
})
export class ClickUpIssueFormComponent extends BaseComponent implements OnInit {
  @Input('workspace') workspace: Integrations;
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Output('onCreate') createCallBack = new EventEmitter<EntityExternalMapping>();
  public formFR: FormGroup;
  public teams;
  public spaces;
  public selectedTeam;
  public selectedSpace;
  public folders;
  public lists;
  public selectedFolder;
  public selectedList;
  public isTeamShow: boolean = false;
  public issueList: any;
  public selectedIssue: any;
  public isLinkToIssue: boolean = false;
  public searchIssuesFormCtrl: FormControl = new FormControl();
  public isFetchingIssues: Boolean = false;
  public description: String;
  public title: String;
  public isSpaceShow: Boolean = false;
  public isFolderShow: Boolean = false;
  public isListShow: Boolean = false;
  public isButtonClicked: Boolean = false;
  public isNoFolders: Boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private mappingService: TestCaseResultExternalMappingService,
    private workspaceService: IntegrationsService,
    public dialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
    this.workspaceService.getClickUpTeams(this.workspace.id).subscribe(res => {
      this.teams = res["data"].teams;
      this.selectedTeam = this.teams[0];
      this.fetchSpaces();
      this.description="Test Case Name: " +this.testCaseResult.testCase.name + ", " +
        "\nResult URL: " + window.location.origin + "/#/td/test_case_results/"+ this.testCaseResult.id+"\n";
    });
  }

  fetchSpaces() {
    this.workspaceService.getClickUpSpaces(this.workspace.id, this.selectedTeam.id).subscribe(res => {
      this.spaces = res["data"].spaces;
      this.selectedSpace = this.spaces[0];
      this.fetchFolders();
    });
  }

  fetchFolders() {
    this.workspaceService.getClickUpFolders(this.workspace.id, this.selectedSpace.id).subscribe(res => {
      this.folders = res["data"].folders;
      if(this.folders.length !=0){
        this.selectedFolder = this.folders[0];
        this.fetchLists();
        this.isNoFolders = false;
      } else {
        this.selectedFolder = undefined;
        this.isNoFolders = true;
      }
    });
  }

  fetchLists() {
    this.workspaceService.getClickUpLists(this.workspace.id, this.selectedFolder.id).subscribe(res => {
      this.lists= res["data"].lists;
      this.selectedList = this.lists[0];
    });
    this.initFormControl();
  }

  fetchIssues(term?) {
    this.workspaceService.searchClickUpIssues(this.workspace.id, this.selectedList.id).subscribe(res => {
      this.isFetchingIssues = false;
      this.issueList = term ? res['data'].tasks.filter(issue => (issue.title.includes(term))) : res['data'].tasks;
    });
  }

  initFormControl() {
    this.formFR = new FormGroup({
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
      mapping.fields['listId'] = this.selectedList.id;
    } else {
      mapping.fields = new Map<String, Object>();
      mapping.fields['title'] = this.title;
      mapping.fields['description'] = this.description;
      mapping.fields['listId'] = this.selectedList.id;
    }
    mapping.applicationId = <number>this.workspace.id;
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

    if (setItem == 'selectedTeam') {
      this.fetchSpaces();
    }
    if (setItem == 'selectedSpace') {
      this.fetchFolders();
    }
    if (setItem == 'selectedFolder') {
      this.fetchLists();
    }
    if (setItem == 'selectedList') {
      this.fetchIssues();
    }

    this.initFormControl();
    this.toggleDropdown(isDropDownClose);
  }

  toggleDropdown(dropDown: string) {
    this[dropDown] = !this[dropDown];
  }

}
