import { Component, OnInit } from '@angular/core';
import {BaseComponent} from '../../../shared/components/base.component';
import {Pageable} from '../../../shared/models/pageable';
import {ActivatedRoute} from '@angular/router';
import {AuthenticationGuard} from '../../../shared/guards/authentication.guard';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MatDialog} from '@angular/material/dialog';
import {Page} from '../../../shared/models/page';
import {ProvisioningProfileService} from '../../../services/provisioning-profile.service';
import {ProvisioningProfile} from '../../../models/provisioning-profile.model';
import {ConfirmationModalComponent} from '../../../shared/components/webcomponents/confirmation-modal.component';


@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
  styles: [
  ]
})
export class ListComponent extends BaseComponent implements OnInit {
  public provisioningProfileList: Page<ProvisioningProfile>;
  currentPage = new Pageable();
  fetchingCompleted: boolean;
  public searchQuery = "";
  public query: string;
  public selectedProvisionProfile = [];
  public isFiltered: Boolean = false;

  constructor(
    public route: ActivatedRoute,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public provisioningProfileService: ProvisioningProfileService,
    private matModal: MatDialog,
    private matDialog: MatDialog,
  ) { super(authGuard, notificationsService, translate, toastrService) }

  ngOnInit(): void {
    this.pushToParent(this.route, this.route.params);
    this.fetchProvisioningList();
  }

  fetchProvisioningList(): void {
    let query = this.searchQuery;
    this.provisioningProfileService.findAll(query).subscribe(data => {
      this.fetchingCompleted = true;
      this.currentPage = data.pageable;
      this.provisioningProfileList = data;
    });
  }


  openDeleteDialog(id?) {
    const message = id ? "message.common.confirmation.default" : "settings.provisioning.bulk_delete.confirmation.message";
    this.translate.get(message, {FieldName: this.selectedProvisionProfile.length}).subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            if (id)
              this.destroyProvioningProfile(id);
          }
        });
    })
  }

  private destroyProvioningProfile(id: any) {
    this.provisioningProfileService.delete(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: 'Provision Profile'})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchProvisioningList();
        this.selectedProvisionProfile = [];
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: 'Provision Profile'})
        .subscribe(res => this.showNotification(NotificationType.Error, res))
    );
  }


  search(term: string) {
    if (term) {
      this.isFiltered = true;
      this.searchQuery = "name:*" + term + "*";
    } else {
      this.isFiltered = false;
      this.searchQuery = "";
    }
    this.fetchProvisioningList()
  }

}
