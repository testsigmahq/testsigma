import {Base} from "./base.model";
import {PageObject} from "./page-object";
import {deserialize, serializable, optional, object} from 'serializr';
import {Integration} from "../enums/integration.enum";

export class IntegrationMetaData extends Base implements PageObject {
  @serializable
  public channel: String;
  @serializable
  public user_name: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(IntegrationMetaData, input));
  }

}
