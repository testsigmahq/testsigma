import { Component, OnInit } from '@angular/core';
import {filter} from "rxjs/operators";
import {NavigationEnd, NavigationStart, Router} from "@angular/router";
import instantiate = WebAssembly.instantiate;

@Component({
  selector: 'app-route-loading',
  template: `
    <div
      *ngIf="isLoading"
      class="route-loading d-flex">
      <div class="loader loader-small vertical-align mr-10"></div>
      <div class="vertical-align m-auto" [translate]="'Loading...'"></div>
    </div>
  `,
  styles: [
  ]
})
export class RouteLoadingComponent implements OnInit {
  public isLoading: boolean = false;
  constructor(
    public router: Router
  ) {
    router.events.pipe().subscribe((event) => {
      if(event instanceof NavigationEnd) {
        this.isLoading = false;
      } else if(event instanceof NavigationStart) {
        this.isLoading = true;
      }
    });
  }

  ngOnInit(): void {
  }

}
