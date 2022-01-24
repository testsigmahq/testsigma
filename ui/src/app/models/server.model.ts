import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {deserialize, serializable} from "serializr";

export class Server extends Base implements PageObject{

  @serializable
  public id: number;
  @serializable
  public serverUuid: string;
  @serializable
  public consent:boolean;
  @serializable
  public consentRequestDone:boolean;
  @serializable
  public onboarded:boolean;



  deserialize(input: any): this {
    return Object.assign(this, deserialize(Server, input))
  }

}
