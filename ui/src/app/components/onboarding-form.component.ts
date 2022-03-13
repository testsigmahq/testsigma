import {Component, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {Server} from "../models/server.model";
import {BaseComponent} from "../shared/components/base.component";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {OnboardingService} from "../services/onboarding.service";
import {Onboarding} from "../models/onboarding.model";
import {SessionService} from "../shared/services/session.service";
import {OnboardingGuard} from "../guards/onboarding.guard";
import {RegistrationType} from "../enums/registration-type.enum";
import {MatStepper} from "@angular/material/stepper";
import {fade} from "../shared/animations/animations";
import {RegistrationMedium} from "../enums/registration-medium.enum";
import {ToastrService} from "ngx-toastr";


@Component({
  selector: 'app-onboarding-form',
  templateUrl: './onboarding-form.component.html',
  animations: [fade],
})
export class OnboardingFormComponent extends BaseComponent implements OnInit {

  updateForm: FormGroup;
  private server: Server;
  isCommunityAccess: boolean = true;
  isSendUpdates : boolean = true;
  showVerificationForm: boolean = false;
  private email: string;
  otp: string;
  otpCount: number = 0;
  token: string;
  @ViewChild('captchaRef') captchaRef;
  @ViewChild('stepper') stepper: MatStepper;
  onboarding: Onboarding = new Onboarding();
  isSaving: boolean = false;
  public isActivating: boolean = false;


  constructor(
    private router: Router,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translateService: TranslateService,
    public toastrService: ToastrService,
    public onboardingGuard: OnboardingGuard,
    private onboardingService: OnboardingService,
    private sessionService: SessionService,
    private route: ActivatedRoute,) {
    super(authGuard, notificationsService, translateService, toastrService)
  }

  ngOnInit(): void {
    this.updateForm = new FormGroup({
      'firstName': new FormControl(null,  [Validators.required,Validators.min(4)]),
      'lastName':new FormControl(null, Validators.required),
      'email':new FormControl(null, [Validators.email]),
      'username': new FormControl(null, [Validators.required,Validators.minLength(4)]),
      'password': new FormControl(null, Validators.required),
    })
  }


  clearOrsetEmailValidators(){
    if(!this.isCommunityAccess && !this.isSendUpdates){
      this.clearEmailValidator();
    }
    else
      this.setEmailValidator();
  }

  clearEmailValidator(){
    this.updateForm.controls['email'].clearValidators();
  }
  setEmailValidator(){
    this.clearEmailValidator()
    this.updateForm.controls['email'].setValidators([Validators.email,Validators.required]);
  }
  toggleCommunityUpdateFlag(){
    this.isCommunityAccess = !this.isCommunityAccess;
    if(!this.isCommunityAccess)
      this.isSendUpdates = false
    else
      this.isSendUpdates = true
    console.log("Called", this.isCommunityAccess, this.isSendUpdates)
    this.clearOrsetEmailValidators();
  }
  toggleSendUpdatesFlag(){
    this.isSendUpdates = !this.isSendUpdates;
    this.clearOrsetEmailValidators()
  }


  getOTP(isInline?: boolean) {
    if (isInline) {
      this.isSaving = true;
    }
    this.otpCount++;
    this.onboarding.registrationMedium = RegistrationMedium.ONBOARDING;
    this.onboardingService.getOTP(this.onboarding).subscribe(()=>{
      let message = this.otpCount > 1 ? 'message.onboarding.otp_resent' : 'message.onboarding.otp_sent';
      this.translate.get(message).subscribe((res) => {
        this.showNotification(NotificationType.Success, res);
      })
      this.showVerificationForm= true;
      this.stepper.next();
      this.isSaving = false;
    }, error => {
        this.translate.get('message.onboarding.otp.send_failure').subscribe((res) => {
          this.showAPIError(error, res);
        })
      this.isSaving = false;
      }
    )
  }

  verifyOrNavigate() {
    this.populateOnboardingObject();
    if(this.isCommunityAccess){
      this.isSaving = true;
      this.getOTP();
      return;
    }
    this.submitForm();
  }

  activate() {
    this.isActivating = true;
    this.onboardingService.activate(this.otp).subscribe(() => {
        this.login();
        this.showVerificationForm = false;
        this.stepper.previous();
        this.isActivating = false;
      },
      error => {
        this.translate.get('message.onboarding.otp.activate_failure').subscribe((res) => {
          this.showAPIError(error, res);
          this.isActivating = false;
        })
      }
    );
  }

  submitForm() {
    this.onboardingService.save(this.onboarding).toPromise().then(
      () => {this.login();}
    );
  }

  populateOnboardingObject(){
    this.onboarding.email = this.updateForm.value.email;
    this.onboarding.username = this.updateForm.value.username;
    this.onboarding.password = this.updateForm.value.password;
    this.onboarding.firstName = this.updateForm.value.firstName;
    this.onboarding.lastName = this.updateForm.value.lastName;
    this.onboarding.isCommunityAccess = this.isCommunityAccess;
    this.onboarding.isSendUpdates = this.isSendUpdates;
    this.onboarding.registrationType = RegistrationType.EMAIL;
  }

  navigateBack() {
    this.showVerificationForm=false;
    this.stepper.previous();
  }

  login(){
    this.sessionService.login(
      this.updateForm.getRawValue()['username'],
      this.updateForm.getRawValue()['password'],
    ).subscribe(res => {
      this.onboardingGuard.server = null;
      this.router.navigate(['dashboard'],{queryParams: {showTelemetryNotification: true}});
    }, err => {this.showNotification(NotificationType.Error, "Problem while logging in");})
  }

  requiredFieldsNotFilled(){
    return !this.updateForm.valid;
  }

}
