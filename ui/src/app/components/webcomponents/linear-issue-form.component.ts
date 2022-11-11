import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from '../../shared/models/integrations.model';
import {TestCaseResult} from '../../models/test-case-result.model';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TestCaseResultExternalMappingService} from '../../services/test-case-result-external-mapping.service';
import {IntegrationsService} from '../../shared/services/integrations.service';
import {EntityExternalMapping} from '../../models/entity-external-mapping.model';
import { debounceTime, tap } from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";

@Component({
  selector: 'app-linear-issue-form',
  templateUrl: './linear-issue-form.component.html',
  styles: []
})
export class LinearIssueFormComponent extends BaseComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Output('onCreate') createCallBack = new EventEmitter<EntityExternalMapping>();
  public formFR: FormGroup;
  public projects;
  public teams;
  public selectedProject;
  public selectedTeam;
  public isProjectShow: boolean = false;
  public issueList: any;
  public selectedIssue: any;
  public isLinkToIssue: boolean = false;
  public searchIssuesFormCtrl: FormControl = new FormControl();
  public isFetchingIssues: Boolean = false;
  public description: String;
  public title: String;
  public isTeamShow: Boolean = false;
  public isButtonClicked = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private mappingService: TestCaseResultExternalMappingService,
    private applicationService: IntegrationsService) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
    this.applicationService.getLinearTeams(this.application.id).subscribe(res => {
      this.teams = res['data']['teams']['nodes'];
      this.selectedTeam = this.teams[0];
      this.fetchProjects();
    }, error => {
      this.translate.get('message.common.linear.failure', {}).subscribe((res) => {
        this.showAPIError(error, res);
      })
    });
  }

  fetchProjects() {
    this.applicationService.getLinearProjects(this.application.id, this.selectedTeam.id).subscribe(  res => {
      this.projects = res['data']['team']['projects']['nodes'];
      this.selectedProject = this.projects[0];
    });
    this.initFormControl();
    this.description = 'Test Case Name: ' + this.testCaseResult.testCase.name+
      '\nResult URL: '+ window.location.origin +'/ui/td/test_case_results/'+ this.testCaseResult.id + '\n';
  }

  fetchIssues(term?) {
    this.isFetchingIssues = true;
    this.applicationService.searchLinearIssues(this.application.id,  this.selectedProject.id)
      .subscribe(res => {
        this.isFetchingIssues = false;
        this.issueList = term ? res['data']['project']['issues']['nodes'].filter(issue => (issue.title.includes(term))) : res['data']['project']['issues']['nodes'];
      });
  }

  initFormControl() {
    this.formFR = new FormGroup({
      name: new FormControl(this.title, [
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
      mapping.fields['title'] = this.title;
      const des = this.description.split('\n').join('\\n');
      mapping.fields['description'] = des;
      mapping.fields['teamId'] = this.selectedTeam.id;
      mapping.fields['projectId'] = this.selectedProject.id;
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
      this.fetchIssues();
    }

    this.initFormControl();
    this.toggleDropdown(isDropDownClose);
  }

  toggleDropdown(dropDown: string) {
    this[dropDown] = !this[dropDown];
  }

}

