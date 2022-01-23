import { Component, OnInit } from '@angular/core';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";

@Component({
  selector: 'app-trial-expiry-notification',
  templateUrl: './trial-expiry-notification.component.html',
  styles: [
  ]
})
export class TrialExpiryNotificationComponent implements OnInit {

  constructor(private authGuard: AuthenticationGuard) { }

  ngOnInit(): void {
  }
}
