import { Component, OnInit } from '@angular/core';
import {Server} from "../../../models/server.model";
import {ServerService} from "../../../services/server.service";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {BaseComponent} from "../../../shared/components/base.component";

@Component({
  selector: 'app-telemetry',
  host: {'class': 'page-content-container'},
  templateUrl: './telemetry.component.html',
  styles: [
  ]
})
export class TelemetryComponent extends BaseComponent implements OnInit {

  disableTelemetry: boolean = false;
  server: Server = new Server();
  public saving: boolean = false;
  public showAction: boolean = false;
  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private serverService: ServerService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.fetchServerConfig();
  }

  submit() {
    this.saving = true;
    this.server.consent = !this.disableTelemetry;
    this.serverService.update(this.server).subscribe(()=> {
      this.translate.get("You have successfully updated Telemetry settings").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.saving = false;
        this.showAction = false;
      });
    }, error => {
      this.saving = false;
    });

  }
  fetchServerConfig(){
    this.serverService.find().subscribe( res => {
        this.disableTelemetry = !res.consent;
      }

    )

  }
}
