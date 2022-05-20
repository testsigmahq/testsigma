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
      <span class="d-inline-block mr-5 no-bg-transparent"
            style="height: 20px;min-width: 20px"
            *ngIf="testPlan.testPlanLabType"
            [class.testsigma-lab-logo]="testPlan.isTestsigmaLab || testPlan.isHybrid"
            [class.grid]="testPlan.isPrivateLab"
            [matTooltip]="('testPlan.lab_type.'+testPlan.testPlanLabType) | translate"></span>
      <span *ngIf="isHybridMobileWeb">
        <span *ngFor="let env of executionEnvironments"
              [matTooltip]="
            ('platform.name.'+env?.platform | translate) + '( '+ env?.osVersion+' ) '+
            (env?.browser? ('browser.name.'+env?.browser | translate) : env?.deviceName) +
            (env?.browserVersion ? '( '+env?.browserVersion+' )' : '')">
            <span class="mr-5 fz-18 text-t-secondary"
                  [class.fa-apple]="env?.isIOS"
                  [class.fa-android-solid]="env?.isAndroid"
            ></span>
            <span class="mr-5 fz-18 text-t-secondary"
                  [class.fa-chrome]="env?.isChrome"
                  [class.fa-firefox-brands]="env?.isFirefox"
                  [class.fa-safari-brands]="env?.isSafari"
                  [class.fa-edge]="env?.isEdge"
            ></span>
          </span>
      </span>
      <span *ngIf="!isHybridMobileNative && !isHybridMobileWeb">
      <span *ngFor="let env of executionEnvironments"
            [matTooltip]="
            ('platform.name.'+env?.platform | translate) + '( '+ env?.osVersion+' ) '+
            (env?.browser? ('browser.name.'+env?.browser | translate) : env?.deviceName) +
            (env?.browserVersion ? '( '+env?.browserVersion+' )' : '')"
            class="mr-5 fz-18 text-t-secondary"
            [class.fa-chrome]="env?.isChrome"
            [class.fa-firefox-brands]="env?.isFirefox"
            [class.fa-safari-brands]="env?.isSafari"
            [class.fa-edge]="env?.isEdge"
            [class.fa-apple]="!env?.browser && env?.isIOS"
            [class.fa-android-solid]="!env?.browser && env?.isAndroid"
      ></span>
      </span>
      <span *ngIf="isHybridMobileNative && !isHybridMobileWeb">
      <span
        *ngFor="let env of executionEnvironments"
        [matTooltip]="env?.title"
        class="mr-5 fz-18 text-t-secondary"
        [class.fa-apple]="isIosNative"
        [class.fa-android-solid]="isAndroidNative"></span>
      </span>
    </div>
  `,
  styles: []
})
export class LabEnvironmentsInfoComponent implements OnInit {
  @Input('testPlan') testPlan: TestPlan;
  @Input('executionEnvironments') executionEnvironments : TestDevice[];

  constructor( private platformService: PlatformService,
               private devicesService: DevicesService,
               private agentService: AgentService) {
  }

  ngOnInit() {
  }

  ngOnChanges(changes : SimpleChanges) : void{
    this.executionEnvironments.forEach((env)=>{
      if(this.testPlan.testPlanLabType == TestPlanLabType.Hybrid){
        this.agentService.findAll("id:"+env?.agentId).subscribe(res=> {
          let agent;
          if(res.content.length){
            agent = res.content[0];
            if(env.browser!=null){
              env.platform = Agent.getPlatformFromOsType(agent.osType);
              env.osVersion = agent.osVersion;
              let browser = agent.browsers.find(browser => browser.name.toUpperCase() == env.browser)
              env.browserVersion = browser.majorVersion;
            }
          }
          if(env.deviceId && agent)
            this.devicesService.findAll(agent.id).subscribe(res => {
              let agentDevice = res.content.find(device => device.id == env.deviceId);
              console.log(agentDevice);
              env.platform = AgentDevice.getPlatformFromMobileOStype(agentDevice.osName);
              env.osVersion = agentDevice.osVersion;
              env.browser = env.platform === Platform.Android ? 'CHROME' : "SAFARI";
              env.deviceName = agentDevice.name;
            })
        });
      }
      else{
        if (env.platformOsVersionId != null) {
          this.platformService.findOsVersion(env.platformOsVersionId, this.testPlan.testPlanLabType).subscribe((platformOsversion) => {
            env.platform =  platformOsversion.platform;
            env.osVersion = platformOsversion.version;
          });
        }
        if (env.platformBrowserVersionId != null) {
          this.platformService.findBrowserVersion(env.platformBrowserVersionId, this.testPlan.testPlanLabType).subscribe((platformBrowsersversion) => {
            env.browser = platformBrowsersversion.name.toUpperCase();
            env.browserVersion = platformBrowsersversion.version;
          });
        }
        if (env.platformDeviceId != null) {
          this.platformService.findDevice(env.platformDeviceId, this.testPlan.testPlanLabType).subscribe((platformDevice) => {
            env.deviceName = platformDevice.displayName;
          });
        }
        if (env.platformScreenResolutionId != null) {
          this.platformService.findScreenResolution(env.platformScreenResolutionId, this.testPlan.testPlanLabType).subscribe((platformResolution) => {
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
