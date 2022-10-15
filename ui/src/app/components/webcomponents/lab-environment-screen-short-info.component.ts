import {Component, Input, OnInit} from '@angular/core';
import {TestDevice} from "../../models/test-device.model";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {AgentService} from "../../agents/services/agent.service";
import {Agent} from "../../agents/models/agent.model";
import {AgentDevice} from "../../agents/models/agent-device.model";
import {DevicesService} from "../../agents/services/devices.service";
import {PlatformService} from "../../agents/services/platform.service";
import {Browser} from "../../agents/models/browser.model";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {Platform} from "../../enums/platform.enum";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";

@Component({
  selector: 'app-lab-environment-screen-short-info',
  template: `
    <div class="details-items pl-50 pr-20">
      <div class="align-items-center d-flex justify-content-start flex-wrap mb-10">
        <span class="mr-5" style="height: 20px;width: 20px;"
              [class.testsigma-lab-logo]="isTestsigmaLab"
              [class.testsigma-local-devices-logo]="isHybrid"
                [class.grid]="isPrivateGrid">
        </span>
        <span
          class="rb-medium"
          [translate]="'execution.lab_type.'+
            environmentResult.testPlanLabType" *ngIf="!isHybrid || !agent"></span>
        <a class="text-link"
           [routerLink]="['/agents', agent.id]" [textContent]="agent.title" *ngIf="isHybrid && agent"></a>
      </div>
      <div class="details-info mt-4">
        <div class="align-items-center d-flex justify-content-start ml-4 w-235 overflow-hidden">
                  <span class="mr-5 sm fa-mobile-alt-solid text-truncate" *ngIf="agentDevice"
                        [textContent]="agentDevice.name"></span>
          <span class="mr-5 sm" style="height: 14px"
                *ngIf="!agentDevice"
                [class.windows]="isWindows"
                [class.apple]="isMac||isIOS"
                [class.android]="isAndroid"
                [class.linux]="isLinux"></span>
          <span
            class="pr-8"
            [textContent]="isHybrid && agentDevice ? 'V. ' +agentDevice.osVersion : environmentResult?.testDeviceSettings.formattedOsVersion"></span>
          <span
            class="mr-5 sm" style="height: 14px"
            [class.fa-mobile-alt-solid]="!isMobileNative && testDevice?.deviceName && (isIOS || isAndroid)"></span>
          <span
            *ngIf="!isMobile"
            class="mr-5 sm" style="height: 14px"
            [class.chrome]="testDevice?.isChrome && testDevice?.formattedBrowserVersion"
            [class.safari]="testDevice?.isSafari && testDevice?.formattedBrowserVersion"
            [class.firefox]="testDevice?.isFirefox && testDevice?.formattedBrowserVersion"
            [class.edge]="testDevice?.isEdge && testDevice?.formattedBrowserVersion"></span>
          <span class="pr-8"
                *ngIf="!isMobile && testDevice?.formattedBrowserVersion"
                [textContent]="testDevice?.formattedBrowserVersion"></span>
          <span
            style="height: 14px"
            class="text-truncate pr-8 pb-16"
            *ngIf="!isHybrid && testDevice?.deviceName && (testDevice?.isIOS || testDevice?.isAndroid)"
            [textContent]="testDevice?.deviceName"></span>
          <span class="ml-5 sm" style="height: 14px"
                *ngIf="!isMobileNative && (testDevice?.isIOS || testDevice?.isAndroid) && testDevice?.browser"
                [class.chrome]="testDevice?.isChrome"
                [class.safari]="testDevice?.isSafari"></span>
          <span *ngIf="canShowResolution">
            <i class="fa-watch-tv pr-5 text-t-secondary"></i>
            <span
              [textContent]="testDevice?.resolution"></span>
          </span>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class LabEnvironmentScreenShortInfoComponent implements OnInit {
  @Input('testDevice') testDevice: TestDevice;
  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Input("environmentResult") environmentResult: TestDeviceResult;

  public agent: Agent;
  public agentDevice: AgentDevice;
  public browser :  Browser;

  constructor(
    private devicesService: DevicesService,
    private agentService: AgentService,
    private platformService : PlatformService,
    private versionService: WorkspaceVersionService) {
  }
  ngOnInit() {

  }
  ngOnChanges() {
    if(this.testPlanResult?.testPlan?.isHybrid) {
      this.agentService.findAll("id:"+this.testDevice?.agentId).subscribe(res=> {
        if(res.content.length) {
          this.agent = res.content[0]
          this.testDevice.platform = Agent.getPlatformFromOsType(this.agent.osType);
          this.testDevice.osVersion = this.agent.osVersion;
          this.browser = this.agent.browsers.find(browser => browser.name.toUpperCase() == this.testDevice.browser);
          this.testDevice.browserVersion = this.browser?.majorVersion;
        }
        if(this.testDevice.deviceId && this.agent) {
          this.devicesService.findAll(this.agent.id).subscribe(res => {
            this.agentDevice = res.content.find(device => device.id == this.testDevice.deviceId);
            this.testDevice.platform = AgentDevice.getPlatformFromMobileOStype(this.agentDevice.osName);
            this.testDevice.osVersion = this.agentDevice.osVersion;
            this.testDevice.browser = this.testDevice.platform === Platform.Android ? 'CHROME' : "SAFARI";
            this.testDevice.deviceName = this.agentDevice.name;
          })
        }
      });
    }
    if(this.testDevice.platformOsVersionId!= null){
      this.platformService.findOsVersion(this.testDevice.platformOsVersionId, this.testDevice.testPlanLabType).subscribe((platformOsversion) => {
        this.testDevice.platform = platformOsversion.platform;
        this.testDevice.osVersion = platformOsversion.version;
      });
    }
    if(this.testDevice.platformBrowserVersionId!=null){
      this.platformService.findBrowserVersion(this.testDevice.platformBrowserVersionId, this.testDevice.testPlanLabType).subscribe((platformBrowsersversion) => {
        this.testDevice.browser = platformBrowsersversion.name;
        this.testDevice.browserVersion = platformBrowsersversion.version;
      });
    }
    if(this.testDevice.platformDeviceId!=null){
        this.platformService.findDevice(this.testDevice.platformDeviceId, this.testDevice.testPlanLabType).subscribe((platformDevice) => {
        this.testDevice.deviceName = platformDevice.displayName;
      });
    }
    if(this.testDevice.platformScreenResolutionId!=null){
      this.platformService.findScreenResolution(this.testDevice.platformScreenResolutionId, this.testDevice.testPlanLabType).subscribe((platformResolution) => {
        this.testDevice.resolution = platformResolution.resolution;
      });
    }
    this.versionService.show(this.testDevice.workspaceVersionId).subscribe(res=> this.testDevice.version = res);
  }

  get canShowResolution() {
    return !this.testDevice?.resolution?.includes('?') && this.testDevice?.resolution && !this.isHybrid;
  }

  get isHybrid() {
    if (this.environmentResult.testPlanLabType==TestPlanLabType.Hybrid){
      return true;
    }
  }

  get isWindows(){
    if(this.environmentResult.testDeviceSettings.platform==Platform.Windows){
      return true
    }
  }
  get isMac(){
    if(this.environmentResult.testDeviceSettings.platform==Platform.Mac){
      return true
    }
  }
  get isLinux(){
    if(this.environmentResult.testDeviceSettings.platform==Platform.Linux){
      return true
    }
  }
  get isIOS(){
    if(this.environmentResult.testDeviceSettings.platform==Platform.iOS){
      return true
    }
  }
  get isAndroid(){
    if(this.environmentResult.testDeviceSettings.platform==Platform.Android){
      return true
    }
  }

  get isTestsigmaLab(){
    if (this.environmentResult.testPlanLabType==TestPlanLabType.TestsigmaLab){
      return true;
    }
  }

  get isPrivateGrid() {
    return this.testDevice?.isPrivateGrid;
  }

  get isMobileWeb() {
    return this.testDevice?.version?.workspace?.isMobileWeb;
  }

  get isMobileNative() {
    return this.testDevice?.version?.workspace?.isMobileNative;
  }

  get isMobile() {
    return this.testDevice?.version?.workspace?.isMobile;
  }


}
