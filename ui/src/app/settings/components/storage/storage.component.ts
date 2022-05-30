import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../../shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {StorageType} from "../../../enums/storage-type.enum";
import {DetailsComponent as AwsStorageDetailsComponent} from "./aws/details.component";
import {DetailsComponent as AzureStorageDetailsComponent} from "./azure/details.component";
import {DetailsComponent as LocalStorageDetailsComponent} from "./local/details.component";

import {CreateComponent as AwsStorageCreateComponent} from "./aws/create.component";
import {CreateComponent as AzureStorageCreateComponent} from "./azure/create.component";
import {CreateComponent as LocalStorageCreateComponent} from "./local/create.component";
import {MatDialog} from "@angular/material/dialog";
import {StorageConfig} from "../../models/storage-config";
import {StorageConfigService} from "../../../services/storage-config.service";
import {TestsigmaOsConfigService} from "../../../shared/services/testsigma-os-config.service";
import {Router} from "@angular/router";
import {WarningModalComponent} from "../../../shared/components/webcomponents/warning-modal.component";

@Component({
  selector: 'app-storage',
  templateUrl: './storage.component.html',
  host: {'class': 'page-content-container'},
  styles: [

  ]
})
export class StorageComponent extends BaseComponent implements OnInit {

  public StorageType = StorageType;
  storageType:StorageType;
  saving = false;
  isAWS:boolean;
  isAzure:boolean;
  isLocal:boolean;
  isTestsigmaCloud = false;

  storageConfig:StorageConfig = new StorageConfig();

  constructor(
    public authGuard: AuthenticationGuard,
    private router: Router,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private storageService : StorageConfigService,
    private openSourceService : TestsigmaOsConfigService,
    private matModal: MatDialog) {

    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {

    this.getStorageConfig();

  }

  getViewComponent(storageType: StorageType) {
    switch (storageType) {
      case StorageType.AWS_S3:
        return AwsStorageDetailsComponent;
      case StorageType.AZURE_BLOB:
        return AzureStorageDetailsComponent;
      case StorageType.ON_PREMISE:
        return LocalStorageDetailsComponent;
    }
  }

  getFormComponent(storageType: StorageType)
  {
    switch (storageType){
      case StorageType.AWS_S3:
        return AwsStorageCreateComponent;
      case StorageType.AZURE_BLOB:
        return AzureStorageCreateComponent;
      case StorageType.ON_PREMISE:
        return LocalStorageCreateComponent
    }
  }

  view(event, storageType:StorageType)
  {
    const dialogRef = this.matModal.open<BaseComponent>(this.getViewComponent(storageType), {
      height: '50vh',
      width: '50%',
      data: {
         name: storageType,
         storageConfig: this.storageConfig,
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        this.getStorageConfig();
      });
  }

  create(event, storageType: StorageType) {
    const dialogRef = this.matModal.open<BaseComponent>(this.getFormComponent(storageType), {
      height: '75vh',
      width: '50%',
      data: {
        storageConfig: this.storageConfig,
      },
      panelClass: ['mat-overlay', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        if (res){
          this.changeStorageStatus(storageType,true);
        }
        else {
          this.changeStorageStatus(storageType, false);
          event.source.checked = false;
        }
        this.getStorageConfig();
      });
  }

  checkOrCreate(event, storageType: StorageType) {
    if(storageType == this.storageConfig.storageType) {
      event.source.checked = true;
      this.translate.get('message.common.cannot_remove_storage').subscribe((res) => {
        this.showNotification(NotificationType.Error, res);
      });
      return;
    }
    if(storageType==StorageType.TESTSIGMA)
    {
      if(!this.isTestsigmaCloud) {
        this.router.navigate(['settings/testsigma'])
        return;
      }
    }

    if(!this.checkStorageStatus(storageType))
    {
      this.create(event,storageType);
    }
    else {
      this.storageConfig.storageType = storageType;
      this.updateStorageConfig();
    }
  }

  checkStorageStatus(storageType: StorageType)
  {
    switch (storageType) {
      case StorageType.AWS_S3:
        return this.isAWS;
      case StorageType.AZURE_BLOB:
        return this.isAzure;
      case StorageType.ON_PREMISE:
        return this.isLocal;
      case StorageType.TESTSIGMA:
        return this.isTestsigmaCloud
    }
  }

  changeStorageStatus(storageType: StorageType, value:boolean)
  {
    switch (storageType) {
      case StorageType.AWS_S3:
        this.isAWS = value;
        break;
      case StorageType.AZURE_BLOB:
        this.isAzure = value;
        break;
      case StorageType.ON_PREMISE:
        this.isLocal = value;
        break;
      case StorageType.TESTSIGMA:
        this.isTestsigmaCloud = value;
        break;
    }
  }

  getStorageConfig()
  {
    this.storageService.find().subscribe(
      (storage) => {
        this.storageConfig = storage;
        this.checkDetails();
      }, error => {
        this.translate.get('message.common.fetch.failure', {FieldName: 'Storage Config'}).subscribe((res) => {
          this.showAPIError(error, res);
        })
      });
  }
  updateStorageConfig()
  {
    this.matModal.open(WarningModalComponent, {
      maxHeight: '20rem',
      width: '450px',
      panelClass: ['mat-dialog', 'rds-none', 'delete-confirm'],
      data: {
        title:this.translate.instant("message.common.warning"),
        message: this.translate.instant("settings.storage.switch.storage.message")
      }});
    this.storageService.update(this.storageConfig).subscribe(
      (storage) => {
        this.storageConfig = storage;
        this.checkDetails();
      }, error => {
        this.translate.get('message.common.created.failure', {FieldName: 'Storage Config'}).subscribe((res) => {
          this.showAPIError(error, res, this.storageConfig.storageType);
        })
      });
  }
  checkAWS()
  {
    this.isAWS = (this.storageConfig.awsBucketName!=null);
  }
  checkAzure()
  {
    this.isAzure = (this.storageConfig.azureContainerName!=null);
  }
  checkLocal()
  {
    this.isLocal = (this.storageConfig.onPremiseRootDirectory!=null);
  }
  isTestsigmaCloudEnabled()
  {

    this.openSourceService.show().subscribe(res=>
    {
      this.isTestsigmaCloud = res.accessKey != null;
    });
  }
  checkDetails()
  {
    this.checkAWS();
    this.checkAzure();
    this.checkLocal();
    this.isTestsigmaCloudEnabled();
  }
  status(type: StorageType) {
    if(this.storageConfig.storageType!=null)
      return (this.storageConfig.storageType == type);
    return false;
  }
}
