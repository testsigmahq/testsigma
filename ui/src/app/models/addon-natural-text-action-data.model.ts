import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {custom, deserialize, serializable} from "serializr";
import {AddonElementData} from "./addon-element-data.model";
import {AddonTestStepTestData} from "./addon-test-step-test-data.model";

export class AddonNaturalTextActionDataModel extends Base implements Deserializable {
  @serializable(custom(v => {
    if (!v)
      return v;
    let returnValue = new Map<string, AddonElementData>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new AddonElementData().deserialize(v[key]);
    }
    return v;
  }, v => {
    if (!v)
      return v;
    let returnValue = new Map<string, JSON>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new AddonElementData().serialize();
    }
    return v;
  }))
  elements: Map<string, AddonElementData>;

  @serializable(custom(v => {
    if (!v)
      return v;
    let returnValue = new Map<string, AddonTestStepTestData>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new AddonTestStepTestData().deserialize(v[key]);
    }
    return v;
  }, v => {
    if (!v)
      return v;
    let returnValue = new Map<string, JSON>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new AddonTestStepTestData().serialize();
    }
    return v;
  }))
  testData: Map<string, AddonTestStepTestData>;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonNaturalTextActionDataModel, input));
  }

}
