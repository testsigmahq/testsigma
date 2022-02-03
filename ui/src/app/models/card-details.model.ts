import {deserialize, serializable} from 'serializr';
import {Deserializable} from "../shared/models/deserializable";

export class CardDetails implements Deserializable {
  @serializable
  public addressLine1: string;
  @serializable
  public addressLine2: string;
  @serializable
  public cardNumber: string;
  @serializable
  public city: string;
  @serializable
  public country: string;
  @serializable
  public cvv: string;
  @serializable
  public expiryMonth: string;
  @serializable
  public expiryYear: string;
  @serializable
  public firstName: string;
  @serializable
  public lastName: string;
  @serializable
  public state: string;
  @serializable
  public zip: string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(CardDetails, input));
  }

}

