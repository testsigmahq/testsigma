import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable, alias, custom} from "serializr";

export class AzureIssueType implements Deserializable {

  @serializable
  public name: String;
  @serializable
  public url: String;
  @serializable(alias('defaultWorkItemType', custom(v => v, v => v)))
  public defaultWorkItemType: JSON;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(AzureIssueType, input))
  }
}


