import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AgentService} from "../services/agent.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {Agent} from "../models/agent.model";
import {AgentType} from "../enums/agent-type.enum";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'agent-form',
  templateUrl: './agent-form.component.html',
  styles: [],
  host: {'class': 'd-flex'}
})
export class AgentFormComponent extends BaseComponent implements OnInit {

  public registerRemoteMachine: boolean = false;
  public unableToConnectLocalAgent: boolean;
  public connectingToAgent: boolean;
  public alreadyRegisteredAsPrivate: boolean;
  public alreadyRegisteredAsPublic: boolean;
  public agentDetailsMissingOnServer: boolean;
  public agentForm: FormGroup;
  public registering: boolean;
  public loading: boolean;
  public agent: Agent;
  public agentId: number;
  public formSubmitted: boolean;
  private hostName: String;
  private port: Number;
  private ip: String;
  public agentUnreachable: boolean;
  public alreadyCreatedAsPrivate: boolean;
  public alreadyCreatedAsPublic: boolean;
  public duplicateAgent: String;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private agentService: AgentService,
    private route: ActivatedRoute,
    private router: Router) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      if (this.route.snapshot.queryParamMap['params'] && this.route.snapshot.queryParamMap['params'].hostName) {
        this.hostName = this.route.snapshot.queryParamMap['params'].hostName;
        this.port = this.route.snapshot.queryParamMap['params'].port;
        this.ip = this.route.snapshot.queryParamMap['params'].ip;
      }
      const allParams = {...params, ...{agentId: this.route.parent.parent.params['_value'].agentId}};
      this.pushToParent(this.route, allParams);
      this.agentId = allParams.agentId;
      if (this.agentId) {
        this.agentService.find(this.agentId).subscribe(
          (res: Agent) => {
            this.agent = res;
            this.addValidations();
          }
        );
      } else {
        this.agent = new Agent();
        this.agent.title = this.hostName ? this.hostName : "localhost";
        this.agent.ipAddress = this.ip ? this.ip : "127.0.0.1";
        this.addValidations();
      }
    });
    this.pingAgent();
  }

  addValidations() {
    this.agentForm = new FormGroup({
      title: new FormControl(this.agent.title, [
        Validators.required,
        Validators.minLength(4)
      ]),
    });
  }

  pingAgent() {
    this.loading = true;
    if (!this.agentId) {
      this.agentService.ping()
        .subscribe({
          next: (response) => this.handleSuccessfulAgentPing(response),
          error: (err: any) => this.handleFailureAgentPing(err)
        });
    } else {
      this.loading = false;
    }
  }

  handleSuccessfulAgentPing(response) {
    if (response.uniqueId) {
      // this.checkForVisibility(response.uniqueId);
    }
    this.loading = false;
  }

  handleFailureAgentPing(err: any) {
    console.log("Failed to ping agent. Error --- ", err);
    this.unableToConnectLocalAgent = true;
    /*this.translate.get('agents.form.create.failure').subscribe((res: string) => {
      this.showNotification(NotificationType.Error, res);
    });*/
    this.loading = false;
  }

  saveAgent() {
    this.loading = true;
    this.formSubmitted = true;
    if (this.agentForm.valid) {
      this.agentService.create(this.agent).subscribe({
        next: (agent) => {
          this.registerAgent(agent);
        },
        error: (error) => {
          var failureMessage = "agents.form.create.failure";
          if (error == 'duplicate') {
            failureMessage = "agents.form.create.duplicate";
            this.checkForDuplicateNameAccess(this.agent.name)
          }
          this.loading = false;
          this.translate.get(failureMessage).subscribe((res: string) => {
            this.showNotification(NotificationType.Error, res);
          });
        }
      });
    } else {
      this.loading = false;
    }
  }

  updateAgent() {
    this.formSubmitted = true;
    if (this.agentForm.valid) {
      this.agentService.update(this.agent.id, this.agent).subscribe({
        next: () => {
          this.translate.get("agents.form.update.success").subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
            this.router.navigate(["agents"]);
          });
        },
        error: () => {
          this.translate.get("agents.form.update.success").subscribe((res: string) => {
            this.showNotification(NotificationType.Error, res);
          });
        }
      });
    }
  }

  registerAgent(agent) {
    this.registering = true;
    this.agentService.registerAgent(agent).subscribe({
      next: () => {
        this.translate.get("agents.form.register.success").subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
        });
        this.agentUnreachable = false;
        this.registering = false;
        this.router.navigate(["agents", agent.id]);
      },
      error: (err: any) => {
        this.registering = false;
        this.loading = false;
        if(true){
          err.status == 0
          this.agentUnreachable = true;
        }
        this.translate.get("agents.form.register.failure").subscribe((res: string) => {
          this.showNotification(NotificationType.Error, res);
        });
      }
    });
  }

  checkForVisibility(uniqueId) {
    this.agentService.findByUuid(uniqueId).subscribe((response) => {
      this.alreadyRegisteredAsPublic = true;
    }, error => this.agentDetailsMissingOnServer = true);
  }

  checkForDuplicateNameAccess(name) {
    this.agentService.findAllPrivateAndPublic("agent.title:"+ name).subscribe((res) => {
      let response = res.content[0];
      this.duplicateAgent = response.title;
    }, error => this.agentDetailsMissingOnServer = true);
  }

  disableAgentCreation() {
    return this.unableToConnectLocalAgent || this.loading || this.alreadyRegisteredAsPrivate
    || this.alreadyRegisteredAsPublic || (this.duplicateAgent && this.duplicateAgent == this.agent.title)
  }
}
