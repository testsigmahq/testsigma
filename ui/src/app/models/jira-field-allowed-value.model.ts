import {custom, deserialize, serializable} from 'serializr';
import {Deserializable} from "../shared/models/deserializable";

export class JiraFieldAllowedValue implements Deserializable {
  @serializable
  public id: String;
  @serializable
  public name: String;
  @serializable
  public value: String;
  @serializable
  public iconUrl: URL;
  @serializable
  public description: String;
  @serializable(custom(v => v, v => v))
  public avatarUrls: Map<String, URL>;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(JiraFieldAllowedValue, input));
  }


}
