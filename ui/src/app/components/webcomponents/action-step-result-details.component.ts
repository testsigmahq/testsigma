import {Component, ElementRef, Inject, Input, OnInit, ViewChild} from '@angular/core';
import {TestStepResult} from "../../models/test-step-result.model";
import {TestStepType} from "../../enums/test-step-type.enum";
import {MatDialog} from "@angular/material/dialog";
import {ElementFormComponent} from "./element-form.component";
import {ScreenShortOverlayComponent} from "./screen-short-overlay.component";
import {defaultPageScrollConfig, PageScrollService} from "ngx-page-scroll-core";
import {DOCUMENT} from "@angular/common";
import {TestStepResultService} from "../../services/test-step-result.service";
import {ElementService} from "../../shared/services/element.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestStepService} from "../../services/test-step.service";
import {TestStep} from "../../models/test-step.model";
import {Element} from "../../models/element.model";
import {ActivatedRoute} from "@angular/router";
import {AddonElementData} from "../../models/addon-element-data.model";
import {AddonTestStepTestData} from "../../models/addon-test-step-test-data.model";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {UploadService} from "../../shared/services/upload.service";

@Component({
  selector: 'app-action-step-result-details',
  templateUrl: './action-step-result-details.component.html',
  styles: []
})
export class ActionStepResultDetailsComponent extends BaseComponent implements OnInit {
  @Input('testStepResult') testStepResult: TestStepResult;
  public TestStepType: typeof TestStepType = TestStepType;
  public isScreenshotBroken: boolean = false;
  public isScreenshotExpired: boolean = false;
  public isShowMetaData: boolean = false;
  public isShowAttachment: boolean = false;
  public isShowAddonLogs: boolean = false;
  public preRequestStep: TestStepResult;
  @ViewChild('stepDetailsRef', {static: false}) public stepDetailsRef: ElementRef;
  public isSelected: string = 'step_data';
  public version: WorkspaceVersion;
  public testStep: TestStep;
  public environmentResult: TestDeviceResult;
  private showScreenShortFlag: Boolean = false;
  public isCapabilities: Boolean = false;
  public showDetails: boolean = false;
  public element: Element;
  public addonElementData: AddonElementData[];
  public addonTestStepTestData: AddonTestStepTestData[];
  public appDetails: JSON = JSON.parse('{}');
  public ElementDetails;
  elementDetails: {};
  isShowElementDetails: boolean;
  stepElementDetails: IElementDetails = {};

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private matDialog: MatDialog,
    private pageScrollService: PageScrollService,
    @Inject(DOCUMENT) private document: any,
    private elementService: ElementService,
    private versionService: WorkspaceVersionService,
    private testStepService: TestStepService,
    private environmentResultService: TestDeviceResultService,
    private testStepResultService: TestStepResultService,
    private uploadService: UploadService,
    private router: ActivatedRoute) {
    super(authGuard, notificationsService, translate, toastrService);
    defaultPageScrollConfig.scrollOffset = 200;
    defaultPageScrollConfig.duration = 20;
  }

  ngOnInit() {
    if (this.testStepResult) {
      let currentDate = new Date();
      let executionStartTime = new Date(this.testStepResult.startTime);
      let diffInMillis = currentDate.getTime() - executionStartTime.getTime();
      if (((diffInMillis) / (1000 * 60 * 60 * 24)) > 30) {
        this.isScreenshotExpired = true;
      }
      if (this.testStepResult?.addonElements) {
        this.addonElementData = this.getElements(this.testStepResult?.addonElements);
      }
      if (this.testStepResult?.addonTestData) {
        this.addonTestStepTestData = this.getTestData(this.testStepResult?.addonTestData);
      }
      this.isScreenshotBroken = false;
    }
  }

  ngOnChanges() {
    this.fetchElementDetails(this.testStepResult?.stepDetails?.dataMap?.elementString);
    this.preRequestStep = undefined;
    if (this.testStepResult?.testCase?.workspaceVersionId)
      this.versionService.show(this.testStepResult?.testCase?.workspaceVersionId).subscribe(res => this.version = res)
    if (this.testStepResult?.metadata?.preRequisite) {
      this.setPreRequisiteStep(this.testStepResult?.metadata?.preRequisite);
    }
    if (this.testStepResult?.envRunId) {
      this.environmentResultService.show(this.testStepResult?.envRunId).subscribe(data => {
        this.environmentResult = data;
        this.getAppDetails();
      })
    }
    // this.fetchElementDetailsByName(this.testStepResult?.stepDetails?.dataMap?.elementString);
    if (this.testStepResult?.addonElements) {
      this.addonElementData = this.getElements(this.testStepResult?.addonElements);
    }
    if (this.testStepResult?.addonTestData) {
      this.addonTestStepTestData = this.getTestData(this.testStepResult?.addonTestData);
    }
    if (this.testStepResult?.stepId && !this.testStepResult?.testStep) {
      this.testStepService.findAll("id:" + this.testStepResult.stepId).subscribe(res => {
        this.testStep = res?.content[0];
        if (this.testStep?.preRequisiteStepId && !this.preRequestStep)
          this.setPreRequisiteStep(this.testStep?.preRequisiteStepId)
      })
    } else if (this.testStepResult?.testStep) {
      this.testStep = this.testStepResult?.testStep;
    }
    this.isScreenshotBroken = false;
  }

  getElementDetails() {
    if (!this.ElementDetails)
      this.ElementDetails = this.getElements(this.testStepResult?.elementDetails, true);
    return this.ElementDetails.length || this.element?.locatorValue;//TODO Fallback element details need to clean
  }

  isEmptyObject(obj){
    return Object.keys(obj).length==0;
  }

  canShowCustomFunctionsParameters(args){
    return args ? Object.keys(args).length : 0;
  }

  setPreRequisiteStep(stepId: Number) {
    this.testStepResultService.findAll("stepId:" + stepId + ",testCaseResultId:" + this.testStepResult.testCaseResultId).subscribe(response => {
      this.preRequestStep = response.content[0];
    });
  }

  get preRequisiteStep() {
    return this.preRequestStep?.testStep?.stepDisplayNumber ? this.preRequestStep?.testStep?.stepDisplayNumber : (<number>this.preRequestStep?.stepDetail?.order_id + 1)
  }

  fetchElementByName(elementName, addonElement?) {
    this.elementService.findAll('name:' + encodeURIComponent(elementName) + ',workspaceVersionId:' + this.testStepResult?.testCase?.workspaceVersionId).subscribe(
      (res) => { if(res.numberOfElements > 1) console.warn('more than one element found with the name: '+elementName);
        let currentStepElement = res.content[0];
        this.setElementDetails(currentStepElement);
        this.testStepResult.isElementChanged = res?.content?.length == 0;
        if (addonElement) {
          addonElement.isElementChanged = res?.content?.length == 0;
          addonElement.element = res.content[0];
        }
        if (!this.testStepResult.isElementChanged)
          this.element = res.content[0];
      },
      (err) => {
        addonElement ? addonElement.isElementChanged = true : '';
        this.testStepResult.isElementChanged = true;
      }
    )
  }

  fetchElementDetailsByName(elementName: String) {
    if(!elementName) this.setElementDetails(null);
    else {
      this.elementService.findAll('name:' + encodeURIComponent(elementName.toString())
        + ',workspaceVersionId:' + this.testStepResult?.testCase?.workspaceVersionId).subscribe(
        (res) => {
          if(res.numberOfElements > 1) console.warn('More than one element found with the name: '+ elementName);
          let currentStepElement = res.content[0];
          this.setElementDetails(currentStepElement);
        },
        (err) => {
          console.debug('Error while finding elements:: '+err);
        }
      )
    }
  }

  setElementDetails(currentStepElement: Element) {
    if(currentStepElement) {
      this.stepElementDetails = {};
      this.stepElementDetails.elementName = currentStepElement.name.toString();
      this.stepElementDetails.locatorType = currentStepElement.locatorType.toString();
      this.stepElementDetails.locator = currentStepElement.locatorValue.toString();
      this.stepElementDetails.createdType = currentStepElement.createdType.toString();
      // this.stepElementDetails.status = currentStepElement.status.toString();
    } else {
      this.stepElementDetails = {};
      this.stepElementDetails.elementName = this.translate.instant('element.step.not_found');
    }
  }

  setBrokenImage() {
    this.isScreenshotBroken = true;
  }

  setRefreshImage() {
    this.showScreenShortFlag = false;
    this.showScreenShortFlag = true;
    this.isScreenshotBroken = false;
  }

  openEditElement(name, isAddonElement?: boolean) {
    this.matDialog.open(ElementFormComponent, {
      height: "100vh",
      width: '60%',
      position: {top: '0px', right: '0px'},
      data: {
        name: name,
        versionId: this.testStepResult?.testCase?.workspaceVersionId,
        testCaseResultId: this.testStepResult.testCaseResultId
      },
      panelClass: ['mat-dialog', 'rds-none']
    }).afterClosed().subscribe((res: Element) => {
      if (res && res instanceof Element) {
        if (isAddonElement)
          this.updateAddonElements(name, res.name);
        else
          this.testStep.element = res.name;
        this.testStepService.update(this.testStep).subscribe()
        this.testStepResult.isElementChanged = true;
      }
    })
  }

  toggleAttachment() {
    this.isShowAttachment = !this.isShowAttachment;
  }

  toggleMetadata() {
    this.isShowMetaData = !this.isShowMetaData;
  }

  toggleAddonLogs() {
    this.isShowAddonLogs = !this.isShowAddonLogs;
  }

  toggleCapabilities() {
    this.isCapabilities = !this.isCapabilities
  }

  openScreenShort() {
    this.matDialog.open(ScreenShortOverlayComponent, {
      width: '100vw',
      height: '100vh',
      position: {top: '0', left: '0', right: '0', bottom: '0'},
      data: {screenShortUrl: this.testStepResult.screenShotURL},
      panelClass: ['mat-dialog', 'full-width', 'rds-none']
    })
  }

  get showScreenShort() {
    return !(this.testStepResult?.isStepGroup ||
      this.testStepResult?.isForLoop ||
      this.testStepResult?.isRestStep ||
      this.testStepResult?.stepGroup || this.version?.workspace?.isRest);
  }

  get ShowFixElementCheck(): Boolean {
    return this.testStepResult?.canShowFixElement;
  }

  getElements(elements: Map<string, any>, isUpdate?) {
    let result = [];
    if (elements)
      Object.keys(elements).forEach(key => {
        if (!isUpdate)
          this.fetchElementByName(elements[key].name, elements[key]);
        result.push(elements[key]);
      });
    return result;
  }

  getTestData(testDataMap: Map<String, AddonTestStepTestData>) {
    let result = [];
    Object.keys(testDataMap).forEach(key => {
      result.push(testDataMap[key]);
    });
    return result;
  }

  updateAddonElements(oldName, newName) {
    let testStepResultElements = this.getElements(this.testStepResult?.addonElements, true);
    let testStepElements = this.getElements(this.testStep?.addonElements, true);
    for (let i = 0; i < testStepElements.length; i++) {
      if (testStepElements[i].name == oldName) {
        testStepElements[i].name = newName;
        testStepResultElements[i].isElementChanged = true;
        testStepElements[i].isElementChanged = true;

      }
    }
  }

  toggleShowDetails() {
    this.showDetails = !this.showDetails;
  }

  getAppDetails() {
    let json: JSON = JSON.parse("{}");
    if (this.environmentResult.testDeviceSettings.appPackage)
      json['appPackage'] = this.environmentResult.testDeviceSettings.appPackage;
    if (this.environmentResult.testDeviceSettings.appActivity)
      json['appActivity'] = this.environmentResult.testDeviceSettings.appActivity;
    // if(this.testDeviceResult.testDeviceSettings.app)
    // json['app']= this.testDeviceResult.testDeviceSettings.app;
    if (this.environmentResult.testDeviceSettings.appUrl)
      json['appUrl'] = this.environmentResult.testDeviceSettings.appUrl;
    if (this.environmentResult.testDeviceSettings.appId)
      json['appId'] = this.environmentResult.testDeviceSettings.appId;
    this.appDetails = json;
    if (this.environmentResult.testDeviceSettings.appUploadId || this.environmentResult.testDeviceSettings.appId)
      this.uploadService.find(this.environmentResult.testDeviceSettings.appUploadId || this.environmentResult.testDeviceSettings.appId).subscribe(app => {
        this.appDetails['appName'] = app.name;
      })
  }

  canShowDetails() {
    return Object.keys(this.appDetails).length > 0;
  }

  processJsonCapabilities() {
    if (this.environmentResult?.testDeviceSettings?.capabilities) {
      this.environmentResult?.testDeviceSettings?.capabilities.forEach(data => {
        if (data.type == 'java.lang.String') {
          data.type = 'String'
        } else if (data.type == 'java.lang.integer') {
          data.type = 'Integer'
        } else if (data.type == 'java.lang.boolean') {
          data.type = 'Boolean'
        }
      })
    }
    return this.environmentResult?.testDeviceSettings?.capabilities;
  }

  //TODO Fallback element details need to clean
  get elementName(){
    return this.ElementDetails[0]?.elementName ? this.ElementDetails[0]?.elementName : this.element.name;
  }

  get elementValue() {
    return this.ElementDetails[0]?.locatorValue ? this.ElementDetails[0]?.locatorValue : this.element.locatorValue;
  }

  get elementType() {
    return this.ElementDetails[0]?.findByType ? this.ElementDetails[0]?.findByType : this.element.locatorType;
  }

  fetchElementDetails(elementName){
    this.elementDetails = {
      'element name': this.testStepResult.elementDetails['element'].elementName,
      'locator type': this.testStepResult.elementDetails['element'].locatorStrategyName,
      'locator': this.testStepResult.elementDetails['element'].locatorValue
    }
  }

  toggleElementDetails() {
    this.isShowElementDetails = !this.isShowElementDetails;
  }

  get hasDefaultActionElements() {
    return !this.testStepResult?.isRestStep && this.testStepResult?.stepDetails?.dataMap?.elementString && !this.testStepResult?.addonElements;
  }

  get hasAddonActionElements() {
    return !this.testStepResult?.isRestStep && this.testStepResult?.addonElements;
  }

  get canShowFrameInfo() {
    return !(this.testStepResult?.isPassed || this.testStepResult?.isQueued || this.testStepResult?.isNotExecuted) &&
      (this.element?.metadata?.currentElement?.['isFrameElement'])
  }

}

interface IElementDetails {
  elementName?: string;
  locatorType?: string;
  locator?: string;
  createdType?: string;
  status?: string;
}
