import {deserialize, serializable} from "serializr";
import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";

export class KibbutzActionElementData extends Base implements Deserializable{
  @serializable
  public name: string;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(KibbutzActionElementData, input));
  }
}
