import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable} from "serializr";
import {Element} from "./element.model";

export class KibbutzElementData extends Base implements Deserializable {
  @serializable
  name: string;

  public isElementChanged: Boolean;
  public element: Element;

  public showInfo = false;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(KibbutzElementData, input));
  }

}
