import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../../shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {AuthenticationType} from "../../../shared/enums/authentication-type.enum";
import {AuthenticationConfig} from "../../../models/authentication-config.model";
import {AuthenticationConfigService} from "../../../services/authentication-config.service";
import {MatDialog} from "@angular/material/dialog";
import {CreateComponent as FormLoginCreateComponent} from "./form-login/create.component";
import {DetailsComponent as GoogleAuthDetailsComponent} from "./google/details.component";
import {DetailsComponent as FormLoginDetailsComponent} from "./form-login/details.component";
import {DetailsComponent as ApiDetailsComponent} from "./api/details.component";
import {CreateComponent as GoogleCreateComponent} from "./google/create.component"
import {CreateComponent as ApiCreateComponent} from "./api/create.component";
import {FormComponent as JWTUpdateComponent} from "./jwt/form.component";
import {ConfirmComponent} from "./confirm.component";
import {SessionService} from "../../../shared/services/session.service";
import {Router} from "@angular/router";
import {LogoutPromptComponent} from "./logout-prompt.component";
import {Upload} from "../../../shared/models/upload.model";


@Component({
  selector: 'app-authentication-config',
  templateUrl: './authentication-config.component.html',
  host: {'class': 'page-content-container'},
  styles: [
  ]
})
export class AuthenticationConfigComponent extends BaseComponent implements OnInit {

  authConfig: AuthenticationConfig;
  public AuthenticationType= AuthenticationType;
  isFormLogin:boolean;
  isGoogleOAuth:boolean;
  isNoAuth:boolean;
  public coping : Map<string, boolean>;

  constructor(
    private sessionService:SessionService,
    private router: Router,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public authConfigService: AuthenticationConfigService,
    private matModal: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.coping = new Map<string, boolean>();
    this.fetchAuthConfig();
  }

  checkOrCreate(event, authType: AuthenticationType){

    if(authType == this.authConfig.authenticationType){
      event.source.checked = true;
      this.translate.get('message.common.cannot_remove_authentication').subscribe((res) => {
        this.showNotification(NotificationType.Error, res);
      });
      return;
    }

    else{
      if (!this.checkAuthDetailsStatus(authType) && authType != AuthenticationType.NO_AUTH) {
        this.create(event, authType);
      }
      else {

        if(authType == AuthenticationType.API){
          this.authConfig.isApiEnabled = !this.authConfig.isApiEnabled;
        }
        else{
          this.authConfig.authenticationType = authType;
          if (this.authConfig.authenticationType == this.AuthenticationType.NO_AUTH) window.location.reload();
        }
        this.updateAuthConfig();
        if(authType != AuthenticationType.NO_AUTH)
          this.openRestartConfirmComponent()
        if (this.authConfig.authenticationType == this.AuthenticationType.NO_AUTH) window.location.reload();
      }
    }
  }

  getFormComponent(authType: AuthenticationType){
    switch (authType){
      case AuthenticationType.FORM:
        return FormLoginCreateComponent;
      case AuthenticationType.API:
        return ApiCreateComponent;
      case AuthenticationType.JWT:
        return JWTUpdateComponent;
      case AuthenticationType.GOOGLE:
        return GoogleCreateComponent;
    }
  }

  getViewComponent(authType: AuthenticationType) {
    switch (authType) {
      case AuthenticationType.FORM:
        return FormLoginDetailsComponent;
      case AuthenticationType.GOOGLE:
        return GoogleAuthDetailsComponent;
      case AuthenticationType.API:
        return ApiDetailsComponent;
    }
  }

  create(event, authType: AuthenticationType) {
    const dialogRef = this.matModal.open<BaseComponent>(this.getFormComponent(authType), {
      height: '50vh',
      width: '50%',
      data: {
        authConfig: this.authConfig,
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        if (res && authType!=AuthenticationType.NO_AUTH) {
          this.openRestartConfirmComponent();
        } else {
          event.source.checked = false;
        }
      });
  }

  view(event, authType:AuthenticationType)
  {
    const dialogRef = this.matModal.open<BaseComponent>(this.getViewComponent(authType), {
      height: '50vh',
      width: '50%',
      data: {
        name: authType,
        authConfig: this.authConfig,
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        this.fetchAuthConfig();
      });
  }

  openRestartConfirmComponent() {
    const dialogRef = this.matModal.open<ConfirmComponent>(ConfirmComponent, {
      height: '30vh',
      width: '50%',
      disableClose:true,
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        this.sessionService.logout().subscribe(()=> this.router.navigate(['login']));
      });
  }
  fetchAuthConfig()
  {
    this.authConfigService.find().subscribe(
      (authConfig) => {
        this.authConfig = authConfig;
        this.coping['generating'] = false;
      }, error => {
        this.translate.get('message.common.fetch.failure', {FieldName: 'Authentication Config'}).subscribe((res) => {
          this.showAPIError(error, res);
          this.coping['generating'] = false;
        })
      });
  }

  updateAuthConfig()
  {
    this.authConfigService.update(this.authConfig).subscribe(
      (authConfig) => {
        this.authConfig = authConfig;
      }, error => {
        this.translate.get('message.common.created.failure', {FieldName: 'Authenticaiton Config'}).subscribe((res) => {
          this.showAPIError(error, res);
        })
      });
  }


  checkAuthDetailsStatus(authType:AuthenticationType){
    switch (authType){
      case AuthenticationType.FORM:
        return this.hasFormAuth();
      case AuthenticationType.API:
        return this.hasApi();
      case AuthenticationType.GOOGLE:
        return this.hasGoogleAuth();
    }
  }
  status(type: AuthenticationType) {
    return type == this.authConfig?.authenticationType;
  }
  apiStatus() {
    return this.authConfig?.isApiEnabled;
  }
  hasFormAuth() {
    return this.authConfig?.userName!=null;
  }
  hasGoogleAuth(){
    return this.authConfig?.googleClientId!=null;
  }
  hasApi()
  {
    return this.authConfig?.apiKey!=null;
  }


  changeApiStatus(event) {
    this.authConfig.isApiEnabled = event.source.checked;
    this.updateAuthConfig();
  }

  regenerate(type: AuthenticationType) {
    this.authConfigService.regenerate(type).subscribe();
    if (AuthenticationType.JWT == type) {
      const dialogRef = this.matModal.open<LogoutPromptComponent>(LogoutPromptComponent, {
        height: '30vh',
        width: '50%',
        disableClose:true,
        panelClass: ['mat-dialog', 'rds-none']
      });
      dialogRef.afterClosed()
        .subscribe((res) => {
          this.sessionService.logout().subscribe(()=> this.router.navigate(['login']));
        });
    } else {
      this.coping['generating'] = true;
      this.fetchAuthConfig();
    }
  }
  showCopied(copyText: string) {
    this.coping[copyText] = true;
    setTimeout(() => this.coping[copyText] = false, 10000);
  }
}
