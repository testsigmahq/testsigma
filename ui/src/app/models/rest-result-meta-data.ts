import {Deserializable} from "../shared/models/deserializable";
import {alias, custom, deserialize, serializable} from "serializr";

export class RestResultMetaData implements Deserializable {

  @serializable(alias('status'))
  public statusCode: String;
  @serializable(custom(v => v, v => v))
  public headers: Map<string, string>;
  @serializable(alias('content'))
  public responseBody: string;
  @serializable
  public headerRuntimeData: any;
  @serializable
  public bodyRuntimeData: any;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(RestResultMetaData, input))
  }

  get isJSONResponse() {
    return this.headers && this.headers['Content-Type'] && this.headers['Content-Type'].indexOf('application/json') > -1;
  }

  get jsonResponse() {
    return JSON.parse(this.responseBody);
  }
}
