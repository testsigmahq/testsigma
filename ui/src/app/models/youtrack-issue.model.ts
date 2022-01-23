import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable, alias, custom} from "serializr";

export class YoutrackIssue implements Deserializable {

  @serializable
  public summary: String;
  @serializable
  public idReadable: String;
  @serializable
  public description: String;
  @serializable
  public id: String;
  @serializable
  public $type: String;



  deserialize(input: any): this {
    return Object.assign(this, deserialize(YoutrackIssue, input))
  }
}


