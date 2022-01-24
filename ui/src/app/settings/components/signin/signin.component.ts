import {Component, Input, OnInit} from '@angular/core';
import {BaseComponent} from '../../../shared/components/base.component';
import {ActivatedRoute} from '@angular/router';
import {AuthenticationGuard} from '../../../shared/guards/authentication.guard';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestsigmaOsConfigService} from "../../../shared/services/testsigma-os-config.service";
import {TestsigmaOSConfig} from "../../../shared/models/testsigma-os-config.model";
import {RegistrationType} from "../../../enums/registration-type.enum";


@Component({
  selector: 'app-testsigma-signin',
  templateUrl: './signin.component.html',
  host: {'class': 'page-content-container'},
})
export class SigninComponent extends BaseComponent implements OnInit {
  @Input('isAddon') isAddon: boolean;
  public showEmailForm: boolean = false;
  private signupWindow: Window;
  public email: string;
  public otp: string;
  public showActivationForm: boolean = false;

  constructor(
    public route: ActivatedRoute,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public opensourceService: TestsigmaOsConfigService) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
  }

  cancel(value: boolean){
    this.showEmailForm = value;
    window.location.reload();
  }

  signup(type: string) {
    switch (type) {
      case 'google':
        this.openOSSignUpWindow(this.authGuard.openSource.url+"/oauth2/authorization/google");
        break;
      case 'github':
        this.openOSSignUpWindow(this.authGuard.openSource.url+"/oauth2/authorization/github");
        break;
      default:
        this.showEmailForm=true;
    }
  }

  openOSSignUpWindow(url) {
    let left = (screen.width) ? (screen.width-400)/2 : 0;
    let top = (screen.height) ? (screen.height-500)/2 : 0;
    let settings =
      'height=500,width=400,top='+top+',left='+left+',scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no, status=yes,resizable'
    this.signupWindow = window.open(url, 'testsigma_os_signup',settings);
    window.onmessage = (message) => {
      if(message.data == "PUBLIC_EMAIL_USER_NOT_FOUND"){
        this.translate.get("message.common.signup.failure", {Provider: "Github"})
          .subscribe(key => this.showAPIError('UnExpected Error', key));
        return;
      }
      console.log(message);
      this.signupWindow.postMessage('THANK_YOU_CLOSE_IT', '*');
      let opensource = new TestsigmaOSConfig();
      opensource.deserialize(this.authGuard.openSource.serialize())
      opensource.accessKey = message.data;
      opensource.userName = "Testsigma OS";
      this.opensourceService.create(opensource).subscribe(opensource => {
        this.authGuard.openSource.accessKey = opensource.accessKey;
      })
    }
  }

}
