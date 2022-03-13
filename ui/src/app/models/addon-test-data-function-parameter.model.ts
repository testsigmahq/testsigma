import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable} from "serializr";
import {AddonParameterType} from "../enums/addon-parameter-type.enum";

export class AddonTestDataFunctionParameter extends Base implements Deserializable{
  @serializable
  public name: string;
  @serializable
  public description: string;
  @serializable
  public reference: string;
  @serializable
  public type: AddonParameterType ;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonTestDataFunctionParameter, input));
  }
}
