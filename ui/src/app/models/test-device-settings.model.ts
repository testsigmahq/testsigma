import {alias, custom, deserialize, serializable} from 'serializr';
import {Deserializable} from "../shared/models/deserializable";
import {Base} from "../shared/models/base.model";
import {ApplicationPathType} from "../enums/application-path-type.enum";
import {Platform} from "../enums/platform.enum";
import {Capability} from "../shared/models/capability.model";

/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

export class TestDeviceSettings extends Base implements Deserializable {
  @serializable
  public title: String;
  @serializable(custom(v=> {
    if(v == "WINDOWS")
      return Platform.Windows;
    if(v == "MACOSX")
      return Platform.Mac
    if(v == "LINUX")
      return Platform.Linux
    return v;
  }, v=> {
    return v;
  }))
  public platform: Platform;
  @serializable
  public osVersion: String;
  @serializable(custom(v => {
    if(v && v.toUpperCase() == 'MOZILLAFIREFOX')
      return 'FIREFOX';
    else if(v && v.toUpperCase() == 'MICROSOFTEDGE')
      return 'EDGE';
    return v
  }, v => {
    if(v && v.toUpperCase() == 'FIREFOX')
      return 'MOZILLAFIREFOX';
    else if(v && v.toUpperCase() == 'EDGE')
      return 'MICROSOFTEDGE';
    return v
  }))
  public browser: String;
  @serializable
  public browserVersion: String;
  @serializable
  public resolution: String;
  @serializable
  public deviceName: String;
  @serializable
  public udid: String
  @serializable
  public appId: String;
  @serializable(alias('app_upload_id'))
  public appUploadId: String;
  @serializable
  public appPackage: String;
  @serializable
  public appActivity: String;
  @serializable
  public appUrl: String;
  @serializable
  public appPathType: ApplicationPathType;
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
  public type: number;
  @serializable
  public createSessionAtCaseLevel: boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestDeviceSettings, input));
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

  get formattedOsVersion() {
    if (isNaN(parseInt(this.osVersion + ''))) {
      return this.osVersion;
    } else {
      return 'V. ' + this.osVersion;
    }
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
