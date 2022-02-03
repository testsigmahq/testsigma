import {deserialize, serializable} from "serializr";
import {MobileOsType} from "../enums/mobile-os-type.enum";
import {PageObject} from "../../shared/models/page-object";
import {Base} from "../../shared/models/base.model";
import {Platform} from "../../enums/platform.enum";

export class AgentDevice extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable
  public uniqueId: String;
  @serializable
  public productModel: String;
  @serializable
  public apiLevel: String;
  @serializable
  public osVersion: String;
  @serializable
  public osName: MobileOsType;
  @serializable
  public abi: String;
  @serializable
  public isEmulator: Boolean;
  @serializable
  public isOnline: Boolean;
  @serializable
  public screenWidth: number;
  @serializable
  public screenHeight: number;
  @serializable
  public provisioned: Boolean;
  public isDisabled: Boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AgentDevice, input));
  }

  inspectorStartAllowed(): boolean {
    return this.isOnline && (this.osName === MobileOsType.ANDROID);
  }

  inspectorStopAllowed(): boolean {
    return this.isOnline == true;
  }

  inspectorDisabled(): boolean {
    return this.isOnline && (this.osName === MobileOsType.IOS);
  }

  get isIOS(): Boolean {
    return this.osName === MobileOsType.IOS;
  }

  get isAndroid(): Boolean {
    return this.osName === MobileOsType.ANDROID;
  }

  static getPlatformFromMobileOStype(osName : MobileOsType){
    if(osName === MobileOsType.ANDROID)
      return Platform.Android
    else
      return Platform.Mac
  }

}
