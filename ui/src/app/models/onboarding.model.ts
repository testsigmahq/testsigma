import {serializable, deserialize} from "serializr";
import {Base} from "../shared/models/base.model";
import {RegistrationType} from "../enums/registration-type.enum";
import {RegistrationMedium} from "../enums/registration-medium.enum";

export class Onboarding extends Base {

  @serializable
  public firstName : string;
  @serializable
  public lastName :string;
  @serializable
  public username: string;
  @serializable
  public password:string;
  @serializable
  public email:string;
  @serializable
  public isSendUpdates:boolean;
  @serializable
  public isCommunityAccess:boolean;
  @serializable
  public registrationType : RegistrationType;
  @serializable
  public registrationMedium : RegistrationMedium;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Onboarding, input));
  }
}
