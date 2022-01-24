import {deserialize, serializable, custom, object} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";

export class Requirement extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public workspaceVersionId: number;
  @serializable
  public requirementName: String;
  @serializable
  public requirementDescription: String;
  @serializable
  public type: String;
  public isSelected: Boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Requirement, input));
  }

  // @ts-ignore
  get name() : String {
    return this.requirementName;
  }
}
