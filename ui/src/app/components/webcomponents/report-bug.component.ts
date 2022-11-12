import {Component, ComponentFactoryResolver, Inject, OnInit} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {IntegrationsService} from "../../shared/services/integrations.service";
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TestCaseResult} from "../../models/test-case-result.model";
import {Integrations} from "../../shared/models/integrations.model";
import {Integration} from "../../shared/enums/integration.enum";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {EntityExternalMappingService} from "../../services/entity-external-mapping.service";

@Component({
  selector: 'app-report-bug',
  templateUrl: './report-bug.component.html',
  styles: []
})
export class ReportBugComponent extends BaseComponent implements OnInit {
  public configs: Integrations[] = [];
  public externalMapping: EntityExternalMapping;
  public externalMappingIssueDetails: EntityExternalMapping[] = [];
  public showExternalApplication: String = 'ADD_NEW';
  public selectedList: String[] = [];
  public unSelectedList: String[] = [];
  public isButtonClicked: Boolean = false;
  public appList: String[] = ['JBR', 'FBR', 'MBR', 'ABR', 'BBR', 'ZBR', 'YBR', 'BZBR', 'TBR', 'LBR'];
  public classList = {
    'JBR': 'jira-software bug-list-jira-software h-100',
    'FBR': 'freshrelease bug-list-freshrelease h-100',
    'MBR': 'mantis bug-list-mantis h-100',
    'ABR': 'azure bug-list-azure h-100',
    'BBR': 'backLog bug-list-backlog h-100',
    'ZBR': 'zepel bug-list-zepel h-100',
    'YBR': 'h-100 youtrack bug-list-youtrack mt-4',
    'BZBR': 'bugzilla bug-list-bugzilla h-100',
    'TBR': 'h-100 trello-report bug-list-trello',
    'LBR': 'h-100 linear-report bug-list-linear mt-2',
    'CBR': 'h-100 click_up bug-list-linear mt-2'
  };

  constructor(public authGuard: AuthenticationGuard,
              public notificationsService: NotificationsService,
              public translate: TranslateService,
              public toastrService: ToastrService,
              private externalMappingService: EntityExternalMappingService,
              private integrationsService: IntegrationsService,
              private resolver: ComponentFactoryResolver,
              @Inject(MAT_DIALOG_DATA) public options: { testCaseResult: TestCaseResult }) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.fetchExternalApplicationConfig();
  }

  fetchExternalApplicationConfig() {
    this.integrationsService.findAll().subscribe(configs => {
      this.configs = configs;
      configs.forEach(mapping => {
        if (mapping.isJira && this.selectedList.indexOf('JBR') == -1) {
          this.selectedList.push('JBR');
        } else if (mapping.isFreshrelease && this.selectedList.indexOf('FBR') == -1) {
          this.selectedList.push('FBR');
        } else if (mapping.isMantis && this.selectedList.indexOf('MBR') == -1) {
          this.selectedList.push('MBR');
        } else if (mapping.isAzure && this.selectedList.indexOf('ABR') == -1) {
          this.selectedList.push('ABR');
        } else if (mapping.isBackLog && this.selectedList.indexOf('BBR') == -1) {
          this.selectedList.push('BBR');
        } else if (mapping.isZepel && this.selectedList.indexOf('ZBR') == -1) {
          this.selectedList.push('ZBR');
        } else if (mapping.isYoutrack && this.selectedList.indexOf('YBR') == -1) {
          this.selectedList.push('YBR');
        } else if (mapping.isBugzilla && this.selectedList.indexOf('BZBR') == -1) {
          this.selectedList.push('BZBR');
        } else if (mapping.isTrello && this.selectedList.indexOf('TBR') == -1) {
          this.selectedList.push('TBR');
        } else if (mapping.isLinear && this.selectedList.indexOf('LBR') == -1) {
          this.selectedList.push('LBR');
        } else if (mapping.isClickUp && this.selectedList.indexOf('CBR') == -1) {
          this.selectedList.push('CBR');
        } else {
        }
      })
      this.appList.forEach(app => {
        if (this.selectedList.indexOf(app) == -1 && this.unSelectedList.indexOf(app) == -1)
          this.unSelectedList.push(app);
      })
      this.fetchExternalMapping();
    });
  }

  fetchExternalMapping(): void {
    let bugReportingWorkspaceIds: string[] = this.configs
      .filter(res => [Integration.Zepel, Integration.Jira, Integration.Freshrelease, Integration.BugZilla,
        Integration.Azure, Integration.Mantis, Integration.BackLog, Integration.Youtrack, Integration.Trello, Integration.Linear, Integration.ClickUp]
        .includes(res.workspace))
      .map(res => res.id.toString());
    if (bugReportingWorkspaceIds.length > 0)
      this.externalMappingService.findAll("entityId:"+ this.options.testCaseResult).subscribe((res) => {
        if (res.content.length) {
          this.externalMapping = res.content.find(res => bugReportingWorkspaceIds.includes(res.applicationId.toString()));
          this.externalMapping.application = this.configs.find(config => config.id == this.externalMapping.applicationId);
          if (this.externalMapping.application.isJira) {
            this.showExternalApplication = "JBR";
          }
          if (this.externalMapping.application.isFreshrelease) {
            this.showExternalApplication = 'FBR';
          }
          if (this.externalMapping.application.isMantis) {
            this.showExternalApplication = 'MBR';
          }
          if (this.externalMapping.application.isAzure) {
            this.showExternalApplication = "ABR";
          }
          if (this.externalMapping.application.isBackLog) {
            this.showExternalApplication = 'BBR';
          }
          if (this.externalMapping.application.isZepel) {
            this.showExternalApplication = 'ZBR';
          }
          if (this.externalMapping.application.isYoutrack) {
            this.showExternalApplication = 'YBR';
          }
          if (this.externalMapping.application.isBugzilla) {
            this.showExternalApplication = 'BZBR';
          }
          if (this.externalMapping.application.isTrello) {
            this.showExternalApplication = 'TBR';
          }
          if (this.externalMapping.application.isLinear) {
            this.showExternalApplication = 'LBR';
          }
          if (this.externalMapping.application.isClickUp) {
            this.showExternalApplication = 'CBR';
          }
          this.fetchIssueDetails();
          if(!this.showExternalApplication){
            this.showExternalApplication = this.selectedList[0];
          }

        } else {
          if(this.selectedList?.length && !this.showExternalApplication)
            this.showExternalApplication = this.selectedList[0];
            this.mappingIssueFindAll()
        }
      })
  }

  fetchIssueDetails(): void {
    if (this.externalMapping) {
      this.mappingIssueFindAll();
      this.externalMappingService.show(this.externalMapping.id).subscribe(res => {
        this.externalMapping = res;
        this.externalMapping.application = this.configs.find(config => config.id == this.externalMapping.applicationId);
      });
    }
  }

  mappingIssueFindAll() {
    this.externalMappingService.findAll("entityId:"+this.options.testCaseResult).subscribe(res => {
      this.externalMappingIssueDetails = res.content;
    })
  }

  destroy(event: EntityExternalMapping) {
    this.externalMappingService.destroy(event).subscribe(() => {
      this.translate.get("test_case_result.details.report_bug.un_link.success").subscribe(res => {
        this.showNotification(NotificationType.Success, res);
        this.fetchExternalApplicationConfig();
      })
    })
  }

  create(event: EntityExternalMapping) {
    this.isButtonClicked = true;
    this.externalMappingService.create(event).subscribe(() => {
      this.translate.get("Successfully reported bug on your favorite bug tracking system").subscribe(res => {
        this.showNotification(NotificationType.Success, res);
        this.fetchExternalMapping();
        this.isButtonClicked = false;
      })
    },error => {
      this.translate.get("Failed to report the bug.").subscribe(res => {
        this.showAPIError(error,res);
        console.log("xyz"+this.isButtonClicked);
        this.isButtonClicked = false;
      })
    })
  }

  toggleView(view: String) {
    this.showExternalApplication = view;
  }

  get showJiraApplication() {
    return this.showExternalApplication == 'JBR';
  }

  get showFreshReleaseApplication() {
    return this.showExternalApplication == 'FBR';
  }

  get showAzureApplication() {
    return this.showExternalApplication == 'ABR';
  }

  get showMantisApplication() {
    return this.showExternalApplication == 'MBR';
  }

  get showZepelApplication() {
    return this.showExternalApplication == 'ZBR';
  }

  get showClickUpApplication() {
    return this.showExternalApplication == 'CBR';
  }

  get showLinearApplication() {
    return this.showExternalApplication == 'LBR';
  }

  get showYoutrackApplication() {
    return this.showExternalApplication == 'YBR';
  }

  get showBackLogApplication() {
    return this.showExternalApplication == 'BBR';
  }

  get showBugZillaApplication() {
    return this.showExternalApplication == 'BZBR';
  }

  get showTrelloApplication() {
    return this.showExternalApplication == 'TBR';
  }

  get mantis(): Integrations {
    return this.configs && this.configs.find(config => config.isMantis);
  }

  get backLog(): Integrations {
    return this.configs && this.configs.find(config => config.isBackLog);
  }

  get zepel(): Integrations {
    return this.configs && this.configs.find(config => config.isZepel);
  }

  get clickUpApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isClickUp);
  }

  get linear(): Integrations {
    return this.configs && this.configs.find(config => config.isLinear);
  }

  get trello(): Integrations {
    return this.configs && this.configs.find(config => config.isTrello);
  }

  get jiraApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isJira);
  }

  get azureApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isAzure);
  }

  get bugZillaApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isBugzilla);
  }

  get freshreleaseApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isFreshrelease);
  }

  get mantisApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isMantis);
  }

  get backLogApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isBackLog);
  }

  get zepelApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isZepel);
  }

  get trelloApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isTrello);
  }

  get linearApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isLinear);
  }

  get youtrackApplication(): Integrations {
    return this.configs && this.configs.find(config => config.isYoutrack);
  }

  get showWarning() {
    return this?.options?.testCaseResult?.isPassed || this?.options?.testCaseResult?.isQueued || this?.options?.testCaseResult?.isStopped;
  }
  get showAddPlugIn(){
    return this.showExternalApplication == 'ADD_NEW';
  }

  public showList(app: String) {
    switch (app) {
      case 'JBR':
        return this.showJiraApplication;
      case 'FBR':
        return this.showFreshReleaseApplication;
      case 'MBR':
        return this.showMantisApplication;
      case 'ABR':
        return this.showAzureApplication;
      case 'BBR':
        return this.showBackLogApplication;
      case 'ZBR':
        return this.showZepelApplication;
      case 'YBR':
        return this.showYoutrackApplication
      case 'BZBR':
        return this.showBugZillaApplication;
      case 'TBR':
        return this.showTrelloApplication
      case 'LBR':
        return this.showLinearApplication;
      case 'CBR':
        return this.showClickUpApplication;
      case 'ADD_NEW':
        return this.showAddPlugIn;
      default:
        return false;
    }
  };
}
