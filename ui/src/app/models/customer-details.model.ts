import {deserialize, serializable} from 'serializr';
import {Deserializable} from "../shared/models/deserializable";

export class CustomerDetails implements Deserializable {
  @serializable
  public company: string;
  @serializable
  public email: string;
  @serializable
  public firstName: string;
  @serializable
  public id: string;
  @serializable
  public lastName: string;
  @serializable
  public phone: string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(CustomerDetails, input));
  }

}

