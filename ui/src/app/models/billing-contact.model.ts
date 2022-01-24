import {deserialize, serializable} from 'serializr';
import {Deserializable} from "../shared/models/deserializable";

export class BillingContact implements Deserializable {
  @serializable
  public id: string;
  @serializable
  public email: string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(BillingContact, input));
  }

}

