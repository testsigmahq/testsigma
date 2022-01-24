import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {deserialize, list, optional, primitive, serializable} from "serializr";
import {KibbutzActionElementData} from "./kibbutz-action-element-data.model";
import {KibbutzActionTestData} from "./kibbutz-action-test-data.model";

export class KibbutzActionData extends Base implements Deserializable{

  @serializable(optional(list(primitive())))
  public testData: KibbutzActionTestData;
  @serializable(optional(list(primitive())))
  public elements: KibbutzActionElementData;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(KibbutzActionData, input));
  }
}
