import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {Router} from "@angular/router";
import {Server} from "../../models/server.model";
import {ServerService} from "../../services/server.service";
import { SessionService } from 'app/shared/services/session.service';
import {ConsentGuard} from "../../guards/consent.guard";

@Component({
  selector: 'app-consent',
  host: {'class': 'page-content-container'},
  templateUrl: './consent.component.html',
  styles: [
  ]
})
export class ConsentComponent extends BaseComponent implements OnInit {

  server: Server = new Server();
  formGroup: FormGroup;
  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public consentGuard: ConsentGuard,
    private router: Router,
    private serverService:ServerService,
    private sessionService: SessionService
  ) {
    super(authGuard,notificationsService, translate);
  }

  ngOnInit(): void {
    this.server.consent = true;
    this.formGroup = new FormGroup({
      'agree': new FormControl(null,  Validators.required),
    })
  }
  onSubmit(){
    this.server.consentRequestDone = true;
    this.server.consent = this.formGroup.value.agree==null ? false : this.formGroup.value.agree;
    this.consentGuard.server = this.server;
    this.serverService.update(this.server).subscribe(
      (server) => {
        this.router.navigate(["dashboard"]);
      }, error => {
        this.translate.get('message.common.update.failure', {FieldName: 'Consent'}).subscribe((res) => {
          this.showAPIError(error, res);
        })
      });

  }
  logout() {
    this.sessionService.logout().subscribe(()=> this.router.navigate(['login']));
  }
}
