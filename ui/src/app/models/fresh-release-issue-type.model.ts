import {Deserializable} from "../shared/models/deserializable";
import {alias, deserialize, serializable} from "serializr";

export class FreshReleaseIssueType implements Deserializable {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable
  public description: String;
  @serializable(alias('type_icon'))
  public icon: String;
  @serializable(alias('label'))
  public displayName: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(FreshReleaseIssueType, input))
  }
}
