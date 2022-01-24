import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable} from 'serializr';

export class AzureProject implements Deserializable {

  @serializable
  public id: String;
  @serializable
  public name: String;
  @serializable
  public url: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AzureProject, input))
  }
}
