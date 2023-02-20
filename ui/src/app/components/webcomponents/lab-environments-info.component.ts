/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {TestPlan} from "../../models/test-plan.model";
import {PlatformService} from "../../agents/services/platform.service";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {Agent} from "../../agents/models/agent.model";
import {AgentDevice} from "../../agents/models/agent-device.model";
import {Platform} from "../../enums/platform.enum";
import {DevicesService} from "../../agents/services/devices.service";
import {AgentService} from "../../agents/services/agent.service";
import {TestDevice} from "../../models/test-device.model";

@Component({
  selector: 'app-lab-environments-info',
  template: `
    <div class="align-items-center d-flex justify-content-start flex-wrap">
        <span *ngFor="let env of testPlan.testDevices" class="align-items-center d-flex justify-content-start flex-wrap">
          <span class="d-inline-block mr-5 no-bg-transparent"
                style="height: 20px;min-width: 20px"
                *ngIf="env.testPlanLabType"
                [class.testsigma-lab-logo]="env.isTestsigmaLab"
                [class.testsigma-local-devices-logo]="env.isHybrid"
                [matTooltip]="('execution.lab_type.'+env.testPlanLabType) | translate"></span>
           <span
             [matTooltip]="
            (env?.platform ? ('platform.name.'+env?.platform | translate) : '') +
            ((env?.platform && env?.osVersion)? '( '+ env?.osVersion+' ) ' : ' ')"
             class="mr-5 fz-18 text-t-secondary"
             [class.fa-windows-brands]="env?.isWindows"
             [class.fa-apple]="env?.isMac"
             [class.fa-linux-2]="env.isLinux"></span>
          <span
            [matTooltip]="
            (env?.browser=='MOZILLAFIREFOX'? ('browser.name.FIREFOX' | translate):env?.browser?('browser.name.'+env?.browser | translate) : env?.deviceName) +
            (env?.browserVersion ? '( '+env?.browserVersion+' )' : '')"
            class="mr-5 fz-18 text-t-secondary"
            [class.fa-chrome]="env?.isChrome"
            [class.fa-firefox-brands]="env?.isFirefox"
            [class.fa-safari-brands]="env.isSafari"
            [class.fa-edge]="env?.isEdge"
            [class.fa-apple]="!env?.browser && env?.isIOS"
            [class.fa-android-solid]="!env?.browser && env?.isAndroid"></span>
        </span>
    </div>
  `,
  styles: []
})
export class LabEnvironmentsInfoComponent implements OnInit {
  @Input('testPlan') testPlan: TestPlan;
  @Input('testDevices') testDevices : TestDevice[];

  public agent:Agent;

  constructor( private platformService: PlatformService,
               private devicesService: DevicesService,
               private agentService: AgentService) {
  }

  ngOnInit() {
  }

  ngOnChanges(changes : SimpleChanges) : void{
    this.testDevices.forEach((env)=>{
      if(env.testPlanLabType == TestPlanLabType.Hybrid){
        this.agentService.findAll("id:"+env?.agentId).subscribe(res=> {
          if(res.content.length){
            this.agent = res.content[0];
            if(env.browser!=null){
              env.platform = this.agent.getPlatformFromOsType(this.agent.osType);
              env.osVersion = this.agent.osVersion;
              let browser = this.agent.browsers.find(browser => browser.name.toUpperCase() == env.browser)
              env.browserVersion = browser.majorVersion;
            }
          }
          if(env.deviceId && this.agent)
            this.devicesService.findAll(this.agent.id).subscribe(res => {
              let agentDevice = res.content.find(device => device.id == env.deviceId);
              console.log(agentDevice);
              env.platform = AgentDevice.getPlatformFromMobileOStype(agentDevice.osName);
              env.osVersion = agentDevice.osVersion;
              //env.browser = env.platform === Platform.Android ? 'CHROME' : "SAFARI";
              env.deviceName = agentDevice.name;
            })
        });
      }
      else{
        if (env.platformOsVersionId != null) {

          this.platformService.findOsVersion(env.platformOsVersionId, env.testPlanLabType).subscribe((platformOsversion) => {
            env.platform =  platformOsversion.platform;
            env.osVersion = platformOsversion.version;
          });
        }
        if (env.platformBrowserVersionId != null) {
          this.platformService.findBrowserVersion(env.platformBrowserVersionId, env.testPlanLabType).subscribe((platformBrowsersversion) => {
            env.browser = platformBrowsersversion.name.toUpperCase();
            env.browserVersion = platformBrowsersversion.version;
          });
        }
        if (env.platformDeviceId != null) {
          this.platformService.findDevice(env.platformDeviceId, env.testPlanLabType).subscribe((platformDevice) => {
            env.deviceName = platformDevice.displayName;
          });
        }
        if (env.platformScreenResolutionId != null) {
          this.platformService.findScreenResolution(env.platformScreenResolutionId, env.testPlanLabType).subscribe((platformResolution) => {
            env.resolution = platformResolution.resolution;
          });
        }
      }
    });
  }

  get isHybridMobileNative() {
    return this.testPlan?.isHybrid && this.testPlan?.workspaceVersion?.workspace?.isMobileNative;
  }

  get isAndroidNative() {
    return this.testPlan?.workspaceVersion?.workspace?.isAndroidNative;
  }

  get isIosNative() {
    return this.testPlan?.workspaceVersion?.workspace?.isIosNative;
  }

  get isHybridMobileWeb(){
    return this.testPlan?.isHybrid && this.testPlan?.workspaceVersion?.workspace?.isMobileWeb;
  }

}
