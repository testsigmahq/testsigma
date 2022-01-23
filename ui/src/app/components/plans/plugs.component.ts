import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-plugs',
  templateUrl: './plugs.component.html',
  styles: [
  ],
  host: {
    'class': 'd-flex ts-col-100 h-100'
  }
})
export class PlugsComponent extends BaseComponent implements OnInit {
  public testPlanId: number

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.testPlanId = this.route.parent.snapshot.params.testPlanId;
  }

}
