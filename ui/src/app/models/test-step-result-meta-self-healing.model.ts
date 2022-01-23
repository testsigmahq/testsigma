import {Deserializable} from "../shared/models/deserializable";
import {alias, custom, deserialize, list, primitive, serializable, SKIP} from 'serializr';
import {TestStepResultMetadata} from "./test-step-result-metadata.model";
import {Base} from "../shared/models/base.model";

export class TestStepResultMetaSelfHealing extends Base implements Deserializable {

  @serializable((custom(v => v, v => v)))
  public priorityXpaths: String[];
  @serializable
  public previousXpath: String;
  @serializable((custom(v => v, v => v)))
  public possibleXpaths: String[];
  @serializable
  public updatedXpath: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestStepResultMetaSelfHealing, input))
  }
}
