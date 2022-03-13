import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {deserialize, list, optional, primitive, serializable} from "serializr";
import {AddonActionElementData} from "./addon-action-element-data.model";
import {AddonActionTestData} from "./addon-action-test-data.model";

export class AddonActionData extends Base implements Deserializable{

  @serializable(optional(list(primitive())))
  public testData: AddonActionTestData;
  @serializable(optional(list(primitive())))
  public elements: AddonActionElementData;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonActionData, input));
  }
}
