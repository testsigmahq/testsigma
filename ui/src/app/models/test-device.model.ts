import {deserialize, object, serializable, SKIP, custom, alias} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {TestDeviceSettings} from "./test-device-settings.model";
import {Agent} from "../agents/models/agent.model";
import {TestSuite} from "./test-suite.model";
import {ApplicationPathType} from "../enums/application-path-type.enum";
import {Capability} from "../shared/models/capability.model";
import {Platform} from "../enums/platform.enum";

export class TestDevice extends Base implements PageObject {
  @serializable
  public disable: Boolean;
  @serializable
  public testPlanId: number;
  @serializable
  public id: number;

  @serializable(object(TestDeviceSettings))
  public settings: TestDeviceSettings;

  @serializable
  public browser : String;

  @serializable
  public platformOsVersionId: Number;  // OsVersion
  @serializable
  public platformBrowserVersionId: Number; // BrowserVersion
  @serializable
  public platformScreenResolutionId: Number; // ScreenResolution
  @serializable
  public platformDeviceId: Number;  //  Platform
  @serializable
  public udid: String
  @serializable
  public appUploadId: Number;
  @serializable
  public appPackage: String;
  @serializable
  public appActivity: String;
  @serializable
  public appUrl: String;
  @serializable
  public appPathType: ApplicationPathType;
  @serializable
  public appBundleId: String;
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
    }
    return JSON.stringify(arr);
  }, v => {
    let capabilities = [];
    if(typeof v == "string") {
      if(/\\\\"/g.test(v)){
        v = v.replace(/\\"/g, '"');
        v = v.replace(/\\"/g, '"');
      } else if(v.startsWith("[{\\\"")) {
        v = v.replace(/\\"/g, '"');
      }
      v = JSON.parse(v);
      v.forEach(capability => capabilities.push(new Capability().deserialize(capability)));
    } else if(v instanceof  Object)
      v.forEach(capability => capabilities.push(new Capability().deserialize(capability)));
    return capabilities
  }))
  public capabilities: Capability[];

  @serializable
  public agentId: Number;
  @serializable
  public deviceId: Number;
  @serializable
  public title: String;
  @serializable
  public matchBrowserVersion: boolean;
  @serializable(custom(v => {
    if(v == null)
      return false;
    return v;
  }, v => {
    if(v == null)
      return false;
    return v;
  }))
  public createSessionAtCaseLevel: Boolean;
  public agent: Agent;
  public testSuites: TestSuite[];

  @serializable(custom(v => v, v => {
    if(v && v instanceof Object)
      return v.filter(res => res)
  }))
  public suiteIds: number[];

  public platform : Platform;
  public osVersion : String;
  public browserVersion : String;
  public deviceName : String;
  public resolution : String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestDevice, input));
  }

  get formattedBrowserVersion() {
    if (isNaN(parseInt(this.browserVersion + ''))) {
      return this.browserVersion;
    } else {
      return 'V. ' + this.browserVersion;
    }
  }

  get formattedOsVersion() {
    if (isNaN(parseInt(this.osVersion + ''))) {
      return this.osVersion;
    } else {
      return 'V. ' + this.osVersion;
    }
  }

  get isWindows() {
    return this.platform == Platform.Windows;
  }

  get isMac() {
    return this.platform == Platform.Mac;
  }

  get isIOS() {
    return this.platform == Platform.iOS;
  }

  get isLinux() {
    return this.platform == Platform.Linux;
  }

  get isAndroid() {
    return this.platform == Platform.Android;
  }

  get isChrome() {
    return this.browser?.toUpperCase() == 'GOOGLECHROME' || this.browser?.toUpperCase() == 'CHROME';
  }

  get isFirefox() {
    return this.browser?.toUpperCase() == 'FIREFOX' || this.browser?.toUpperCase() == 'MOZILLAFIREFOX';
  }

  get isSafari() {
    return this.browser?.toUpperCase() == 'SAFARI';
  }

  get isEdge() {
    return this.browser?.toUpperCase() == 'EDGE' || this.browser?.toUpperCase() == 'MICROSOFTEDGE';
  }

  get isAppUploadType() {
    return this.appPathType == ApplicationPathType.UPLOADS;
  }

  get isAppDetailsType() {
    return this.appPathType == ApplicationPathType.APP_DETAILS;
  }

  get isAppPathType() {
    return this.appPathType == ApplicationPathType.USE_PATH;
  }

}
