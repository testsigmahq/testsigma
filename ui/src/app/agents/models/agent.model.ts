/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "app/shared/models/base.model";
import {PageObject} from "app/shared/models/page-object";
import {alias, custom, deserialize, list, object, optional, serializable, SKIP} from 'serializr';
import {Browser} from "app/agents/models/browser.model";
import * as moment from 'moment';
import {AgentOsType} from "../enums/agent-os-type.enum";
import {Platform} from "../../enums/platform.enum";

export class Agent extends Base implements PageObject {
  @serializable
  public agentVersion: String;
  @serializable(alias('browserList', optional(list(object(Browser)))))
  public browsers: Browser[];
  @serializable
  public currentAgentVersion: String;
  @serializable
  public uniqueId: String;
  @serializable
  public updatedDate: Date;
  @serializable
  public jwtApiKey: String;
  @serializable
  public isLocalAgent: Boolean = false;

  @serializable(alias('created_date', custom(() => SKIP, (v) => v)))
  public createdAt: Date;
  @serializable(alias('updated_date', custom(() => SKIP, (v) => v)))
  public updatedAt: Date;
  @serializable
  public hostName: String;
  @serializable
  public ipAddress: String;
  @serializable
  public osType: AgentOsType;
  @serializable
  public title: String;
  @serializable
  public osVersion: String;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(Agent, input));
  };

  isOnline(): boolean {
    return moment(this.updatedAt) > moment().subtract(10, 'minute');
  };

  isOutOfSync(): boolean {
    if(this.currentAgentVersion.startsWith("v")) {
      this.currentAgentVersion = this.currentAgentVersion.slice(1);
    }
    if(this.currentAgentVersion.endsWith("-m1")) {
      this.agentVersion = this.agentVersion + "-m1";
    }
    return (this.agentVersion != this.currentAgentVersion);
  };

  // @ts-ignore
  get name(): String {
    return this.title;
  }

  get isMac() {
    return this.osType === AgentOsType.MACOSX;
  }

  get isLinux() {
    return this.osType === AgentOsType.LINUX;
  }

  get isWindows() {
    return this.osType === AgentOsType.WINDOWS;
  }

  static getPlatformFromOsType(osType: AgentOsType): Platform {
    switch (osType) {
      case AgentOsType.LINUX:
        return Platform.Linux
      case AgentOsType.MACOSX:
        return Platform.Mac
      case AgentOsType.WINDOWS:
        return Platform.Windows
      case AgentOsType.ANDROID:
        return Platform.Android
      case AgentOsType.IOS:
        return Platform.iOS
    }
  }
}
