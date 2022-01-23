import {alias, deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";

export class RestStepAuthorizationValue extends Base implements Deserializable {
  @serializable
  public username: String;
  @serializable
  public password: String;
  @serializable(alias('Bearertoken'))
  public token: string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(RestStepAuthorizationValue, input));
  }
}
