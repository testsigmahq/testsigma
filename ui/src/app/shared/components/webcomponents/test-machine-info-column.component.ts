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
    <span>
        <a
          (click)="eventPrevent($event)"
          class="text-link"
           [routerLink]="['/agents', agent.id]"
          [matTooltip]="agent.title.length > 15 ? agent.title: ''"
          [textContent]="agent.title.length > 15 ? agent.title.slice(0, 15)+ '...': agent.title" *ngIf="isHybrid && agent"></a>
        <span
          class="pl-5 sm fa-mobile-alt-solid mw-30 text-truncate-d-block"
          *ngIf="agentDevice"
          [textContent]="agentDevice.name"
          appTooltipOnEllipsis></span>
        <span class="text-nowrap">
          <i
            *ngIf="!isHybrid"
            [class.fa-windows-brands]="executionEnvironment?.isWindows"
            [class.fa-apple]="executionEnvironment?.isMac || executionEnvironment?.isIOS"
            [class.fa-linux-2]="executionEnvironment?.isLinux"
            [class.fa-android-solid]="executionEnvironment?.isAndroid"></i>
        <span class="pl-5 text-nowrap"
              [textContent]="isHybrid && agentDevice ? 'V. ' +agentDevice.osVersion : executionEnvironment?.formattedOsVersion"></span>
        </span>
      </span>
    <div
      *ngIf="!isHybrid"
      class="px-10 text-nowrap text-left">
          <i [class.fa-mobile-alt-solid]="executionEnvironment?.deviceName
          && (executionEnvironment?.isIOS || executionEnvironment?.isAndroid)"></i>
        <i
           [class.fa-chrome]="executionEnvironment?.isChrome"
           [class.fa-firefox-brands]="executionEnvironment?.isFirefox"
           [class.fa-safari-brands]="executionEnvironment?.isSafari"
           [class.fa-edge]="executionEnvironment?.isEdge"></i>
        <span class="pl-5"
              *ngIf="executionEnvironment?.formattedBrowserVersion"
              [textContent]="executionEnvironment?.formattedBrowserVersion"></span>
        <span class="pl-5 text-truncate-d-block mw-50"
              *ngIf="executionEnvironment?.deviceName && (executionEnvironment?.isIOS || executionEnvironment?.isAndroid)"
              [textContent]="executionEnvironment?.deviceName"></span>
      <i class="pl-5"
         *ngIf="isHybrid && (executionEnvironment?.isIOS || executionEnvironment?.isAndroid)"
         [class.fa-chrome]="executionEnvironment?.isChrome"
         [class.fa-firefox-brands]="executionEnvironment?.isFirefox"
         [class.fa-safari-brands]="executionEnvironment?.isSafari"
         [class.fa-edge]="executionEnvironment?.isEdge"></i>
    </div>
  `,
  styles: [],
  host:{"class":"d-flex"}
})
export class TestMachineInfoColumnComponent implements OnInit {
  public environmentSettings: TestDeviceSettings;
  @Input("environmentResult") environmentResult: TestDeviceResult;
  @Input('executionEnvironment') executionEnvironment: TestDevice;
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
      this.environmentSettings = this.executionEnvironment?.settings;
    }

  }

  ngOnChanges(){
    if(this.isHybrid) {
      this.agentService.findAll("id:"+this.executionEnvironment?.agentId).subscribe(res=> {
        if(res.content?.length){
          this.agent = res.content[0]
          this.executionEnvironment.platform = Agent.getPlatformFromOsType(this.agent.osType);
          this.executionEnvironment.osVersion = this.agent.osVersion;
          if(this.executionEnvironment.browser!=null) {
            this.executionEnvironment.browserVersion = this.agent.browsers.find(browser =>
              browser.name.toUpperCase() == this.executionEnvironment.browser).majorVersion;
          }
        }
        if(this.executionEnvironment?.deviceId && this.agent)
          this.devicesService.findAll(this.agent.id).subscribe(res => {
            this.agentDevice = res.content.find(device => device.id == this.executionEnvironment.deviceId);
            this.executionEnvironment.platform = AgentDevice.getPlatformFromMobileOStype(this.agentDevice.osName);
            this.executionEnvironment.osVersion = this.agentDevice.osVersion;
            this.executionEnvironment.browser = this.executionEnvironment.platform === Platform.Android ? 'CHROME' : "SAFARI";
            this.executionEnvironment.deviceName = this.agentDevice.name;
          })
      });
    }
    else{
      if(this.executionEnvironment?.platformOsVersionId!= null){
        this.platformService.findOsVersion(this.executionEnvironment.platformOsVersionId, TestPlanLabType.TestsigmaLab).subscribe((platformOsversion) => {
          this.executionEnvironment.platform = platformOsversion.platform;
          this.executionEnvironment.osVersion = platformOsversion.version;
        });
      }
      if(this.executionEnvironment?.platformBrowserVersionId!=null){
        this.platformService.findBrowserVersion(this.executionEnvironment.platformBrowserVersionId,  TestPlanLabType.TestsigmaLab).subscribe((platformBrowsersversion) => {
          this.executionEnvironment.browser = platformBrowsersversion.name;
          this.executionEnvironment.browserVersion = platformBrowsersversion.version;
        });
      }
      if(this.executionEnvironment?.platformDeviceId!=null){
        this.platformService.findDevice(this.executionEnvironment.platformDeviceId,  TestPlanLabType.TestsigmaLab).subscribe((platformDevice) => {
          this.executionEnvironment.deviceName = platformDevice.displayName;
        });
      }
      if(this.executionEnvironment?.platformScreenResolutionId!=null){
        this.platformService.findScreenResolution(this.executionEnvironment.platformScreenResolutionId,  TestPlanLabType.TestsigmaLab).subscribe((platformResolution) => {
          this.executionEnvironment.resolution = platformResolution.resolution;
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
