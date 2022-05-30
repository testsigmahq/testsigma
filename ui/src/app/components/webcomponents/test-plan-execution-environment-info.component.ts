import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TestDevice} from "../../models/test-device.model";
import {AgentService} from "../../agents/services/agent.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanType} from "../../enums/execution-type.enum";
import {PlatformService} from "../../agents/services/platform.service";
import {Agent} from "../../agents/models/agent.model";
import {AgentDevice} from "../../agents/models/agent-device.model";
import {Platform} from "../../enums/platform.enum";
import { DevicesService } from 'app/agents/services/devices.service';

@Component({
  selector: 'app-test-plan-execution-environment-info',
  template: `
      <div class="text-t-secondary d-flex flex-column execution-list-item">
          <div *ngIf="!isHybrid" class="d-flex justify-content-between">
              <div class="d-flex">
                  <i (click)="remove()"
                     [matTooltip]="'hint.message.common.delete' | translate"
                     class="pointer fa-close-alt mr-8 mt-8 position-absolute right-0 top-0" style="font-size:8px"></i>
                  <i [class.fa-apple]="testDevice?.isIOS || testDevice?.isMac"
                     [class.fa-windows-brands]="testDevice?.isWindows"
                     [class.fa-linux-2]="testDevice?.isLinux"
                     [class.fa-android-solid]="testDevice?.isAndroid"></i>
                  <span class="ml-4" [textContent]="testDevice?.formattedOsVersion"></span>
              </div>
              <div class="d-flex mw-80 text-nowrap" *ngIf="version?.workspace?.isMobile">
                  <i class="ml-4"
                     [class.fa-mobile-alt-solid]="testDevice?.deviceName"></i>
                  <span class="d-inline-block mw-90 text-truncate"
                        [textContent]="testDevice?.deviceName"></span>
              </div>
              <div class="d-flex text-nowrap" *ngIf="(version?.workspace?.isMobileWeb || version?.workspace?.isWeb)">
                  <i class="ml-4"
                     [class.fa-chrome]="testDevice?.isChrome"
                     [class.fa-firefox]="testDevice?.isFirefox"
                     [class.fa-safari-brands]="testDevice?.isSafari"
                     [class.fa-edge]="testDevice?.isEdge"></i>
                  <span class="ml-4" *ngIf="!version?.workspace?.isMobileWeb"
                        [textContent]="testDevice?.formattedBrowserVersion"></span>
                  <span class="ml-15" [textContent]="testDevice?.resolution"></span>
              </div>
          </div>
          <div *ngIf="isHybrid" class="d-flex justify-content-around">
              <div class="text-truncate">
                  <i (click)="remove()"
                     [matTooltip]="'hint.message.common.delete' | translate"
                     class="pointer fa-close-alt mr-8 mt-8 position-absolute right-0 top-0" style="font-size:8px"></i>
                  <i
                          [class.fa-apple]="testDevice.agent?.isMac || testDevice?.isMac || testDevice?.isIOS"
                          [class.fa-android-solid]="testDevice?.isAndroid"
                          [class.fa-windows-brands]="testDevice.agent?.isWindows || testDevice?.isWindows"
                          [class.fa-linux-2]="testDevice.agent?.isLinux || testDevice?.isLinux"></i>
                  <span class="ml-4" [textContent]="testDevice.agent?.title"></span>
              </div>
              <div class="d-flex mw-80" *ngIf="version?.workspace?.isMobile">
                  <i class="ml-4"
                     [class.fa-mobile-alt-solid]="testDevice.deviceId"></i>
                  <span class="d-inline-block mw-90 text-truncate"
                        [textContent]="testDevice?.deviceName"></span>
              </div>
              <div class="d-flex" *ngIf="(version?.workspace?.isMobileWeb || version?.workspace?.isWeb)">
                  <i class="ml-4"
                     [class.fa-chrome]="testDevice?.isChrome"
                     [class.fa-firefox]="testDevice?.isFirefox"
                     [class.fa-safari-brands]="testDevice?.isSafari"
                     [class.fa-edge]="testDevice?.isEdge"></i>
                  <span class="ml-4" *ngIf="!version?.workspace?.isMobileWeb"
                        [textContent]="testDevice?.formattedBrowserVersion"></span>
              </div>
          </div>
          <div class="d-flex text-dark pt-10 pl-10" *ngIf="advancedSettingsEnabled">
        <span
                [textContent]="'test_plan.environments.no_of_suites' | translate : {count: (testDevice.testSuites?.length || 0)}"></span>
          </div>
      </div>
  `,
  styles: []
})
export class TestPlanExecutionEnvironmentInfoComponent implements OnInit {
  @Input('testDevice') testDevice: TestDevice;
  @Input('testPlan') testPlan: TestPlan;
  @Input('version') version: WorkspaceVersion;
  @Input('testPlanLabType') testPlanLabType: TestPlanLabType;
  @Input('index') index: number;
  @Output('onRemove') onRemove = new EventEmitter<void>();

  constructor(private agentService: AgentService,
              private devicesService: DevicesService,
              private platformService: PlatformService) {
  }

  get isHybrid() {
    return this.testPlanLabType == TestPlanLabType.Hybrid;
  }

  get advancedSettingsEnabled() {
    return this.testPlan.testPlanType == TestPlanType.DISTRIBUTED;
  }

  ngOnInit(): void {
  }


  ngOnChanges() {
    if(this.isHybrid){
      this.agentService.findAll("id:"+this.testDevice?.agentId).subscribe(res=> {
        let agent;
        if(res.content.length){
          agent = res.content[0];
          if(this.testDevice.browser!=null){
            this.testDevice.platform = Agent.getPlatformFromOsType(agent.osType);
            this.testDevice.osVersion = agent.osVersion;
            let browser = agent.browsers.find(browser => browser.name.toUpperCase() == this.testDevice.browser)
            this.testDevice.browserVersion = browser.majorVersion;
          }
        }
        if(this.testDevice.deviceId && agent)
          this.devicesService.findAll(agent.id).subscribe(res => {
            let agentDevice = res.content.find(device => device.id == this.testDevice.deviceId);
            console.log(agentDevice);
            this.testDevice.platform = AgentDevice.getPlatformFromMobileOStype(agentDevice.osName);
            this.testDevice.osVersion = agentDevice.osVersion;
            this.testDevice.browser = this.testDevice.platform === Platform.Android ? 'CHROME' : "SAFARI";
            this.testDevice.deviceName = agentDevice.name;
          })
      });
    }
    else{
      if (this.testDevice.platformOsVersionId != null) {
        this.platformService.findOsVersion(this.testDevice.platformOsVersionId, this.testPlanLabType).subscribe((platformOsversion) => {
          this.testDevice.platform = platformOsversion.platform;
          this.testDevice.osVersion = platformOsversion.version;
        });
      }
      if (this.testDevice.platformBrowserVersionId != null) {
        this.platformService.findBrowserVersion(this.testDevice.platformBrowserVersionId, this.testPlanLabType).subscribe((platformBrowsersversion) => {
          this.testDevice.browser = platformBrowsersversion.name.toUpperCase();
          this.testDevice.browserVersion = platformBrowsersversion.version;
        });
      }
      if (this.testDevice.platformDeviceId != null) {
        this.platformService.findDevice(this.testDevice.platformDeviceId, this.testPlanLabType).subscribe((platformDevice) => {
          this.testDevice.deviceName = platformDevice.displayName;
        });
      }
      if (this.testDevice.platformScreenResolutionId != null) {
        this.platformService.findScreenResolution(this.testDevice.platformScreenResolutionId, this.testPlanLabType).subscribe((platformResolution) => {
          this.testDevice.resolution = platformResolution.resolution;
        });
      }
    }
    if (this.isHybrid && this.testDevice.agentId)
      this.agentService.findAll("id:" + this.testDevice.agentId).subscribe(res => {
        if (res.content.length)
          this.testDevice.agent = res.content[0];
      })
  }

  remove() {
    this.onRemove.emit();
  }

}
