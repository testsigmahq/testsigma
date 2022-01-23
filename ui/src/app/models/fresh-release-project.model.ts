import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable} from 'serializr';

export class FreshReleaseProject implements Deserializable {

  @serializable
  public id: Number;
  @serializable
  public name: String;
  @serializable
  public key: String;

  // @serializable(alias('issuetypes', list(object(FreshReleaseIssueType))))
  // public issueTypes: FreshReleaseIssueType[];

  deserialize(input: any): this {
    return Object.assign(this, deserialize(FreshReleaseProject, input))
  }
}
