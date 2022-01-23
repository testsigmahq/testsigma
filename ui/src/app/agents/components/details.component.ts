import {Component, OnInit} from '@angular/core';
import {Agent} from "app/agents/models/agent.model";
import {AgentService} from "app/agents/services/agent.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {BaseComponent} from "app/shared/components/base.component";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {MatDialog} from '@angular/material/dialog';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styles: []
})
export class DetailsComponent extends BaseComponent implements OnInit {
  public agent: Agent;
  private agentId: Number;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private agentService: AgentService,
    private router: Router,
    private dialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
    // this.subscribeRouterEvents();
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      this.agentId = params.agentId;
      if (this.agentId) {
        this.fetchAgent(this.agentId);
      }
    });
  }


  fetchAgent(agentId: Number): void {
    this.agentService.find(agentId).subscribe(
      (res: Agent) => {
        this.agent = res;
      }
    );
  }

  deleteAgent(agent) {
    this.translate.get("agents.delete.confirmation.message").subscribe((res) => {
      const dialogRef = this.dialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            this.agentService.destroy(agent.id).subscribe({
              next: () => {
                this.translate.get('agents.delete.success').subscribe((res: string) => {
                  this.showNotification(NotificationType.Success, res);
                  this.router.navigate(["agents"]);
                });

              },
              error: (error) => {
                if (error.status == "400") {
                  this.showNotification(NotificationType.Error, error.error);
                } else {
                  this.translate.get("agents.delete.failure").subscribe((res: string) => {
                    this.showNotification(NotificationType.Error, res);
                  });
                }
              }
            })
          }
        });
    });
  }
}
