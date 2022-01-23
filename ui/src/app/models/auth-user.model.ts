import {deserialize, serializable} from "serializr";
import {Deserializable} from "../shared/models/deserializable";

export class AuthUser implements Deserializable {
  public id: number;
  @serializable
  public udid: String;
  @serializable
  public email: String;
  @serializable
  public uuid: String;
  @serializable
  public firstName: String;
  @serializable
  public lastName: String;
  @serializable
  public userName: String;
  @serializable
  public roleType: String;
  @serializable
  public jobTitle: String;

  // @ts-ignore
  get name() {
    if ((this.firstName == null) && (this.lastName == null)) {
      return this.userName;
    }
    return `${this.firstName || ''} ${this.lastName || ''}`;
  }

  get domain() {
    return (this.email || '').split('@')[(this.email || '').split('@').length - 1];
  }

  get isActive() {
    return true;
  }

  get isDeleted() {
    return false;
  }

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AuthUser, input));
  }
}
