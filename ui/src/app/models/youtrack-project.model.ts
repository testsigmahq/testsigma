import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable,alias,custom} from 'serializr';

export class YoutrackProject implements Deserializable {

  @serializable
  public id: String;
  @serializable
  public name: String;
  @serializable
  public shortName: String;

  @serializable(alias('$type', custom(v => v, v => v)))
  public type:any;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(YoutrackProject, input))
  }
}
