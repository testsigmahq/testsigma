import {Base} from "../../shared/models/base.model";
import {deserialize, serializable} from 'serializr';
import {Platform} from "../../enums/platform.enum";

export class PlatformOsVersion extends Base {
  @serializable
  name: String;
  @serializable
  version: String;
  @serializable
  displayName: String;
  @serializable
  platform : Platform

  deserialize(input: any): this {
    return Object.assign(this, deserialize(PlatformOsVersion, input));
  }
}
