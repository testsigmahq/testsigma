import {Base} from "../shared/models/base.model";
import {deserialize, serializable} from "serializr";

export class ActivateUser extends Base{
  @serializable
  public firstName:String;
  @serializable
  public lastName:String;
  @serializable
  public password:String;
  @serializable
  public passwordConfirmation:String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ActivateUser, input));
  }
}
