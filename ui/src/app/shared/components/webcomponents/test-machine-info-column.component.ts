import {Component, Input, OnInit} from '@angular/core';
import {TestDevice} from "../../../models/test-device.model";
import {TestPlanResult} from "../../../models/test-plan-result.model";
import {Agent} from "../../../agents/models/agent.model";
import {AgentDevice} from "../../../agents/models/agent-device.model";
import {DevicesService} from "../../../agents/services/devices.service";
import {AgentService} from "../../../agents/services/agent.service";
import {TestPlan} from "../../../models/test-plan.model";
import {PlatformService} from "../../../agents/services/platform.service";
import {TestPlanLabType} from "../../../enums/test-plan-lab-type.enum";
import {TestDeviceSettings} from "../../../models/test-device-settings.model";
import {TestDeviceResult} from "../../../models/test-device-result.model";
import {Platform} from "../../../enums/platform.enum";

@Component({
  selector: 'app-test-machine-info-column',
  template: `
    <span
      #labsTooltip="matTooltip"
      class="d-inline-block mr-10 no-bg-transparent"
      style="height: 14px!important;min-width: 14px;min-height: unset"
      *ngIf="testDevice?.testPlanLabType && !(isHybrid && agent)"
      [class.testsigma-lab-logo]="testDevice.isTestsigmaLab"
      [class.testsigma-local-devices-logo]="testDevice.isHybrid"
      [matTooltip]="('execution.lab_type.'+ testDevice.testPlanLabType) | translate"></span>
    <a
      #labsTooltip="matTooltip"
      (click)="eventPrevent($event)"
      [routerLink]="['/agents', agent.id]"
      [matTooltip]="agent.title"
      *ngIf="isHybrid && agent"
    >
      <span
        class="d-inline-block mr-10 no-bg-transparent"
        style="height: 14px!important;min-width: 14px;min-height: unset"
        [class.testsigma-local-devices-logo]="testDevice.isHybrid"
      >
      </span>
    </a>
    <span class="text-truncate">
        <span
          #mobileAgentTooltip="matTooltip"
          class="fa-mobile-alt-solid"
          *ngIf="agentDevice"
          [matTooltip]="agentDevice.name"
        ></span>
        <i
          #osTooltip="matTooltip"
          *ngIf="isHybrid && !agentDevice"
          [class.fa-windows-brands]="agent.isWindows"
          [class.fa-apple]="agent.isMac"
          [class.fa-linux-2]="agent.isLinux"
          [matTooltip]="isHybrid && agentDevice ? 'V. ' +agentDevice.osVersion : ( 'agents.list_view.os' | translate) +' : '+ testDevice?.platform+' '+testDevice?.formattedOsVersion"
        ></i>
        <i
          #osTooltip="matTooltip"
          *ngIf="!isHybrid && !agentDevice"
          [class.fa-windows-brands]="testDevice?.isWindows"
          [class.fa-apple]="testDevice?.isMac || testDevice?.isIOS"
          [class.fa-linux-2]="testDevice?.isLinux"
          [class.fa-android-solid]="testDevice?.isAndroid"
          [matTooltip]="isHybrid && agentDevice ? 'V. ' +agentDevice.osVersion : ( 'agents.list_view.os' | translate) +' : '+ testDevice?.platform+' '+testDevice?.formattedOsVersion"></i>
      </span>
    <div
      *ngIf="isHybrid"
      class="px-10 mw-70 d-flex"
    >
      <!--[appTooltipOnEllipsis]="testDevice?.formattedBrowserVersion"-->
      <div class="text-truncate"
      >
        <i
          #browserTooltip="matTooltip"
          class="pl-0"
          [class.fa-chrome]="testDevice?.isChrome"
          [class.fa-firefox-brands]="testDevice?.isFirefox"
          [class.fa-safari-brands]="testDevice?.isSafari"
          [class.fa-edge]="testDevice?.isEdge"
          [matTooltip]="(testDevice?.formattedBrowserVersion || testDevice.browserNameI18nKey) ? ('test_plan.environment.browser'|translate)+' : '+( testDevice.browserNameI18nKey | translate) +' '+ (testDevice?.formattedBrowserVersion||'') : ''"
        ></i>
      </div>
    </div>
    <div
      *ngIf="!isHybrid"
      class="px-10 mw-70 d-flex">
      <!--[appTooltipOnEllipsis]="testDevice?.deviceName"-->
      <div class="mw-100 text-truncate"
           *ngIf="testDevice?.deviceName && (testDevice?.isIOS || testDevice?.isAndroid)"
      >
        <i
          #mobileTooltip="matTooltip"
          [class.fa-mobile-alt-solid]="testDevice?.deviceName && (testDevice?.isIOS || testDevice?.isAndroid)"
          [matTooltip]="testDevice?.deviceName"
        ></i>
      </div>
      <!--[appTooltipOnEllipsis]="testDevice?.formattedBrowserVersion"-->
      <div class="mw-100 text-truncate"
      >
        <i
          #nativeLabBrowserTooltip="matTooltip"
          class="pl-0"
          [class.fa-chrome]="testDevice?.isChrome"
          [class.fa-firefox-brands]="testDevice?.isFirefox"
          [class.fa-safari-brands]="testDevice?.isSafari"
          [class.fa-edge]="testDevice?.isEdge"
          *ngIf="!testDevice?.deviceName && !(testDevice?.isIOS || testDevice?.isAndroid)"
          [matTooltip]="(testDevice?.formattedBrowserVersion|| testDevice.browserNameI18nKey) ? ('test_plan.environment.browser'|translate)+' : '+( testDevice.browserNameI18nKey | translate) +' '+ (testDevice?.formattedBrowserVersion||'') : ''"
        ></i>
      </div>
    </div>
  `,
  styles: [],
  host:{"class":"d-flex"}
})
export class TestMachineInfoColumnComponent implements OnInit {
  public environmentSettings: TestDeviceSettings;
  @Input("environmentResult") environmentResult: TestDeviceResult;
  @Input('testDevice') testDevice: TestDevice;
  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Input('testPlan') testPlan: TestPlan;
  public agent: Agent;
  public agentDevice: AgentDevice;

  constructor(
    private devicesService: DevicesService,
    private agentService: AgentService,
    private platformService : PlatformService) {
  }

  ngOnInit() {
    if(this.environmentResult){
      this.environmentSettings = this.environmentResult?.testDeviceSettings;
    }else{
      this.environmentSettings = this.testDevice?.settings;
    }

  }

  ngOnChanges(){
    if(this.isHybrid) {
      this.agentService.findAll("id:"+this.testDevice?.agentId).subscribe(res=> {
        if(res.content?.length){
          this.agent = res.content[0]
          this.testDevice.platform = Agent.getPlatformFromOsType(this.agent.osType);
          this.testDevice.osVersion = this.agent.osVersion;
          if(this.testDevice.browser!=null) {
            this.testDevice.browserVersion = this.agent.browsers.find(browser =>
              browser.name.toUpperCase() == this.testDevice.browser).majorVersion;
          }
        }
        if(this.testDevice?.deviceId && this.agent)
          this.devicesService.findAll(this.agent.id).subscribe(res => {
            this.agentDevice = res.content.find(device => device.id == this.testDevice.deviceId);
            this.testDevice.platform = AgentDevice.getPlatformFromMobileOStype(this.agentDevice.osName);
            this.testDevice.osVersion = this.agentDevice.osVersion;
            this.testDevice.browser = this.testDevice.platform === Platform.Android ? 'CHROME' : "SAFARI";
            this.testDevice.deviceName = this.agentDevice.name;
          })
      });
    }
    else{
      if(this.testDevice?.platformOsVersionId!= null){
        this.platformService.findOsVersion(this.testDevice.platformOsVersionId, TestPlanLabType.TestsigmaLab).subscribe((platformOsversion) => {
          this.testDevice.platform = platformOsversion.platform;
          this.testDevice.osVersion = platformOsversion.version;
        });
      }
      if(this.testDevice?.platformBrowserVersionId!=null){
        this.platformService.findBrowserVersion(this.testDevice.platformBrowserVersionId,  TestPlanLabType.TestsigmaLab).subscribe((platformBrowsersversion) => {
          this.testDevice.browser = platformBrowsersversion.name;
          this.testDevice.browserVersion = platformBrowsersversion.version;
        });
      }
      if(this.testDevice?.platformDeviceId!=null){
        this.platformService.findDevice(this.testDevice.platformDeviceId,  TestPlanLabType.TestsigmaLab).subscribe((platformDevice) => {
          this.testDevice.deviceName = platformDevice.displayName;
        });
      }
      if(this.testDevice?.platformScreenResolutionId!=null){
        this.platformService.findScreenResolution(this.testDevice.platformScreenResolutionId,  TestPlanLabType.TestsigmaLab).subscribe((platformResolution) => {
          this.testDevice.resolution = platformResolution.resolution;
        });
      }
    }
  }

  get isHybrid() {
    return this.testPlanResult?.testPlan?.isHybrid || this.testPlan?.isHybrid;
  }

  get isMobileWeb() {
    return this.testPlanResult?.testPlan?.workspaceVersion?.workspace?.isMobileWeb
    || this.testPlan?.workspaceVersion?.workspace?.isMobileWeb;
  }

  get isMobileNative() {
    return this.testPlanResult?.testPlan?.workspaceVersion?.workspace?.isMobileNative
    || this.testPlan?.workspaceVersion?.workspace?.isMobileNative;
  }

  get isMobile() {
    return this.testPlanResult?.testPlan?.workspaceVersion?.workspace?.isMobile
    || this.testPlan?.workspaceVersion?.workspace?.isMobile;
  }
  eventPrevent(event) {
    event.stopPropagation();
    event.stopImmediatePropagation();
  }
}
