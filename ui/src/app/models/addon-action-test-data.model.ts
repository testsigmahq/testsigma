import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {alias, custom, deserialize, optional, serializable} from "serializr";
import {TestDataType} from "../enums/test-data-type.enum";
import {TestStepTestDataFunction} from "./test-step-test-data-function.model";

export class AddonActionTestData extends Base implements Deserializable{
  @serializable
  public value: string;
  @serializable
  public type: TestDataType;
  @serializable(alias('test-data-function', optional(custom(v => {
    if (!v)
      return v;
    return v.serialize();
  }, v => {
    return new TestStepTestDataFunction().deserialize(v)
  }))))
  public testDataFunction: TestStepTestDataFunction;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonActionTestData, input));
  }
}
