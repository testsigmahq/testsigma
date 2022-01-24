import {Base} from "../../shared/models/base.model";
import {deserialize, serializable} from 'serializr';

export class PlatformScreenResolution extends Base {
  @serializable
  resolution: String;
  @serializable
  displayResolution: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(PlatformScreenResolution, input));
  }
}
