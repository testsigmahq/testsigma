import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, object, optional, serializable, SKIP} from "serializr";
import {ReportModule} from "./report-module.model";
import {ReportConfiguration} from "./report-configuration.model";


export class Report extends Base implements PageObject {

  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable
  public description: String;
  @serializable
  public reportType: String;
  @serializable(optional(object(ReportModule)))
  public reportModule:ReportModule;
  @serializable(optional(object(ReportConfiguration)))
  public reportConfiguration:ReportConfiguration;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Report, input));
  }
}
