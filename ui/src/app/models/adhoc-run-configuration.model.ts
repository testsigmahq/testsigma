import {alias, custom, deserialize, object, optional, serializable} from 'serializr';
import {Capability} from "../shared/models/capability.model";
import {PageObject} from "../shared/models/page-object";
import {Base} from "../shared/models/base.model";
import {WorkspaceType} from "../enums/workspace-type.enum";
import {TestPlanLabType} from "../enums/test-plan-lab-type.enum";
import {Screenshot} from "../enums/screenshot.enum";
import {Platform} from "../enums/platform.enum";
import {Environment} from "./environment.model";
import {TestDevice} from "./test-device.model";
import {ApplicationPathType} from "../enums/application-path-type.enum";

export class AdhocRunConfiguration extends Base implements PageObject {
  @serializable(alias('configName'))
  public name: String;
  @serializable
  public pageTimeOut: number;
  @serializable
  public elementTimeOut: number;
  @serializable(custom(v => v, v => {
    if (v == "1" || v == WorkspaceType.WebApplication)
      return WorkspaceType.WebApplication
    else if (v == "2" || v == WorkspaceType.MobileWeb)
      return WorkspaceType.MobileWeb
    else if (v == "3" || v == WorkspaceType.AndroidNative)
      return WorkspaceType.AndroidNative
    else if (v == "6" || v == WorkspaceType.IOSNative)
      return WorkspaceType.IOSNative
    else if (v == "8" || v == WorkspaceType.Rest)
      return WorkspaceType.Rest
  }))
  public workspaceType: WorkspaceType;
  @serializable(custom(v => {
    switch (v) {
      case TestPlanLabType.TestsigmaLab:
        return 1;
      case TestPlanLabType.Hybrid:
        return 3;
    }
  }, v => {
    if (v == "1" || v == TestPlanLabType.TestsigmaLab)
      return TestPlanLabType.TestsigmaLab
    else if (v == "3" || v == TestPlanLabType.Hybrid)
      return TestPlanLabType.Hybrid
  }))
  public type: TestPlanLabType;
  @serializable
  public browser: String;
  @serializable
  public platformOsVersionId: Number;  // OsVersion
  @serializable
  public platformBrowserVersionId: Number; // BrowserVersion
  @serializable
  public platformScreenResolutionId: Number; // ScreenResolution
  @serializable
  public platformDeviceId: Number;  //  Platform
  @serializable
  public environmentId: string;
  @serializable(custom(v => {
    switch (v) {
      case Screenshot.ALL_TYPES:
        return "1";
      case Screenshot.FAILED_STEPS:
        return "2";
      case Screenshot.NONE:
        return "0";
    }
  }, v => {
    if (v == "1" || v == Screenshot.ALL_TYPES)
      return Screenshot.ALL_TYPES
    else if (v == "2" || v == Screenshot.FAILED_STEPS)
      return Screenshot.FAILED_STEPS
    else if (v == "0" || v == Screenshot.NONE)
      return Screenshot.NONE
  }))
  public captureScreenshots: Screenshot;
  @serializable(custom(v => {
    let arr = [];
    if(v){
      if(!(v instanceof Array)) {
        v = JSON.parse(v);
      }
      v.forEach(capability => {
        let key = Object.keys(capability);
        if(capability[key[0]]?.length && capability[key[1]]?.length && capability[key[2]]?.length){
          arr.push(capability.serialize());
        }
      });
      return JSON.stringify(arr);
    }
  }, v => {
    let capabilities = [];
    if (typeof v == "string") {
      v = v.replace(/\\"/g, '"');
      v = JSON.parse(v);
      v.forEach(capability => capabilities.push(new Capability().deserialize(capability)));
    } else if (v instanceof Object)
      v.forEach(capability => capabilities.push(new Capability().deserialize(capability)));
    return capabilities
  }))
  public desiredCapabilities: Capability[];
  @serializable
  public agentId: Number;
  @serializable
  public deviceId: Number;
  @serializable
  public appPathType: ApplicationPathType;
  @serializable
  public appName: String;
  @serializable
  public udId: String;
  @serializable
  public appUploadId: Number;
  @serializable
  public appPackage: String;
  @serializable
  public appActivity: String;
  @serializable
  public appUrl: String;
  @serializable
  public appBundleId: String;
  @serializable(optional(object(Environment)))
  public environment: Environment;
  public platform : Platform;
  public osVersion : String;
  public browserVersion : String;
  public deviceName : String;
  public resolution : String;
  public executionEnvironment : TestDevice;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(AdhocRunConfiguration, input));
  }

  deserializeDryRunForm(input: JSON): this {
    let convert: JSON = JSON.parse("{}");
    convert['pageTimeOut'] = input['pageTimeOut'];
    convert['elementTimeOut'] = input['elementTimeOut'];
    convert['environmentId'] = input['environmentId'];
    convert['type'] = input['testPlanLabType'];
    convert['browser'] = input['testDevices'][0]['browser'];
    convert['platformOsVersionId'] = input['testDevices'][0]['platformOsVersionId'];
    convert['platformBrowserVersionId'] = input['testDevices'][0]['platformBrowserVersionId'];
    convert['platformScreenResolutionId'] = input['testDevices'][0]['platformScreenResolutionId'];
    convert['platformDeviceId'] = input['testDevices'][0]['platformDeviceId'];
    convert['captureScreenshots'] = input['screenshot'];
    convert['desiredCapabilities'] = input['testDevices'][0]['capabilities'];
    convert['agentId'] = input['testDevices'][0]['agentId'];
    convert['deviceId'] = input['testDevices'][0]['deviceId'];
    convert['appName'] = input['testDevices'][0]['appName'];
    convert['udId'] = input['testDevices'][0]['udid'];
    convert['appUploadId'] = input['testDevices'][0]['appUploadId'];
    convert['appPackage'] = input['testDevices'][0]['appPackage'];
    convert['appActivity'] = input['testDevices'][0]['appActivity'];
    convert['appUrl']   = input['testDevices'][0]['appUrl'];
    convert['appBundleId'] = input['testDevices'][0]['appBundleId'];
    convert['appPathType'] = input['testDevices'][0]['appPathType'];

    return Object.assign(this, deserialize(AdhocRunConfiguration, convert));
  }

  equals(input: JSON) {
    let envSettings = input['testDevices'][0];
    let changed =
      this.pageTimeOut != input['pageTimeOut'] ||
      this.elementTimeOut != input['elementTimeOut'] ||
      this.environmentId != input['environmentId'] ||
      this.type != input['testPlanLabType'] ||
      this.browser != envSettings['browser'] ||
      this.captureScreenshots != input['screenshot'] ||
      Boolean(this.agentId) && this.agentId != envSettings['agentId'] ||
      this.appName != envSettings['appName'] ||
      this.udId != envSettings['udid'] ||
      Boolean(this.appUploadId) && this.appUploadId != envSettings['appUploadId'] ||
      this.appPackage != envSettings['appPackage'] ||
      this.appActivity != envSettings['appActivity'] ||
      this.appUrl != envSettings['appUrl'] ||
      this.appBundleId != envSettings['appBundleId'] ||
      this.appPathType !=envSettings['appPathType'] ||
      this.platformDeviceId != envSettings['platformDeviceId'] ||
      this.platformBrowserVersionId != envSettings['platformBrowserVersionId'] ||
      this.platformScreenResolutionId !=envSettings['platformScreenResolutionId'] ||
      this.platformOsVersionId!=envSettings['platformOsVersionId'];

    this.desiredCapabilities.forEach((cap: Capability) => {
      if(!!cap.value && !!cap.name && !!cap.type)
        if (!envSettings.capabilities?.find((envCap: Capability) => {
            return envCap.type == cap.type && envCap.name == cap.name && envCap.value == cap.value
          }))
          changed = true;
    })
    return !changed;
  }



  formattedName(environment): String {
    let name = environment.platform + " (" + environment.osVersion + ") ";
    if (environment.browser!=null && this.isWeb)
      name += environment.browser + " - " + environment.browserVersion;
    else if (environment.deviceName!=null)
      name += environment.deviceName
    return name;
  }

  formattedHybridName(localHostName,environment): String {
    let name = localHostName + " - ";
    if (environment.browser!=null && this.isWeb)
      name += environment.browser + " - " + environment.browserVersion;
    else if (environment.deviceName!=null)
      name += environment.deviceName
    return name;
  }

  get isHybrid() {
    return this.type === TestPlanLabType.Hybrid;
  }

  get isTestSigmaLab(): boolean {
    return this.type == TestPlanLabType.TestsigmaLab;
  }

  get isIOSNative(): boolean {
    return this.workspaceType == WorkspaceType.IOSNative;
  }

  get isAndroidNative(): boolean {
    return this.workspaceType == WorkspaceType.AndroidNative;
  }

  get isWeb(): boolean{
    return this.workspaceType == WorkspaceType.WebApplication;
  }

  get isMobileNative(): boolean {
    return this.isAndroidNative || this.isIOSNative;
  }
}
