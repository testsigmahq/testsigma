import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, object, optional, serializable, SKIP} from "serializr";
import {ReportModule} from "./report-module.model";


export class ReportConfiguration extends Base{

  @serializable
  public id: number;
  @serializable
  public chartType: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ReportConfiguration, input));
  }
}
