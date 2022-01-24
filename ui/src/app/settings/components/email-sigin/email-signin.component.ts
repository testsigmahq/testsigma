import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BaseComponent} from "../../../shared/components/base.component";
import {TestsigmaOsConfigService} from "../../../shared/services/testsigma-os-config.service";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {ActivatedRoute} from "@angular/router";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Onboarding} from "../../../models/onboarding.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {RegistrationType} from "../../../enums/registration-type.enum";
import {RegistrationMedium} from "../../../enums/registration-medium.enum";


@Component({
  selector: 'app-testsigma-email-signin',
  templateUrl: './email-sigin.component.html',
  host: {'class': 'page-content-container'},
  styleUrls: []
})
export class EmailSigninComponent extends BaseComponent implements OnInit {
  @Input('showEmailForm') doShowEmailForm : boolean;
  @Output() goBackRequest = new EventEmitter<boolean>();

  public emailSigninForm : FormGroup;
  public showEmailForm : boolean;
  public showActivationForm : boolean;
  public isResent : boolean = false;
  public otp: string;
  public onboarding : Onboarding;
  constructor(public route: ActivatedRoute,
                  public authGuard: AuthenticationGuard,
                  public notificationsService: NotificationsService,
                  public translate: TranslateService,
                  public toastrService: ToastrService,
                  public opensourceService: TestsigmaOsConfigService) {
    super(authGuard, notificationsService, translate, toastrService)
    this.showEmailForm = this.doShowEmailForm;
    this.onboarding = new Onboarding();
    this.addFormControl();
  }

  addFormControl(){
    this.emailSigninForm = new FormGroup({
      firstName : new FormControl(this.onboarding.firstName, [Validators.required]),
      lastName : new FormControl(this.onboarding.lastName, [Validators.required]),
      email : new FormControl(this.onboarding.email, [Validators.email])
    })
  }
  cancelRequest(){
    this.showEmailForm = false;
    this.showActivationForm = false;
    this.goBackRequest.emit(false);
  }

  signup(type: string) {
    this.showEmailForm=true;
  }
  goBack(){
    this.showEmailForm = false;
    this.showActivationForm = false;
  }

  getOTP() {
    this.onboarding = new Onboarding().deserialize(this.emailSigninForm.getRawValue())
    this.onboarding.isSendUpdates = true;
    this.onboarding.isCommunityAccess = true;
    this.onboarding.registrationType = RegistrationType.EMAIL;
    this.onboarding.registrationMedium = RegistrationMedium.CONFIGURATION;
    this.opensourceService.getOTP(this.onboarding).subscribe(()=>{
      this.showActivationForm= true;
      this.showEmailForm=true;
    })
  }

  activate() {
    this.opensourceService.activate(this.otp).subscribe(()=>{
      this.showActivationForm= false;
      this.showEmailForm= false;
      this.opensourceService.show().subscribe(res => this.authGuard.openSource = res);
    })
  }
}
