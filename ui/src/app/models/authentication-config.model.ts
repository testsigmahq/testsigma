import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {deserialize, primitive, serializable} from "serializr";
import {AuthenticationType} from "../shared/enums/authentication-type.enum";

export class AuthenticationConfig extends Base implements PageObject{

  @serializable(primitive())
  public id: number;
  @serializable
  public isApiEnabled: boolean;
  @serializable
  public isGoogleAuthEnabled: boolean;
  @serializable
  public httpOnly: boolean;
  @serializable
  public secure: boolean;
  @serializable
  public authenticationType: AuthenticationType;
  @serializable
  public userName: string;
  @serializable
  public password: string;
  @serializable
  public apiKey: string;
  @serializable
  public jwtSecret: string;
  @serializable
  public googleClientId: string;
  @serializable
  public googleClientSecret: string;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(AuthenticationConfig, input))
  }

}
