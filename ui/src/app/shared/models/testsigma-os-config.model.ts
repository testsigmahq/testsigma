import {Base} from "./base.model";
import {alias, deserialize, serializable} from 'serializr';
import {RegistrationType} from "../../enums/registration-type.enum";

export class TestsigmaOSConfig extends Base {
  @serializable
  public url: URL;
  @serializable(alias('user_name'))
  public userName: String;
  @serializable(alias('access_key'))
  public accessKey: String;
  
  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestsigmaOSConfig, input));
  }

  get isEnabled(){
    return !!this.accessKey;
  }
}
