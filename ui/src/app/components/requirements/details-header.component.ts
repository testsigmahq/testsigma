import {Component, OnInit} from '@angular/core';
import {Requirement} from "../../models/requirement.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {RequirementsService} from "../../services/requirements.service";

import {BaseComponent} from "../../shared/components/base.component";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-details-header',
  templateUrl: './details-header.component.html',
  styles: []
})
export class DetailsHeaderComponent extends BaseComponent implements OnInit {
  public requirement: Requirement;
  public requirementId: number;
  public isFetchingCompleted: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private requirementsService: RequirementsService,
    private route: ActivatedRoute,
    private router: Router,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.requirementId = this.route.snapshot.params.requirementId;
    this.fetchRequirement();
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
            this.destroyRequirements();
        });
    })
  }

  private fetchRequirement() {
    this.requirementsService.show(this.requirementId).subscribe(res => {
      this.requirement = res;
      this.route.snapshot.params = {...this.route.snapshot.params, ...{versionId: res.workspaceVersionId}};
      this.pushToParent(this.route, this.route.snapshot.params);
      this.isFetchingCompleted = true;
    });
  }

  private destroyRequirements() {
    this.requirementsService.delete(this.requirementId).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Requirement"})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
        this.router.navigate(['/td', this.requirement.workspaceVersionId, 'requirements']);
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: "Requirement"})
        .subscribe(res => this.showAPIError(err, res))
    );
  }

}
