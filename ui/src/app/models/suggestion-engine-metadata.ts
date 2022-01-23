import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {alias, custom, deserialize, object, serializable, SKIP} from "serializr";

export class SuggestionEngineMetadata extends Base implements Deserializable {
  @serializable
  public tagName: string;
  @serializable
  public tagCount: string;
  @serializable(custom(v => SKIP, v => v))
  public suggestions: JSON;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(SuggestionEngineMetadata, input));
  }
}
