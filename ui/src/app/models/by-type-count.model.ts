import {deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";

export class ByTypeCount extends Base implements PageObject {
  @serializable
  public type: number;
  @serializable
  public count: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ByTypeCount, input));
  }

}
