import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, deserialize, serializable} from "serializr";

export class TestPlanTag extends Base implements PageObject {
  @serializable
  public name: String;
  @serializable(alias('count'))
  public usedCount: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestPlanTag, input));
  }
}
