import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {custom, deserialize, serializable} from "serializr";
import {AddonParameterType} from "../enums/addon-parameter-type.enum";

export class AddonNaturalTextActionParameter extends Base implements Deserializable{
  @serializable
  public name: string;
  @serializable
  public description: string;
  @serializable
  public reference: string;
  @serializable
  public type: AddonParameterType;
  @serializable(custom(v => v, v => v))
  public allowedValues: any[];


  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonNaturalTextActionParameter, input));
  }

  get isElement() {
    return this.type == AddonParameterType.ELEMENT
  }

  get isTestData() {
    return this.type == AddonParameterType.TEST_DATA
  }

  get isTestDataProfile(){
    return this.type == AddonParameterType.TEST_DATA_PROFILE;
  }

  get isEnvironmentData(){
    return this.type == AddonParameterType.ENVIRONMENT_DATA;
  }

  get isTestDataSet(){
    return this.type == AddonParameterType.TEST_DATA_SET;
  }
}
