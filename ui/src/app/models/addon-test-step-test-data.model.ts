import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {custom, deserialize, primitive, serializable} from "serializr";
import {TestDataType} from "../enums/test-data-type.enum";

export class AddonTestStepTestData extends Base implements Deserializable {
  @serializable
  value: string;
  @serializable
  type: TestDataType;
  @serializable(custom(v=>v, v=>v))
  testDataFunctionArguments: Map<string, string>;
  @serializable
  testDataFunctionId: number;
  @serializable(custom(v=>v, v=>v))
  isAddonFn: boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonTestStepTestData, input));
  }

}
