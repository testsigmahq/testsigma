import {alias, deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";

export class TestSuiteTag extends Base implements PageObject {
  @serializable
  public name: String;
  @serializable(alias('count'))
  public usedCount: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestSuiteTag, input));
  }
}
