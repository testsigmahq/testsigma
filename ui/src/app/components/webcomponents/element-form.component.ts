import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {Element} from "../../models/element.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {ActivatedRoute, Router} from "@angular/router";

import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from "../../shared/components/base.component";
import {ElementService} from "../../shared/services/element.service";
import {ElementLocatorType} from "../../enums/element-locator-type.enum";
import {ElementCreateType} from "../../enums/element-create-type.enum";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {AgentService} from "../../agents/services/agent.service";
import {ElementMetaData} from "../../models/element-meta-data.model";
import {UserPreferenceService} from "../../services/user-preference.service";
import {UserPreference} from "../../models/user-preference.model";
import {ElementScreenNameService} from "../../shared/services/element-screen-name.service";
import {Observable, of} from "rxjs";
import {DomSanitizer} from "@angular/platform-browser";
import {ElementScreenName} from "../../models/element-screen-name.model";
import {ChromeRecorderService} from "../../services/chrome-recoder.service";
import {WorkspaceType} from "../../enums/workspace-type.enum";
import {DuplicateLocatorWarningComponent} from "./duplicate-locator-warning.component";

@Component({
  selector: 'app-element-form',
  templateUrl: './element-form.component.html',
})

export class ElementFormComponent extends BaseComponent implements OnInit {
  @ViewChild('submitReviewButton') public submitReviewButton: ElementRef;
  public control = new FormControl();
  public workspaceVersion: WorkspaceVersion;
  public element: Element;
  public elementForm: FormGroup;
  public agentInstalled: Boolean;
  public formSubmitted: Boolean = false;
  public showDetails: Boolean = false;
  public versionId: number;
  public testCaseId: number;
  public elementId: number;
  public elementCreateType = ElementCreateType;
  public defaultScreenName = "Default Screen";
  private userPreference: UserPreference;
  public canNotShowLaunch: boolean = false;
  public saving = false;
  public testCaseResultId: number;
  public reviewSubmittedElement: Element;
  public screenNameOptions: Observable<Set<ElementScreenName>>;
  public screenNames: Set<ElementScreenName>;
  public isInProgress: Boolean;
  public isElementsChanged:Boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
    private elementService: ElementService,
    private dialogRef: MatDialogRef<ElementFormComponent>,
    private workspaceVersionService: WorkspaceVersionService,
    private agentService: AgentService,
    private elementScreenNameService: ElementScreenNameService,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public options: {
      isStepRecordView: boolean;
      elementId?: number, versionId?: number,
      name?: string, isNew?: boolean, isDryRun?: boolean,
      testCaseId: number, testCaseResultId: number
    },
    private userPreferenceService: UserPreferenceService,
    public chromeRecorderService: ChromeRecorderService,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
    this.versionId = this.options.versionId;
    this.elementId = this.options.elementId;
    this.testCaseId = this.options?.testCaseId;
    this.testCaseResultId = this.options?.testCaseResultId;
    this.screenNameOptions = new Observable<Set<ElementScreenName>>();
    this.screenNames = new Set<ElementScreenName>();
  }

  get locatorTypes() {
    return Object.keys(ElementLocatorType).filter(type => {
      if (this.workspaceVersion?.workspace?.isWeb || this.workspaceVersion?.workspace?.isMobileWeb) {
        return type != ElementLocatorType.accessibility_id
      } else if (this.workspaceVersion?.workspace?.isMobileNative) {
        return (type == ElementLocatorType.accessibility_id ||
          type == ElementLocatorType.class_name ||
          type == ElementLocatorType.xpath ||
          type == ElementLocatorType.id_value ||
          type == ElementLocatorType.name)
      }
    });
  }

  get canShowInspector() {
    return this.router.url.includes('/elements');
  }

  ngOnInit() {
    this.canNotShowLaunch = !this.router.url.includes('/step')
    this.userPreferenceService.show().subscribe(res => {
      this.userPreference = res
      this.fetchApplicationVersion();
      if (this.elementId)
        this.fetchElement();
      else if (this.options?.name && !this.options?.isNew)
        this.fetchElementByName()
      else {
        this.setElement();
      }

      this.fetchScreenNames();
    })
  }

  setElement() {
    if (this.workspaceVersion?.workspace) {
      this.createElement();
      this.element.name = this.options?.name || '';
      this.addValidations();
      if (this.element.createdType == ElementCreateType.CHROME && this.chromeRecorderService?.isChrome)
        this.startCapture();
    } else {
      setTimeout(() => this.setElement(), 300);
    }
  }

  isManualOrHasMandatoryPrerequisites(): Boolean {
    switch (this.element.createdType) {
      case ElementCreateType.MANUAL:
        return true;
      case ElementCreateType.CHROME:
        return (this.chromeRecorderService.isChrome && this.chromeRecorderService.isInstalled);
      case ElementCreateType.MOBILE_INSPECTOR:
        return this.agentInstalled;
    }
  }

  fetchApplicationVersion() {
    this.workspaceVersionService.show(this.versionId).subscribe(res => {
      this.workspaceVersion = res;
      if (this.workspaceVersion.workspace.isWebMobile) {
        this.chromeRecorderService.isChromeBrowser();
        this.chromeRecorderService.pingRecorder();
      } else if (this.workspaceVersion.workspace.isMobileNative) {
        this.isAgentInstalled();
      }
    });
  }

  hasInspectorFeature() {
    return (
      this.workspaceVersion && this.workspaceVersion.workspace.isAndroidNative) || (this.workspaceVersion && this.workspaceVersion.workspace.isIosNative);
  }

  fetchElement() {
    this.elementService.show(this.elementId).subscribe((res: Element) => {
      this.element = res;
      this.element.screenName = res.screenNameObj.name;
      if (this.element.createdType == ElementCreateType.CHROME)
        this.startCapture();
      this.addValidations();
    });
  }

  createElement() {
    this.element = new Element();
    this.element.createdType = this.workspaceVersion.workspace.isWebMobile ? ElementCreateType.CHROME : ElementCreateType.MANUAL;
    this.element.locatorType = ElementLocatorType.xpath;
    this.element.workspaceVersionId = <number>this.versionId;
  }

  addValidations() {
    this.elementForm = new FormGroup({
      created_type: new FormControl(this.element.createdType),
      name: new FormControl(this.element.name, [Validators.required, Validators.minLength(4),
        Validators.pattern('[a-zA-Z0-9_\\\- ]+$'), Validators.maxLength(250)]),
      definition: new FormControl(this.element.locatorValue),
      locatorType: new FormControl(this.element.locatorType),
      screen_name: new FormControl(this.element.screenNameObj.name, [Validators.required,
        Validators.minLength(4),Validators.maxLength(250)]),
    });
    this.element.createdType = this.options.isStepRecordView ? ElementCreateType.MANUAL : this.element.createdType;
  }

  saveElement() {
    this.formSubmitted = true;
    if (this.elementForm.valid && this.nameIsValid(this.element.name)) {
      this.saving = true;
      this.checkIsDynamic()
      let query = 'workspaceVersionId:' + this.workspaceVersion.id + ',locatorType:' + this.element.locatorType +
        ',locatorValue:' + this.element.locatorValueWithSpecialCharacters + ',screenNameId:' + this.element.screenNameId ;
      query = this.byPassSpecialCharacters(query);
      this.element.workspaceVersionId = this.workspaceVersion.id;
      this.element.createdType = this.options.isStepRecordView ? ElementCreateType.MOBILE_INSPECTOR : this.element.createdType;
      this.elementService.findAll(query).subscribe(res => {
        if(res?.content.length){
          this.saving = false;
          this.openDuplicateLocatorWarning(res.content);
        } else
          this.saveAfterValidation();
      });
    }
  }

  private saveAfterValidation(){
    this.checkIsDynamic()
    this.element.workspaceVersionId = this.workspaceVersion.id;
    this.element.createdType = this.options.isStepRecordView?ElementCreateType.MOBILE_INSPECTOR:this.element.createdType;
    this.elementService.create(this.element).subscribe((element: Element) => {
        this.saving = false;
        this.translate.get('message.common.created.success', {FieldName: 'Element'}).subscribe((res) => {
          this.showNotification(NotificationType.Success, res);
          this.stopCapture();
          this.dialogRef.close(element);
        })
      },
      error => {
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: 'Element'}).subscribe((res) => {
          this.showAPIError(error, res,'Element');
        })
      });
  }

  updateElement() {
    this.formSubmitted = true;
    if (this.elementForm.valid && this.nameIsValid(this.element.name)) {
      this.saving = true;
      let query = 'workspaceVersionId:' + this.workspaceVersion.id + ',locatorType:' + this.element.locatorType +
        ',locatorValue:' + this.element.locatorValueWithSpecialCharacters +
        ',screenNameId:' + this.element.screenNameId + ',id!'+ this.elementId;
      query = this.byPassSpecialCharacters(query);
      this.elementService.findAll(query).subscribe(res => {
        if(res?.content.length){
          this.saving = false;
          this.openDuplicateLocatorWarning(res.content);
        } else{
          this.updateAfterValidation();
        }
      });
    }
  }

  checkIsDynamic() {
    let dynamicLocators = new RegExp(/@\|(.*?)\||\*\|(.*?)\|/gi);
    let isTestData = new RegExp(/@\|(.*?)\|/gi);
    let data = dynamicLocators.exec(this.element.locatorValue);
    this.element.isDynamic = false;
    delete this.element?.metadata?.testData;
    if (data?.length) {
      this.element.isDynamic = true;
      let testData = {};
      testData['test-data'] = data[1] ? data[1] : data[2]
      testData['test-data-type'] = isTestData.test(this.element.locatorValue) ? 'parameter' : 'environment';
      this.element.metadata = this.element.metadata ? this.element.metadata : new ElementMetaData();
      this.element.metadata.testData = <JSON>(testData)
    }
  }

  nameIsValid(name) {
    let notAllowedChar = "[{}().+*?^$%`'/\\\\|]";
    let exp = new RegExp(notAllowedChar)
    if (exp.test(name)) {
      this.translate.get('element.name.invalid.message').subscribe((res) => {
        this.showNotification(NotificationType.Error, res);
      })
      return false
    }
    return true;
  }

  refresh() {
    if (window.parent == window)
      window.location.reload();
    else
      window.parent.location.reload();
  }

  isAgentInstalled() {
    this.agentService.ping().subscribe({
      next: (res) => this.agentInstalled = res['isRegistered'],
      error: () => this.agentInstalled = false
    });
  }

  startCapture() {
    this.chromeRecorderService.recorderVersion = this.workspaceVersion;
    this.setLocatorTypeToXpath();
    this.chromeRecorderService.pingRecorder();
    this.chromeRecorderService.elementCallBackContext = this;
    this.chromeRecorderService.elementCallBack = this.chromeExtensionElementCallback;
    if (this.chromeRecorderService.isInstalled)
      this.chromeRecorderService.postGetElementMessage(false);
  }

  // private setLocatorTypeToXpath() {
  //   if (!this.elementId)
  //     this.elementForm.controls['locatorType'].setValue(this.locatorTypes[0])
  // }

  stopCapture() {
    this.chromeRecorderService.elementCallBackContext = undefined;
    this.chromeRecorderService.elementCallBack = undefined;
    this.chromeRecorderService.stopSpying();
  }

  private chromeExtensionElementCallback(chromeRecorderElement: Element) {
    this.element = chromeRecorderElement
  }


  private fetchElementByName() {
    this.elementService.findAll('name:' + encodeURIComponent(this.options.name) + ',workspaceVersionId:' + this.versionId).subscribe(res => {
      if (res.empty) {
        this.setElement();
        return;
      }
      this.element = res.content[0];
      this.elementId = this.element.id;
      if (this.element.createdType == ElementCreateType.CHROME)
        this.startCapture();
      this.addValidations();
    })
  }

  private fetchScreenNames() {
    this.elementScreenNameService.findAll('workspaceVersionId:' + this.versionId).subscribe(res => {
      res.content.forEach(screenName => {
        this.screenNames.add(screenName);
      })
      this.screenNameOptions = of(this.screenNames)

    })
  }

  public filterData(target: any) {
    let name: String = target.value;
    this.element.screenNameObj.name = name;
    this.isElementsChanged=true;
    if (!name.length) {
      this.element.screenNameId = null;
    }
    let arrSet: Set<ElementScreenName> = new Set<ElementScreenName>();
    this.elementScreenNameService.findAll('workspaceVersionId:' + this.versionId + (name?.length > 0 ? ",name~" + name : '')).subscribe(res => {
      res.content.forEach(screenName => {
        arrSet.add(screenName);
      })
      this.screenNameOptions = of(arrSet);
    })
  }

  private setLocatorTypeToXpath() {
    if (!this.elementId)
      this.elementForm.controls['locatorType'].setValue(this.locatorTypes[0])
  }

  openChat() {
    // @ts-ignore
    window.fcWidget.open();
  }

  public addScreenNameIfNeed(target: any) {
    let name = target.value;
    let screenNameBean: ElementScreenName = new ElementScreenName();
    screenNameBean.name = name;
    screenNameBean.workspaceVersionId = this.element.workspaceVersionId;
    if (!this.screenNames.has(screenNameBean)) {

    }
  }

  public setScreenName(screenName: any, save?) {
    if (screenName.id != null) {
      this.element.screenNameObj.name = screenName.name;
      this.element.screenNameObj.id = screenName.id;
      this.element.screenName = screenName.name;
      this.element.screenNameId = screenName.id;
    } else {
      let screenNameBean: ElementScreenName = new ElementScreenName();
      screenNameBean.name = screenName.name;
      screenNameBean.workspaceVersionId = this.element.workspaceVersionId;
      this.elementScreenNameService.create(screenNameBean).subscribe(screenNameBean => {
        this.element.screenNameObj.name = screenNameBean.name;
        this.element.screenNameId = screenNameBean.id;
        this.elementForm.get('screen_name').setValue(this.element.screenNameObj.name);
        if (save) {
          (this.elementId) ? this.updateElement() : this.saveElement();
        }
        this.isInProgress = false;
      })
    }
  }

  saveOrUpdate() {
    let screenName : ElementScreenName;
    if (!this.isInProgress) {
      this.formSubmitted = true;
      if (this.elementForm.invalid)
        return;
      if (this.element.screenNameId && !this.isElementsChanged) {
        let updatedScreenName =this.elementForm.get("screen_name").value;
        this.element.screenNameObj.name =  updatedScreenName;
        screenName = new ElementScreenName();
        screenName.name = updatedScreenName;
        screenName.workspaceVersionId =this.element.workspaceVersionId;
        this.elementScreenNameService.create(screenName).subscribe(screeNameObj => {
          this.element.screenNameId = screeNameObj.id;
          (this.elementId) ? this.updateElement() : this.saveElement()
        });
      } else {
        let screenNameBean: ElementScreenName = new ElementScreenName();
        screenNameBean.name = this.elementForm.get('screen_name').value;
        screenNameBean.workspaceVersionId = this.element.workspaceVersionId;
        this.setScreenName(screenNameBean, true);
      }
    }
  }

  get isWeb() {
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.WebApplication;
  }

  get isMobileWeb() {
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.MobileWeb;
  }

  get isAndroidNative() {
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.AndroidNative;
  }

  get isIosNative() {
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.IOSNative;
  }

  private openDuplicateLocatorWarning(res: Element[]) {
    let elementType = this.translate.instant('element.locator_type.'+this.element.locatorType);
    let description = this.elementId ?
      this.translate.instant('element.update_confirmation_with_screen_name.description',
        {elementType: elementType, definition: this.element.locatorValue,
          screenName: this.element.screenNameObj.name}) :
      this.translate.instant('element.create_confirmation_with_screen_name.description',
        {elementType: elementType, definition: this.element.locatorValue,
          screenName: this.element.screenNameObj.name});
    const dialogRef = this.matDialog.open(DuplicateLocatorWarningComponent, {
      width: '568px',
      height: 'auto',
      data: {
        description: description,
        elements: res,
        isRecorder: false,
        isUpdate: Boolean(this.elementId)
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe(result => {
        if (result) {
          this.saving = true;
          if (this.elementId)
            this.updateAfterValidation();
          else
            this.saveAfterValidation();
        }
      });
  }

  private updateAfterValidation() {
    this.checkIsDynamic();
    this.element.createdType = this.options.isStepRecordView?ElementCreateType.MOBILE_INSPECTOR:this.element.createdType;
    this.elementService.update(this.elementId, this.element).subscribe(
      (element: Element) => {
        this.saving = false;
        this.translate.get('message.common.update.success', {FieldName: 'Element'}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
          this.stopCapture()
          this.dialogRef.close(element);
        });
      },
      error => {
        this.saving = false;
        this.translate.get('message.common.update.failure', {FieldName: 'Element'}).subscribe((res) => {
          this.showAPIError(error, res)
        })
      });
  }

}


