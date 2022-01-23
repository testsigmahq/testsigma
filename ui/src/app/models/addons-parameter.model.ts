import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {custom, deserialize, serializable} from "serializr";
import {KibbutzParameterType} from "../enums/kibbutz-parameter-type.enum";

export class AddonNaturalTextActionParameter extends Base implements Deserializable{
  @serializable
  public name: string;
  @serializable
  public description: string;
  @serializable
  public reference: string;
  @serializable
  public type: KibbutzParameterType;
  @serializable(custom(v => v, v => v))
  public allowedValues: any[];


  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonNaturalTextActionParameter, input));
  }

  get isElement() {
    return this.type == KibbutzParameterType.ELEMENT
  }

  get isTestData() {
    return this.type == KibbutzParameterType.TEST_DATA
  }
}
