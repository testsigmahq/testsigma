import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable} from "serializr";
import {KibbutzParameterType} from "../enums/kibbutz-parameter-type.enum";

export class KibbutzTestDataFunctionParameter extends Base implements Deserializable{
  @serializable
  public name: string;
  @serializable
  public description: string;
  @serializable
  public reference: string;
  @serializable
  public type: KibbutzParameterType ;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(KibbutzTestDataFunctionParameter, input));
  }
}
