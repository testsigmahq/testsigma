import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Pageable} from '../../../shared/models/pageable';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {AuthenticationGuard} from '../../../shared/guards/authentication.guard';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from '../../../shared/components/base.component';
import {ProvisioningProfileService} from '../../../services/provisioning-profile.service';
import {ProvisioningProfile} from '../../../models/provisioning-profile.model';
import {Page} from '../../../shared/models/page';
import {FormControl, Validators} from "@angular/forms";


@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  host: {'class': 'page-content-container'},
  styles: [
  ]
})
export class FormComponent extends BaseComponent implements OnInit {
  @ViewChild('name') nameInput: ElementRef;
  public provisioningProfile: ProvisioningProfile;
  public saving = false;
  public uploadFile: any;
  public enableForm:boolean=true;
  public nameFormControl: FormControl;
  public fetching:boolean=false;

  constructor(
    public route: ActivatedRoute,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    public provisioningProfileService: ProvisioningProfileService,
  ) { super(authGuard, notificationsService, translate, toastrService) }

  get showNameInput(): boolean {
    return (this.provisioningProfile?.id && this.enableForm) || (!this.provisioningProfile?.id);
  }

  ngOnInit(): void {
    this.nameFormControl = new FormControl('', [Validators.required, this.noWhitespaceValidator]);
    this.route.params.subscribe((params: Params) => {
      if(params.id){
        this.enableForm=false;
        this.fetching=true;
        this.getProvisioningProfile(params.id);
      }
    });
    this.provisioningProfile = new ProvisioningProfile();
    this.provisioningProfile.status = 'CSR_REQUESTED';
  }

  getProvisioningProfile(id): void {
    this.provisioningProfileService.show(id)
      .subscribe(data => {
        this.fetching=false;
        this.provisioningProfile = data;
        this.nameFormControl.setValue(this.provisioningProfile.name);
      }, error => console.log(error));
  };

  generateCsr(){
    this.provisioningProfile.name = this.nameFormControl.value;
    this.provisioningProfileService.create(this.provisioningProfile).subscribe((res) => {
        this.saving = false;
        this.provisioningProfile = res;
        this.translate.get('message.common.created.success', {FieldName: 'Provisioning Profile' }).subscribe((res) => {
          this.showNotification(NotificationType.Success, res);
          this.router.onSameUrlNavigation = 'reload';
          this.router.navigate(['settings','provisioning_profiles', this.provisioningProfile.id]);
        })
      },
      error => {
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: 'Provisioning Profile'}).subscribe((res) => {
          this.showAPIError(error, res,'Provisioning Profile');
        })
      })
  }


  private get formData(): FormData {
    let formData = new FormData();
    formData.append("cer", this.uploadFile || new File([""], ""));
    formData.append("name", this.provisioningProfile.name);
    formData.append("teamId", this.provisioningProfile.teamId );
    formData.append("provisioning_profile_id", this.provisioningProfile.id.toString() );
    return formData
  }

  uploadSizeCheck(event){
    this.uploadFile = event.target.files[0];
    if (this.uploadFile && this.uploadFile.size && this.uploadFile.size / 1024 / 1024 < 5) {

      }  else if (this.uploadFile.size / 1024 / 1024 >= 5) {
      this.translate.get("message.common.upload.limit_fail").subscribe((res: string) => {
        this.showNotification(NotificationType.Error, res);
        event.target.value = '';
        return;
      });
    }
  }

  certificateUpload(event) {
    this.uploadSizeCheck(event);
    this.provisioningProfileService.update(this.provisioningProfile, this.formData).subscribe(() => {
        this.translate.get("message.common.upload.success", {FieldName: 'Certificate'}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
          this.ngOnInit();
        });
      }, error => {
        this.translate.get("message.common.upload.failure", {FieldName: 'Certificate'}).subscribe((res: string) => {
          this.showAPIError(error, res);
        });
    })
  }

  private get profileFormData(): FormData {
    let formData = new FormData();
    formData.append("provisioningProfile", this.uploadFile || new File([""], ""));
    formData.append("name", this.provisioningProfile.name);
    formData.append("teamId", this.provisioningProfile.teamId );
    formData.append("provisioning_profile_id", this.provisioningProfile.id.toString() );
    return formData
  }

  profileUpload(event) {
    this.uploadSizeCheck(event);
      this.provisioningProfileService.update(this.provisioningProfile, this.profileFormData).subscribe(() => {
        this.translate.get("message.common.upload.success", {FieldName: 'Provisioning Profile'}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
          this.ngOnInit();
        });
      }, error => {
        this.translate.get("message.common.upload.failure", {FieldName: 'Provisioning Profile'}).subscribe((res: string) => {
          this.showAPIError(error, res);
        });
    })
  }

  saveProvisioningProfile() {
    let formData = new FormData();
    formData.append("name", this.nameFormControl.value);
    this.provisioningProfileService.update(this.provisioningProfile, formData).subscribe(() => {
      this.translate.get("message.common.update.success", {FieldName: 'Provisioning Profile'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.ngOnInit();
      });
    }, error => {
      this.translate.get("message.common.update.failure", {FieldName: 'Provisioning Profile'}).subscribe((res: string) => {
        this.showAPIError(error, res);
      });
    })
  }

  enableInputForm() {
    this.enableForm=true;
    this.nameFormControl.setValue(this.provisioningProfile.name);
    this.setFocusOnInput();
  }

  setFocusOnInput(){
    if(this.nameInput?.nativeElement)
      this.nameInput.nativeElement.focus();
    else
      setTimeout(()=>this.setFocusOnInput(), 200);
  }

  hideInputForm() {
    this.enableForm=false;
    this.nameFormControl.setValue(this.provisioningProfile.name);
  }
}
