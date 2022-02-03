import {alias, deserialize, serializable} from "serializr";
import {PageObject} from "../shared/models/page-object";
import {Base} from "../shared/models/base.model";

export class TestCaseType extends Base implements PageObject {
  @serializable
  public displayName: string;
  @serializable
  public name: string;
  @serializable(alias("workspace_id"))
  public workspaceId: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestCaseType, input));
  }
}
