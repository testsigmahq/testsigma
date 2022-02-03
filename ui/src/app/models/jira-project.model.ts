import {alias, custom, deserialize, list, object, serializable} from 'serializr';
import {JiraIssueType} from "./jira-issue-type.model";
import {Deserializable} from "../shared/models/deserializable";

export class JiraProject implements Deserializable {
  @serializable
  public id: String;
  @serializable
  public name: String;
  @serializable
  public key: String;
  @serializable(custom(v => v, v => v))
  public avatarUrls: Map<String, URL>;

  @serializable(alias('issuetypes', list(object(JiraIssueType))))
  public issueTypes: JiraIssueType[];

  deserialize(input: any): this {
    return Object.assign(this, deserialize(JiraProject, input));
  }


}
