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
                  <i [class.fa-apple]="executionEnvironment?.isIOS || executionEnvironment?.isMac"
                     [class.fa-windows-brands]="executionEnvironment?.isWindows"
                     [class.fa-linux-2]="executionEnvironment?.isLinux"
                     [class.fa-android-solid]="executionEnvironment?.isAndroid"></i>
                  <span class="ml-4" [textContent]="executionEnvironment?.formattedOsVersion"></span>
              </div>
              <div class="d-flex mw-80 text-nowrap" *ngIf="version?.workspace?.isMobile">
                  <i class="ml-4"
                     [class.fa-mobile-alt-solid]="executionEnvironment?.deviceName"></i>
                  <span class="d-inline-block mw-90 text-truncate"
                        [textContent]="executionEnvironment?.deviceName"></span>
              </div>
              <div class="d-flex text-nowrap" *ngIf="(version?.workspace?.isMobileWeb || version?.workspace?.isWeb)">
                  <i class="ml-4"
                     [class.fa-chrome]="executionEnvironment?.isChrome"
                     [class.fa-firefox]="executionEnvironment?.isFirefox"
                     [class.fa-safari-brands]="executionEnvironment?.isSafari"
                     [class.fa-edge]="executionEnvironment?.isEdge"></i>
                  <span class="ml-4" *ngIf="!version?.workspace?.isMobileWeb"
                        [textContent]="executionEnvironment?.formattedBrowserVersion"></span>
                  <span class="ml-15" [textContent]="executionEnvironment?.resolution"></span>
              </div>
          </div>
          <div *ngIf="isHybrid" class="d-flex justify-content-around">
              <div class="text-truncate">
                  <i (click)="remove()"
                     [matTooltip]="'hint.message.common.delete' | translate"
                     class="pointer fa-close-alt mr-8 mt-8 position-absolute right-0 top-0" style="font-size:8px"></i>
                  <i
                          [class.fa-apple]="executionEnvironment.agent?.isMac || executionEnvironment?.isMac || executionEnvironment?.isIOS"
                          [class.fa-android-solid]="executionEnvironment?.isAndroid"
                          [class.fa-windows-brands]="executionEnvironment.agent?.isWindows || executionEnvironment?.isWindows"
                          [class.fa-linux-2]="executionEnvironment.agent?.isLinux || executionEnvironment?.isLinux"></i>
                  <span class="ml-4" [textContent]="executionEnvironment.agent?.title"></span>
              </div>
              <div class="d-flex mw-80" *ngIf="version?.workspace?.isMobile">
                  <i class="ml-4"
                     [class.fa-mobile-alt-solid]="executionEnvironment.deviceId"></i>
                  <span class="d-inline-block mw-90 text-truncate"
                        [textContent]="executionEnvironment?.deviceName"></span>
              </div>
              <div class="d-flex" *ngIf="(version?.workspace?.isMobileWeb || version?.workspace?.isWeb)">
                  <i class="ml-4"
                     [class.fa-chrome]="executionEnvironment?.isChrome"
                     [class.fa-firefox]="executionEnvironment?.isFirefox"
                     [class.fa-safari-brands]="executionEnvironment?.isSafari"
                     [class.fa-edge]="executionEnvironment?.isEdge"></i>
                  <span class="ml-4" *ngIf="!version?.workspace?.isMobileWeb"
                        [textContent]="executionEnvironment?.formattedBrowserVersion"></span>
              </div>
          </div>
          <div class="d-flex text-dark pt-10 pl-10" *ngIf="advancedSettingsEnabled">
        <span
                [textContent]="'test_plan.environments.no_of_suites' | translate : {count: (executionEnvironment.testSuites?.length || 0)}"></span>
          </div>
      </div>
  `,
  styles: []
})
export class TestPlanExecutionEnvironmentInfoComponent implements OnInit {
  @Input('executionEnvironment') executionEnvironment: TestDevice;
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
      this.agentService.findAll("id:"+this.executionEnvironment?.agentId).subscribe(res=> {
        let agent;
        if(res.content.length){
          agent = res.content[0];
          if(this.executionEnvironment.browser!=null){
            this.executionEnvironment.platform = Agent.getPlatformFromOsType(agent.osType);
            this.executionEnvironment.osVersion = agent.osVersion;
            let browser = agent.browsers.find(browser => browser.name.toUpperCase() == this.executionEnvironment.browser)
            this.executionEnvironment.browserVersion = browser.majorVersion;
          }
        }
        if(this.executionEnvironment.deviceId && agent)
          this.devicesService.findAll(agent.id).subscribe(res => {
            let agentDevice = res.content.find(device => device.id == this.executionEnvironment.deviceId);
            console.log(agentDevice);
            this.executionEnvironment.platform = AgentDevice.getPlatformFromMobileOStype(agentDevice.osName);
            this.executionEnvironment.osVersion = agentDevice.osVersion;
            this.executionEnvironment.browser = this.executionEnvironment.platform === Platform.Android ? 'CHROME' : "SAFARI";
            this.executionEnvironment.deviceName = agentDevice.name;
          })
      });
    }
    else{
      if (this.executionEnvironment.platformOsVersionId != null) {
        this.platformService.findOsVersion(this.executionEnvironment.platformOsVersionId, this.testPlanLabType).subscribe((platformOsversion) => {
          this.executionEnvironment.platform = platformOsversion.platform;
          this.executionEnvironment.osVersion = platformOsversion.version;
        });
      }
      if (this.executionEnvironment.platformBrowserVersionId != null) {
        this.platformService.findBrowserVersion(this.executionEnvironment.platformBrowserVersionId, this.testPlanLabType).subscribe((platformBrowsersversion) => {
          this.executionEnvironment.browser = platformBrowsersversion.name.toUpperCase();
          this.executionEnvironment.browserVersion = platformBrowsersversion.version;
        });
      }
      if (this.executionEnvironment.platformDeviceId != null) {
        this.platformService.findDevice(this.executionEnvironment.platformDeviceId, this.testPlanLabType).subscribe((platformDevice) => {
          this.executionEnvironment.deviceName = platformDevice.displayName;
        });
      }
      if (this.executionEnvironment.platformScreenResolutionId != null) {
        this.platformService.findScreenResolution(this.executionEnvironment.platformScreenResolutionId, this.testPlanLabType).subscribe((platformResolution) => {
          this.executionEnvironment.resolution = platformResolution.resolution;
        });
      }
    }
    if (this.isHybrid && this.executionEnvironment.agentId)
      this.agentService.findAll("id:" + this.executionEnvironment.agentId).subscribe(res => {
        if (res.content.length)
          this.executionEnvironment.agent = res.content[0];
      })
  }

  remove() {
    this.onRemove.emit();
  }

}
