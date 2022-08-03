import {Component, Input, OnInit} from '@angular/core';
import {WorkspaceType} from "../../../enums/workspace-type.enum";
import {TestPlanLabType} from "../../../enums/test-plan-lab-type.enum";
import {DevicesService} from "../../../agents/services/devices.service";
import {AgentService} from "../../../agents/services/agent.service";
import {PlatformService} from "../../../agents/services/platform.service";
import {AgentDevice} from "../../../agents/models/agent-device.model";
import {Agent} from "../../../agents/models/agent.model";
import {TestDevice} from "../../../models/test-device.model";
import {TestDeviceSettings} from "../../../models/test-device-settings.model";
import {Platform} from "../../../enums/platform.enum";

@Component({
  selector: 'app-test-plan-machine-info',
  template: `<div class="d-flex align-items-center w-100">
    <span class="d-inline-block mr-10 no-bg-transparent" style="height: 14px!important;min-width: 14px;min-height: unset"
          *ngIf="executionEnvironment?.testPlanLabType"
          [class.testsigma-lab-logo]="executionEnvironment.isTestsigmaLab"
          [class.testsigma-local-devices-logo]="executionEnvironment.isHybrid"
          [matTooltip]="('execution.lab_type.'+executionEnvironment.testPlanLabType) | translate"></span>
    <span class="flex-shrink-0">
      <!--appTooltipOnEllipsis-->
      <a
        (click)="eventPrevent($event)"
        class="mat-tooltip-trigger mr-3 mw-75 text-link text-truncate-d-block w-fit-content"
        [routerLink]="['/agents', agent.id]"
        [textContent]="agent.title"
        *ngIf="isHybrid && agent"
        ></a>
      <i
        [class.fa-windows-brands]="executionEnvironment?.isWindows"
        [class.fa-apple]="executionEnvironment?.isMac || executionEnvironment?.isIOS"
        [class.fa-linux-2]="executionEnvironment?.isLinux"
        [class.fa-android-solid]="executionEnvironment?.isAndroid"
        [matTooltip]="isHybrid && agentDevice ? 'V. ' +agentDevice.osVersion : ( 'agents.list_view.os' | translate) +' : '+ executionEnvironment?.platform+' '+executionEnvironment?.formattedOsVersion"></i>
    </span>
    <div
      class="px-10 mw-70 w-70 d-flex">
      <!--[appTooltipOnEllipsis]="executionEnvironment?.deviceName"-->
      <div class="mw-50 text-truncate"
           *ngIf="executionEnvironment?.deviceName && (executionEnvironment?.isIOS || executionEnvironment?.isAndroid)"
      >
        <i
          class="mr-6"
          [class.fa-mobile-alt-solid]="executionEnvironment?.deviceName && (executionEnvironment?.isIOS || executionEnvironment?.isAndroid)"
          [matTooltip]="executionEnvironment?.deviceName"
        ></i>
      </div>
      <!--[appTooltipOnEllipsis]="executionEnvironment?.formattedBrowserVersion"-->
      <div class="mw-50 text-truncate"
      >
        <i class="pl-0"
           [class.fa-chrome]="executionEnvironment?.isChrome"
           [class.fa-firefox-brands]="executionEnvironment?.isFirefox"
           [class.fa-safari-brands]="executionEnvironment?.isSafari"
           [class.fa-edge]="executionEnvironment?.isEdge"
           [matTooltip]="isHybrid? '' : (executionEnvironment?.formattedBrowserVersion ? ('test_plan.environment.browser'|translate)+' : '+( executionEnvironment?.browserNameI18nKey | translate) +' '+ executionEnvironment?.formattedBrowserVersion : '')"
        ></i>
      </div>
      <i class="fa-screen-resolution ml-12" [matTooltip]="executionEnvironment.resolution" *ngIf="executionEnvironment.resolution"></i>
    </div>
  </div>
  `,
  host:{"class":"d-flex"}
})
export class TestPlanMachineInfoComponent implements OnInit {
  public environmentSettings: TestDeviceSettings;

  @Input('executionEnvironment') executionEnvironment: TestDevice;

  public agent: Agent;
  public agentDevice: AgentDevice;

  constructor(
    private devicesService: DevicesService,
    private agentService: AgentService,
    private platformService: PlatformService) {
  }

  ngOnInit() {}

  ngOnChanges(){
    if(this.isHybrid){
      let targetMachine = this.executionEnvironment?.deviceId || this.executionEnvironment?.agentId;
      this.agentService.findAll("id:" + targetMachine).subscribe(res=> {
        if(res.content.length){
          this.agent = res.content[0];
          if(this.executionEnvironment.browser!=null){
            this.executionEnvironment.platform = Agent.getPlatformFromOsType(this.agent.osType);
            this.executionEnvironment.osVersion = this.agent.osVersion;
            let browser = this.agent.browsers.find(browser => browser.name.toUpperCase() == this.executionEnvironment.browser)
            this.executionEnvironment.browserVersion = browser.majorVersion;
          }
        }
        if(this.executionEnvironment.deviceId && this.agent)
          this.devicesService.findAll(this.agent.id).subscribe(res => {
            this.agentDevice = res.content.find(device => device.id == this.executionEnvironment.deviceId);
            this.executionEnvironment.platform = AgentDevice.getPlatformFromMobileOStype(this.agentDevice.osName);
            this.executionEnvironment.osVersion = this.agentDevice.osVersion;
            this.executionEnvironment.browser = this.executionEnvironment.platform === Platform.Android ? 'CHROME' : "SAFARI";
            this.executionEnvironment.deviceName = this.agentDevice.name;
          })
      });
    }
    else {
      if (this.executionEnvironment?.platformOsVersionId != null) {
        this.platformService.findOsVersion(this.executionEnvironment.platformOsVersionId, this.executionEnvironment.testPlanLabType).subscribe((platformOsversion) => {
          this.executionEnvironment.platform = platformOsversion.platform;
          this.executionEnvironment.osVersion = platformOsversion.version;
        });
      }
      if (this.executionEnvironment?.platformBrowserVersionId != null) {
        this.platformService.findBrowserVersion(this.executionEnvironment.platformBrowserVersionId, this.executionEnvironment.testPlanLabType).subscribe((platformBrowsersversion) => {
          this.executionEnvironment.browser = platformBrowsersversion.name.toUpperCase();
          this.executionEnvironment.browserVersion = platformBrowsersversion.version;
        });
      }
      if (this.executionEnvironment?.platformDeviceId != null) {
        this.platformService.findDevice(this.executionEnvironment.platformDeviceId, this.executionEnvironment.testPlanLabType).subscribe((platformDevice) => {
          this.executionEnvironment.deviceName = platformDevice.displayName;
        });
      }
      if (this.executionEnvironment?.platformScreenResolutionId != null) {
        this.platformService.findScreenResolution(this.executionEnvironment.platformScreenResolutionId, this.executionEnvironment.testPlanLabType).subscribe((platformResolution) => {
          this.executionEnvironment.resolution = platformResolution.resolution;
        });
      }
    }
  }

  get applicationType() {
    return this.executionEnvironment?.version?.workspace?.workspaceType;
  }

  get isHybrid() {
    return this.executionEnvironment.testPlanLabType == TestPlanLabType.Hybrid;
  }

  get isMobileNative() {
    return this.applicationType == WorkspaceType.AndroidNative || this.applicationType == WorkspaceType.IOSNative;
  }

  get isAndroidNative() {
    return this.applicationType == WorkspaceType.AndroidNative;
  }

  get isIosNative() {
    return this.applicationType == WorkspaceType.IOSNative;
  }

  get isWeb() {
    return this.applicationType == WorkspaceType.WebApplication;
  }

  get isMobile() {
    return this.isMobileWeb || this.isMobileNative;
  }

  get isMobileWeb() {
    return this.applicationType == WorkspaceType.MobileWeb;
  }

  get isWebMobile() {
    return this.isMobileWeb || this.isWeb;
  }

  eventPrevent(event) {
    event.stopPropagation();
    event.stopImmediatePropagation();
  }
}
