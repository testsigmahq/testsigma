import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {Integrations} from "../../shared/models/integrations.model";
import {IntegrationsService} from "../../shared/services/integrations.service";
import {JiraIssueField} from "../../models/jira-issue-field.model";
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {JiraProject} from "../../models/jira-project.model";
import {JiraIssueType} from "../../models/jira-issue-type.model";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestCaseResultExternalMappingService} from "../../services/test-case-result-external-mapping.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MatDialog} from "@angular/material/dialog";
import {JiraFieldAllowedValue} from "../../models/jira-field-allowed-value.model";
import {catchError, debounceTime, map, switchMap, tap} from 'rxjs/operators';
import {ResultConstant} from "../../enums/result-constant.enum";

@Component({
  selector: 'app-jira-issue-form',
  templateUrl: './jira-issue-form.component.html',
  styles: []
})
export class JiraIssueFormComponent extends BaseComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Output('onCreate') createCallBack = new EventEmitter<EntityExternalMapping>();
  public fields: JiraIssueField[];
  public multiSelectFields: JiraIssueField[];
  form: FormGroup;
  public projects: JiraProject[];
  public issueTypes: JiraIssueType[];
  public selectedProject: JiraProject;
  public selectedIssueType: JiraIssueType;
  public isProjectShow: boolean = false;
  public isIssueTypeShow: boolean = false;
  public selectedPriority: JiraFieldAllowedValue;
  public issueList: any;
  public selectedIssue: any;
  public isLinkToIssue: boolean = false;
  public searchIssuesFormCtrl: FormControl = new FormControl();
  public isFetchingIssues: Boolean = false;
  @Input("isButtonClicked") isButtonClicked: Boolean;

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
    console.log("ngOninit")
    this.applicationService.getJiraFields(this.application.id).subscribe(projects => {
      if (projects && projects[0] && projects[0].issueTypes && projects[0].issueTypes[0]) {
        this.projects = projects;
        this.selectedProject = projects[0];
        this.issueTypes = projects[0].issueTypes;
        this.setBugIssueType();

        this.fields = this.selectedIssueType.formFields;
        this.multiSelectFields = this.filterMultiSelectFields();
        this.selectedIssue = undefined;
        this.initFormControl();
      }
    });
  }

  ngOnChange(namespace :SimpleChanges) : void {
    console.log(namespace);
  }

  filterMultiSelectFields() {
    return this.fields.filter((f) => (f?.schema["custom"] == "com.atlassian.jira.plugin.system.customfieldtypes:multiselect"));
  }

  setBugIssueType() {
    this.selectedIssueType = this.issueTypes.find(type => type.name == 'Bug') || this.issueTypes[0];
  }

  initFormControl() {
    const group: any = {};
    this.fields.forEach(field => {
      if(field)
        group[field.key.toString()] = field.required ? new FormControl(undefined, Validators.required)
          : new FormControl(undefined);
    })
    this.form = new FormGroup(group);
  }

  fetchIssuesByTerm(term?: any) {
    return this.applicationService.searchJiraIssues(this.application.id, this.selectedProject.id, this.selectedIssueType.id, term)
  }

  displayIssueAutocomplete(issue): string {
    if (issue) {
      return '[' + issue['key'] + '] ' + issue['fields']['summary'];
    }
  }

  formatMultiSelectFields(mappingFields: Map<String, Object>) {
    this.multiSelectFields.forEach(field => {
      const fieldKey = field?.key.toString();
      if (mappingFields.hasOwnProperty(fieldKey) && (mappingFields[fieldKey] instanceof Array)) {
        const values = <Array<string>>mappingFields[fieldKey];
        const selectedValues = [];
        values.forEach(value => {
          selectedValues.push({
            "value": value
          });
        });
        mappingFields[fieldKey] = selectedValues;
      }
    });
    return mappingFields;
  }

  onSubmit() {
    let mapping = new EntityExternalMapping();
    if (this.searchIssuesFormCtrl.value) {
      mapping.linkToExisting = true;
      mapping.externalId = this.searchIssuesFormCtrl.value.key;
      mapping.applicationId = <number>this.application.id;
    } else {
      mapping.applicationId = <number>this.application.id;
      mapping.fields = this.form.getRawValue();
      mapping.fields = this.formatMultiSelectFields(mapping.fields);
      mapping.fields["priority"] = {"name": this.selectedPriority.name};
      mapping.fields["project"] = {"id": this.selectedProject.id};
      mapping.fields["issuetype"] = {"id": this.selectedIssueType.id};
      var description="";
      mapping.fields["description"] = this.getJiraDescription(description)+mapping.fields["description"]+", ";
      mapping.fields["summary"] = "[Automated Test Failed]"+this.testCaseResult.testCase.name;

    }
    this.createCallBack.emit(mapping);
  }

  getJiraDescription(description){

    var JIRA_DESCRIPTION="Test Description";
    var JIRA_IS_DATA_DRIVEN="Data Driven";
    var JIRA_TEST_CASE_RESULT="Test Case Result";
    var JIRA_RUN_NAME="Run Name";
    var JIRA_RUN_ID="Run Id";
    var JIRA_RUN_CONFIG="Run Config";
    var JIRA_START_TIME="Start Time";
    var JIRA_DURATION="Duration";
    var JIRA_SUMMARY="Summary";
    var JIRA_STEPS="steps";
    var JIRA_MESSAGE="Message";

    description = JIRA_DESCRIPTION+" : "+" "+this.form.value.description+",  ";
    var isDataDriven = (this.testCaseResult.testCase.isDataDriven)? 'Yes,' : 'No,';
    description =  description + JIRA_IS_DATA_DRIVEN + " : " + isDataDriven + "  " ;
    description =  description +  JIRA_TEST_CASE_RESULT + " : " +  " " + this.testCaseResult.result +", " ;
    description =  description + JIRA_RUN_NAME + " : " + this.testCaseResult.testDeviceResult.testPlanResult.testPlan.name + ", " + JIRA_RUN_ID + " : "+ this.testCaseResult.testDeviceResult.testPlanResult.id +", ";
    description =  description + JIRA_RUN_CONFIG + " : " + this.testCaseResult.testDeviceResult.testPlanResult.testPlan.testPlanLabType + ", "
      + this.testCaseResult.testDeviceResult.testDevice.platform + '(' + this.testCaseResult.testDeviceResult.testDevice.osVersion + ')' + ", "
      + this.testCaseResult.testDeviceResult.testDevice.browser + '(' + this.testCaseResult.testDeviceResult.testDevice.browserVersion +')' +" ";

    var duration = this.testCaseResult.duration;

    description =  description + JIRA_START_TIME + " : " + new Date(this.testCaseResult.startTime) + ", " + JIRA_DURATION + " : " + duration + " ms, ";
    description =  description + JIRA_SUMMARY + " : " + this.testCaseResult.totalCount + "  " + JIRA_STEPS + ", " + this.getStatusString() +", ";
    description =  description + JIRA_MESSAGE + " : " + this.replaceMsgSpecialCharcter(this.replaceHtml(this.testCaseResult.message))
      +", Click here to see the below link https://app.testsigma.com/ui/td/test_case_results/"+this.testCaseResult.id ;

    return description;
  }

  getIterationText = function(metatdata){
    if(metatdata){
      var meta = JSON.parse(metatdata);
      if(meta['for_loop']){
        return meta['for_loop'].testdata+"-"+meta['for_loop'].itearation+"("+meta['for_loop'].index+")";
      }
    }
    return "";
  }
  getStatusString(){
    var statusStr = "";
    statusStr = this.testCaseResult.passedCount ? statusStr+this.testCaseResult.passedCount+" " +ResultConstant.SUCCESS :"";
    statusStr = this.testCaseResult.failedCount ? statusStr+", "+this.testCaseResult.failedCount+" " +ResultConstant.FAILURE :"";
    statusStr = this.testCaseResult.abortedCount ? statusStr+", "+this.testCaseResult.abortedCount+" " +ResultConstant.ABORTED : "";
    statusStr = this.testCaseResult.notExecutedCount ? statusStr+", "+this.testCaseResult.notExecutedCount+" " +ResultConstant.NOT_EXECUTED : "";
    statusStr = this.testCaseResult.queuedCount ? statusStr+", "+this.testCaseResult.queuedCount+" " +ResultConstant.QUEUED : "";
    statusStr = this.testCaseResult.stoppedCount ? statusStr+", "+this.testCaseResult.stoppedCount+" " +ResultConstant.STOPPED :"";
    return statusStr;
  }
  replaceHtml(message){
    message = message.replace("<br>","");
    message = message.replace("</br>","");
    return message.replace(/(<([^>]+)>)/ig,"");
  }

  replaceMsgSpecialCharcter(text){
    return text.replace("->", "");
  }

  regexIndexOf(startpos) {
    var anchorRegex = /<a.*?href\s+=\s+"(.*?)".*?>(.*?)<\/a>/ig;
    var indexOf = startpos.substring(startpos || 0).search(anchorRegex);
    return (indexOf >= 0) ? (indexOf + (startpos || 0)) : indexOf;
  }

  getHrefText(text) {
    var hrefRegex = /<a.*?href\s*=\s*"/ig;
   var match = text.match(hrefRegex);
    if(match && match.length){
      var start =  text.indexOf(match[0]) + match[0].length;
      var txt = text.substring(start, text.length);
      txt = txt.substring(0, txt.indexOf("\""));
      return txt;
    }
    return text;
  }
  toggleProject(project) {
    this.selectedProject = project ? project : this.selectedProject;
    this.isProjectShow = false;
    this.issueTypes = project.issueTypes;
    this.setBugIssueType();
    this.fields = this.selectedIssueType.formFields;
    this.initFormControl();
    if (this.isLinkToIssue) {
      this.fetchIssuesByTerm().subscribe(data => {
        if (data)
          this.issueList = data['issues'];
      })
    }
  }

  toggleIssueType(issueType) {
    this.selectedIssueType = issueType ? issueType : this.selectedIssueType;
    this.fields = this.selectedIssueType.formFields;
    this.isIssueTypeShow = false;
    this.initFormControl();
    if (this.isLinkToIssue) {
      this.fetchIssuesByTerm().subscribe(data => {
        if (data)
          this.issueList = data['issues'];
      })
    }
  }

  toggleDropdown(projectShow: string) {
    this[projectShow] = !this[projectShow];
  }

  setPriority(event: any) {
    this.selectedPriority = event;
  }

  toggleLinkToIssue() {
    this.isLinkToIssue = !this.isLinkToIssue;
    if (this.isLinkToIssue) {
      this.searchIssuesFormCtrl.valueChanges
        .pipe(
          debounceTime(500),
          tap(() => {
            this.issueList = [];
            this.isFetchingIssues = true;
          }),
          switchMap(value => {
            if (value && value.key)
              return [];
            else
              return this.fetchIssuesByTerm(value)
          })
        )
        .subscribe(data => {
          if (data)
            this.issueList = data['issues'];
          this.isFetchingIssues = false;
        });
      this.searchIssuesFormCtrl.setValue('');
    }
  }
}
