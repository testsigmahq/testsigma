import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {KibbutzActionService} from "../services/kibbutz-action.service";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";

@Component({
  selector: 'app-kibbutz-app',
  templateUrl: './kibbutz-app.component.html',
  styles: [
  ],
  host: {'class': 'page-content-container'},
})
export class KibbutzAppComponent implements OnInit {
  public isCreate: string;
  constructor(
    private route: ActivatedRoute,
    public authGuard: AuthenticationGuard,
    private kibbutzActionService: KibbutzActionService
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(res => {
      this.isCreate = res.create;
    });
    this.kibbutzActionService.registerListenerAllWindowMessage();
  }

}
