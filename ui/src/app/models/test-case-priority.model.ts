import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, deserialize, serializable} from "serializr";

export class TestCasePriority extends Base implements PageObject {

  @serializable
  public displayName: string;
  @serializable
  public name: string;
  @serializable(alias("workspace_id"))
  public workspaceId: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestCasePriority, input));
  }
}
