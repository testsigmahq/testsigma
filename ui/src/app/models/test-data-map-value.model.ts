import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {alias, custom, deserialize, optional, serializable, SKIP} from 'serializr';
import {AddonTestStepTestData} from "./addon-test-step-test-data.model";
import {TestStepTestDataFunction} from "./test-step-test-data-function.model";
import {TestDataType} from "../enums/test-data-type.enum";
import {Optional} from "@angular/core";

export class TestDataMapValue extends Base implements Deserializable {
  @serializable
  public  type: String;
  @serializable(custom(v=> (v && v.trim()), v=> v))
  public value: String;
  @serializable(optional(custom(v => {
    if (!v)
      return v;
    return v.serialize();
  }, v => {
    return new TestStepTestDataFunction().deserialize(v)
  })))
  private testDataFunction: TestStepTestDataFunction;
  @serializable(optional(custom(v => {
    if (!v)
      return v;
    return v.serialize();
  }, v => {
    return new AddonTestStepTestData().deserialize(v)
  })))
  private addonTDF: AddonTestStepTestData;
  public parameterNameValue: string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestDataMapValue, input));
  }
}
