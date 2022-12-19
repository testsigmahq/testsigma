import {Component, OnInit} from '@angular/core';
import {IntegrationsService} from "../../../shared/services/integrations.service";
import {Integrations} from "../../../shared/models/integrations.model";
import {MatDialog} from "@angular/material/dialog";
import {CreateComponent as FreshReleaseCreateComponent} from "./freshrelease/create.component";
import {CreateComponent as TrelloCreateComponent} from './trello/create.component';
import {DetailsComponent as TrelloDetailsComponent} from './trello/details.component';
import {DetailsComponent as FreshReleaseDetailsComponent} from "./freshrelease/details.component";
import {CreateComponent as JiraCreateComponent} from "./jira/create.component";
import {DetailsComponent as JiraDetailsComponent} from "./jira/details.component";
import {DetailsComponent as AzureDetailsComponent} from "./azure/details.component";
import {CreateComponent as AzureCreateComponent} from "./azure/create.component";
import {DetailsComponent as LinearDetailsComponent} from "./linear/details.component";
import {CreateComponent as LinearCreateComponent} from "./linear/create.component";
import {Integration} from "../../../shared/enums/integration.enum";
import {BaseComponent} from "../../../shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {ActivatedRoute, Router} from '@angular/router';
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {CreateComponent} from "./privateGrid/create.component";
import {DetailsComponent} from "./privateGrid/details.component";
import {CreateComponent as XrayCreateComponent} from "./xray/create.component";
import {DetailsComponent as XrayDetailsComponent} from "./xray/details.component";

@Component({
  selector: 'app-plugins',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
})
export class ListComponent extends BaseComponent implements OnInit {
  plugins: Integrations[] = [];
  public Integration = Integration;

  constructor(
    public authGuard: AuthenticationGuard,
    public route: ActivatedRoute,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public router: Router,
    private integrationsService: IntegrationsService,
    private matModal: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get xrayApplication(){
    return this.plugins.find(plug => plug.isXray);
  }

  get bugReport() : boolean{
    return this.router.url.indexOf("bug_report") > -1
  }
  get testLab() : boolean{
    return this.router.url.indexOf("test_lab") > -1
  }
  get productManagement() : boolean{
    return this.router.url.indexOf("product_management") > -1
  }
  get ciCd() : boolean{
    return this.router.url.indexOf("ci_cd") > -1
  }

  get all() : boolean{
    return this.router.url.endsWith("plugs");
  }
  get freshReleaseApplication() {
    return this.plugins.find(plug => plug.isFreshrelease)
  }

  get mantisApplication(){
    return this.plugins.find(plug => plug.isMantis);
  }

  get backLogApplication(){
    return this.plugins.find(plug => plug.isBackLog);
  }

  get zepelApplication(){
    return this.plugins.find(plug => plug.isZepel);
  }

  get clickUpApplication() {
    return this.plugins.find(plug => plug.isClickUp);
  }

  get bugZillaApplication(){
    return this.plugins.find(plug => plug.isBugzilla);
  }

  get jiraApplication() {
    return this.plugins.find(plug => plug.isJira);
  }

  get azureApplication() {
    return this.plugins.find(plug => plug.isAzure);
  }

  get youtrackApplication() {
    return this.plugins.find(plug => plug.isYoutrack);
  }

  get trelloApplication() {
    return this.plugins.find(plug => plug.isTrello);
  }

  get linearApplication() {
    return this.plugins.find(plug => plug.isLinear);
  }

  get testsigmaLabApplication() {
    return this.plugins.find(plug => plug.isTestsigmaLab);
  }

  get privateGridApplication() {
    return this.plugins.find(plug => plug.isPrivateLab);
  }

  get testProject(){
    return this.plugins.find(plug => plug.isTestProject);
  }

  ngOnInit(): void {
    this.fetchPlugins();
    this.pushToParent(this.route, this.route.params);
  }

  fetchPlugins() {
    this.integrationsService.findAll().subscribe(data => {
      this.plugins = data;
    }, error => console.log(error));
  }

  toggleApplication(event, integration: Integration, app?: Integrations) {
    if(integration == Integration.TestsigmaLab) {
      this.router.navigate(['/settings', 'testsigma']);
      return;
    }
    if(integration == Integration.TestProjectImport) {
      let integrations:Integrations = new Integrations();
      integrations.name = integration
      integrations.workspaceId = (Object.keys(Integration).indexOf(integration) + 1);
      integrations.url = integration;
      integrations.username = integration;
      integrations.password = integration;
      if(event.checked){
        this.integrationsService.create(integrations).subscribe(res => console.log(res))
        console.log(event)
      } else {
        this.integrationsService.delete(integrations.workspaceId).subscribe();
      }
      return;
    }
    if (event.source.checked)
      this.create(event, integration);
    else
      this.viewDetails(integration, app, event);
  }

  create(event, integration: Integration) {
    const dialogRef = this.matModal.open<BaseComponent>(this.getFormComponent(integration), {
      height: '75vh',
      width: '50%',
      data: {
        integrationName: integration,
        workspaceId: (Object.keys(Integration).indexOf(integration) + 1),
        name: integration,
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        if (res)
          this.fetchPlugins();
        else
          event.source.checked = false;
      });
  }

  viewDetails(integration: Integration, app: Integrations, event?) {
    if(integration == Integration.TestsigmaLab){
      this.router.navigate(['/settings', 'testsigma']);
      return;
    }
    const dialogRef = this.matModal.open<BaseComponent>(this.getViewComponent(integration), {
      height: '400px',
      width: '50%',
      data: {
        workspaceId: app.id,
        name: integration,
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res)
        this.fetchPlugins();
      else if (event)
        event.source.checked = true
    })
  }

  getViewComponent(integration: Integration) {
    switch (integration) {
      case Integration.Jira:
        return JiraDetailsComponent;
      case Integration.XrayCloud:
        return XrayDetailsComponent;
      case Integration.Mantis:
      case Integration.BackLog:
      case Integration.Zepel:
      case Integration.BugZilla:
      case Integration.Freshrelease:
        return FreshReleaseDetailsComponent;
      case Integration.Trello:
        return  TrelloDetailsComponent;
      case Integration.Linear:
      case Integration.ClickUp:
        return LinearDetailsComponent;
      case Integration.Youtrack:
      case Integration.Azure:
        return AzureDetailsComponent;
      case Integration.PrivateGrid:
        return DetailsComponent;
    }
  }

  getFormComponent(integration: Integration) {
    switch (integration) {
      case Integration.Jira:
        return JiraCreateComponent;
      case Integration.XrayCloud:
        return XrayCreateComponent;
      case Integration.Mantis:
      case Integration.BackLog:
      case Integration.Zepel:
      case Integration.BugZilla:
      case Integration.Freshrelease:
        return FreshReleaseCreateComponent;
      case Integration.Trello:
        return TrelloCreateComponent;
      case Integration.Linear:
      case Integration.ClickUp:
        return LinearCreateComponent;
      case Integration.Azure:
      case Integration.Youtrack:
        return AzureCreateComponent;
      case Integration.PrivateGrid:
        return CreateComponent;
    }
  }

  // isWriteAccess(name) {
  //   let entityAccessList = this.authGuard.session.user.privileges.filter((access)=> access.entity.name != "test_group_report");
  //   return entityAccessList.filter(access => {
  //     return access.getAccessLevel(name) &&
  //       (AccessLevel[access.accessLevel] == AccessLevel.WRITE || AccessLevel[access.accessLevel] == AccessLevel.FULL_ACCESS)}
  //   ).length > 0;
  // }
}
