import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, serializable} from 'serializr';

export class CustomStep extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public lib: number;
  @serializable(custom(v => v, v => v))
  public args: JSON;
  @serializable(alias('args_types', custom(v => v, v => v)))
  public argsTypes: JSON;
  @serializable
  public type: number;
  @serializable
  public class: string;
  @serializable
  public package: string;
  @serializable
  public function: string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(CustomStep, input));
  }
}
