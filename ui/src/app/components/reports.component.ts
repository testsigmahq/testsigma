import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {AddonActionService} from "../services/addon-action.service";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styles: [
  ],
  host: {'class': 'page-content-container'},
})
export class ReportsComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    public authGuard: AuthenticationGuard
  ) {
  }

  ngOnInit(): void {

  }

}
