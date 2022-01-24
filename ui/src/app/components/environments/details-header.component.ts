import { Component, OnInit } from '@angular/core';
import {Environment} from "../../models/environment.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {EnvironmentService} from "../../services/environment.service";
//
import { NotificationsService, NotificationType } from 'angular2-notifications';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import {BaseComponent} from "../../shared/components/base.component";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-details-header',
  templateUrl: './details-header.component.html',
  styles: [
  ]
})
export class DetailsHeaderComponent extends BaseComponent implements OnInit {
  isFetchingCompleted: boolean;
  public environmentId: number;
  public environment: Environment;
  versionId: Number;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private environmentsService: EnvironmentService,
    private route: ActivatedRoute,
    private router: Router,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.versionId = this.route.snapshot.queryParams['v'];
    this.environmentId = this.route.snapshot.params.environmentId;
    this.route.snapshot.params = {...this.route.snapshot.params, ...{v: this.versionId}};
    this.pushToParent(this.route, this.route.snapshot.params);
    this.fetchEnvironment();
  }

  openDeleteDialog() {
    this.translate.get("message.common.confirmation.default").subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {description: res},
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result)
            this.destroyEnvironments();
        });
    })
  }

  private fetchEnvironment() {
    this.environmentsService.show(this.environmentId).subscribe(res => {
      this.environment = res;
      this.environment.parametersJson = JSON.stringify(res.parameters);
      this.isFetchingCompleted = true;
    });
  }


  private destroyEnvironments() {
    this.environmentsService.delete(this.environmentId).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Environments"})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
        this.router.navigate(['/td', this.versionId, 'environments']);
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: "Environments"})
        .subscribe(res => {
          this.showAPIError(err, res);
        })
    );
  }

}
