import {custom, deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";

export class ChromeRecorderDependentElement extends Base implements Deserializable {
  @serializable(custom(v => v, v => {
    if(v && typeof v == 'string')
      return JSON.parse(v);
    else if(v)
      return JSON.parse(JSON.stringify(v))
  }))
  public attributes: Map<string, string>;
  @serializable
  public name: String;

  public viewMore: Boolean;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(ChromeRecorderDependentElement, input));
  }
}
