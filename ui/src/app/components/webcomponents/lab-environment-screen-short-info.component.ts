import {Component, Input, OnInit} from '@angular/core';
import {TestDevice} from "../../models/test-device.model";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {AgentService} from "../../agents/services/agent.service";
import {Agent} from "../../agents/models/agent.model";
import {AgentDevice} from "../../agents/models/agent-device.model";
import {DevicesService} from "../../agents/services/devices.service";
import {PlatformService} from "../../agents/services/platform.service";
import {Browser} from "../../agents/models/browser.model";
import {PlatformOsVersion} from "../../agents/models/platform-os-version.model";
import {TestDeviceSettings} from "../../models/test-device-settings.model";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {Platform} from "../../enums/platform.enum";

@Component({
  selector: 'app-lab-environment-screen-short-info',
  template: `
    <div class="details-items pl-50 pr-20">
      <div class="align-items-center d-flex justify-content-start flex-wrap mb-10">
        <span class="mr-5" style="height: 20px;width: 20px;"
              [class.testsigma-lab-logo]="executionResult?.testPlan?.isTestsigmaLab"
              [class.testsigma-local-devices-logo]="isHybrid">
        </span>
        <span
          class="rb-medium"
          [translate]="'execution.lab_type.'+
            executionResult?.testPlan?.testPlanLabType" *ngIf="!isHybrid || !agent"></span>
        <a class="text-link"
           [routerLink]="['/agents', agent.id]" [textContent]="agent.title" *ngIf="isHybrid && agent"></a>
      </div>
      <div class="details-info mt-4">
        <div class="align-items-center d-flex justify-content-start ml-4 w-235 overflow-hidden">
                  <span class="mr-5 sm fa-mobile-alt-solid text-truncate" *ngIf="agentDevice"
                        [textContent]="agentDevice.name"></span>
          <span class="mr-5 sm" style="height: 14px"
                *ngIf="!agentDevice"
                [class.windows]="executionEnvironment?.isWindows"
                [class.apple]="executionEnvironment?.isMac||executionEnvironment?.isIOS"
                [class.android]="executionEnvironment?.isAndroid"
                [class.linux]="executionEnvironment?.isLinux"></span>
          <span
            class="pr-8"
            [textContent]="isHybrid && agentDevice ? 'V. ' +agentDevice.osVersion : executionEnvironment?.formattedOsVersion"></span>
          <span
            class="mr-5 sm" style="height: 14px"
            [class.fa-mobile-alt-solid]="executionEnvironment?.deviceName && (executionEnvironment?.isIOS || executionEnvironment?.isAndroid)"></span>
          <span
            *ngIf="!isMobile"
            class="mr-5 sm" style="height: 14px"
            [class.chrome]="executionEnvironment?.isChrome && executionEnvironment?.formattedBrowserVersion"
            [class.safari]="executionEnvironment?.isSafari && executionEnvironment?.formattedBrowserVersion"
            [class.firefox]="executionEnvironment?.isFirefox && executionEnvironment?.formattedBrowserVersion"
            [class.edge]="executionEnvironment?.isEdge && executionEnvironment?.formattedBrowserVersion"></span>
          <span class="pr-8"
                *ngIf="!isMobile && executionEnvironment?.formattedBrowserVersion"
                [textContent]="executionEnvironment?.formattedBrowserVersion"></span>
          <span
            style="height: 14px"
            class="text-truncate pr-8 pb-16"
            *ngIf="!isHybrid && executionEnvironment?.deviceName && (executionEnvironment?.isIOS || executionEnvironment?.isAndroid)"
            [textContent]="executionEnvironment?.deviceName"></span>
          <span class="ml-5 sm" style="height: 14px"
                *ngIf="(executionEnvironment?.isIOS || executionEnvironment?.isAndroid) && executionEnvironment?.browser"
                [class.chrome]="executionEnvironment?.isChrome"
                [class.safari]="executionEnvironment?.isSafari"></span>
          <span *ngIf="canShowResolution">
            <i class="fa-watch-tv pr-5 text-t-secondary"></i>
            <span
              [textContent]="executionEnvironment?.resolution"></span>
          </span>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class LabEnvironmentScreenShortInfoComponent implements OnInit {
  @Input('executionEnvironment') executionEnvironment: TestDevice;
  @Input('executionResult') executionResult: TestPlanResult;
  @Input("environmentResult") environmentResult: TestDeviceResult;

  public agent: Agent;
  public agentDevice: AgentDevice;
  public browser :  Browser;

  constructor(
    private devicesService: DevicesService,
    private agentService: AgentService,
    private platformService : PlatformService) {
  }
  ngOnInit() {

  }
  ngOnChanges() {
    if(this.executionResult?.testPlan?.isHybrid) {
      this.agentService.findAll("id:"+this.executionEnvironment?.agentId).subscribe(res=> {
        if(res.content.length) {
          this.agent = res.content[0]
          this.executionEnvironment.platform = Agent.getPlatformFromOsType(this.agent.osType);
          this.executionEnvironment.osVersion = this.agent.osVersion;
          this.browser = this.agent.browsers.find(browser => browser.name.toUpperCase() == this.executionEnvironment.browser);
          this.executionEnvironment.browserVersion = this.browser?.majorVersion;
        }
        if(this.executionEnvironment.deviceId && this.agent) {
          this.devicesService.findAll(this.agent.id).subscribe(res => {
            this.agentDevice = res.content.find(device => device.id == this.executionEnvironment.deviceId);
            this.executionEnvironment.platform = AgentDevice.getPlatformFromMobileOStype(this.agentDevice.osName);
            this.executionEnvironment.osVersion = this.agentDevice.osVersion;
            this.executionEnvironment.browser = this.executionEnvironment.platform === Platform.Android ? 'CHROME' : "SAFARI";
            this.executionEnvironment.deviceName = this.agentDevice.name;
          })
        }
      });
    }
    if(this.executionEnvironment.platformOsVersionId!= null){
      this.platformService.findOsVersion(this.executionEnvironment.platformOsVersionId, this.executionResult.testPlan.testPlanLabType).subscribe((platformOsversion) => {
        this.executionEnvironment.platform = platformOsversion.platform;
        this.executionEnvironment.osVersion = platformOsversion.version;
      });
    }
    if(this.executionEnvironment.platformBrowserVersionId!=null){
      this.platformService.findBrowserVersion(this.executionEnvironment.platformBrowserVersionId, this.executionResult.testPlan.testPlanLabType).subscribe((platformBrowsersversion) => {
        this.executionEnvironment.browser = platformBrowsersversion.name;
        this.executionEnvironment.browserVersion = platformBrowsersversion.version;
      });
    }
    if(this.executionEnvironment.platformDeviceId!=null){
        this.platformService.findDevice(this.executionEnvironment.platformDeviceId, this.executionResult.testPlan.testPlanLabType).subscribe((platformDevice) => {
        this.executionEnvironment.deviceName = platformDevice.displayName;
      });
    }
    if(this.executionEnvironment.platformScreenResolutionId!=null){
      this.platformService.findScreenResolution(this.executionEnvironment.platformScreenResolutionId, this.executionResult.testPlan.testPlanLabType).subscribe((platformResolution) => {
        this.executionEnvironment.resolution = platformResolution.resolution;
      });
    }
  }

  get canShowResolution() {
    return !this.executionEnvironment?.resolution?.includes('?') && this.executionEnvironment?.resolution && !this.isHybrid;
  }

  get isHybrid() {
    return this.executionResult?.testPlan?.isHybrid;
  }

  get isMobileWeb() {
    return this.executionResult?.testPlan?.workspaceVersion?.workspace?.isMobileWeb;
  }

  get isMobileNative() {
    return this.executionResult?.testPlan?.workspaceVersion?.workspace?.isMobileNative;
  }

  get isMobile() {
    return this.executionResult?.testPlan?.workspaceVersion?.workspace?.isMobile;
  }


}
