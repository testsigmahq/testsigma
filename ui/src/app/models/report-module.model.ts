import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, object, optional, serializable, SKIP} from "serializr";


export class ReportModule extends Base {

  @serializable
  public id: number;
  @serializable
  public moduleName: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ReportModule, input));
  }
}
