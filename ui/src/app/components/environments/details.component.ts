import { Component, OnInit } from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
//
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {EnvironmentService} from "../../services/environment.service";
import {Environment} from "../../models/environment.model";
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html'
})
export class DetailsComponent extends BaseComponent implements OnInit {
  public environment: Environment;
  public environmentId: number;
  public formGroup: FormGroup = new FormGroup({
    parameters: new FormControl('', [])
  });

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private environmentsService: EnvironmentService,
    private route: ActivatedRoute) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.environmentId = this.route.parent.snapshot.params.environmentId;
    this.fetchEnvironment();
  }

  private fetchEnvironment() {
    this.environmentsService.show(this.environmentId).subscribe(res => {
      this.environment = res;
      this.environment.parametersJson = JSON.stringify(res.parameters);
    });
  }

}
