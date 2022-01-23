import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {custom, deserialize, serializable} from "serializr";
import {KibbutzElementData} from "./kibbutz-element-data.model";
import {KibbutzTestStepTestData} from "./kibbutz-test-step-test-data.model";

export class AddonNaturalTextActionDataModel extends Base implements Deserializable {
  @serializable(custom(v => {
    if (!v)
      return v;
    let returnValue = new Map<string, KibbutzElementData>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new KibbutzElementData().deserialize(v[key]);
    }
    return v;
  }, v => {
    if (!v)
      return v;
    let returnValue = new Map<string, JSON>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new KibbutzElementData().serialize();
    }
    return v;
  }))
  elements: Map<string, KibbutzElementData>;

  @serializable(custom(v => {
    if (!v)
      return v;
    let returnValue = new Map<string, KibbutzTestStepTestData>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new KibbutzTestStepTestData().deserialize(v[key]);
    }
    return v;
  }, v => {
    if (!v)
      return v;
    let returnValue = new Map<string, JSON>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new KibbutzTestStepTestData().serialize();
    }
    return v;
  }))
  testData: Map<string, KibbutzTestStepTestData>;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonNaturalTextActionDataModel, input));
  }

}
