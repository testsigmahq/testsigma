/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Agent} from "./agent.model";
import {AgentDevice} from "./agent-device.model";
import {Upload} from "../../shared/models/upload.model";
import {CloudDevice} from "./cloud-device.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {PlatformOsVersion} from "./platform-os-version.model";
import {custom, serializable} from "serializr";
import {Capability} from "../../shared/models/capability.model";
import {TestPlanLabType} from "app/enums/test-plan-lab-type.enum";

export class MirroringData {
  agent: Agent;
  device: AgentDevice;
  recording: boolean;
  upload: Upload;
  uploadId: number;
  workspaceVersionId: number;
  workspaceVersion: WorkspaceVersion;
  app_activity: String;
  app_package: String;
  bundleId: String;
  cloudDevice: CloudDevice;
  os_version: PlatformOsVersion;
  uiId: number;
  uiIdName: String;
  uiIdScreenName: String;
  isManually: Boolean;
  isRecord: boolean;
  testsigmaAgentEnabled: boolean;
  @serializable(custom(v => {
    let arr = [];
    if (v) {
      if (!(v instanceof Array)) {
        v = JSON.parse(v);
      }
      v.forEach(capability => {
        let key = Object.keys(capability);
        if (capability[key[0]]?.length && capability[key[1]]?.length && capability[key[2]]?.length) {
          arr.push(capability);
        }
      });
    }
    return JSON.stringify(arr);
  }, v => {
    let capabilities = [];
    if (typeof v == "string") {
      v = v.replace(/\\"/g, '"');
      v = JSON.parse(v);
      v.forEach(capability => capabilities.push(new Capability().deserialize(capability)));
    } else if (v instanceof Object)
      v.forEach(capability => capabilities.push(new Capability().deserialize(capability)));
    return capabilities;
  }))
  public capabilities: Capability[];
  public testPlanLabType: TestPlanLabType;
  public isStepRecord: Boolean;
  public testCaseId: number;
  public get showFullScreen(): boolean {
    let deviceName = this.cloudDevice ? this.cloudDevice.deviceName : this.device?.name;
    deviceName = deviceName.toLowerCase()
    return (deviceName.includes("tab") || deviceName.includes("ipad") || deviceName.includes("note"));
  }
}
