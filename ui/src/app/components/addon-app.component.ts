import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {AddonActionService} from "../services/addon-action.service";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";

@Component({
  selector: 'app-addon-app',
  templateUrl: './addon-app.component.html',
  styles: [
  ],
  host: {'class': 'page-content-container'},
})
export class AddonAppComponent implements OnInit {
  public isCreate: string;
  constructor(
    private route: ActivatedRoute,
    public authGuard: AuthenticationGuard,
    private addonActionService: AddonActionService
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(res => {
      this.isCreate = res.create;
    });
    this.addonActionService.registerListenerAllWindowMessage();
  }

}
