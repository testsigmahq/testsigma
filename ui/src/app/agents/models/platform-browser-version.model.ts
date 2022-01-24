import {Base} from "../../shared/models/base.model";
import {deserialize, serializable} from 'serializr';

export class PlatformBrowserVersion extends Base {
  @serializable
  name: String;
  @serializable
  version: String;
  @serializable
  displayVersion: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(PlatformBrowserVersion, input));
  }
}
