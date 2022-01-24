import {alias, custom, deserialize, serializable, SKIP} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";

export class TestStepTestDataFunction extends Base implements PageObject {
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
  @serializable(alias('binary_file_url', custom(v => v, v => v)))
  public binaryFileUrl: string;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestStepTestDataFunction, input));
  }
}
