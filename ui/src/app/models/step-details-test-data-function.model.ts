import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {custom, deserialize, serializable, SKIP, alias} from 'serializr';

export class StepDetailsTestDataFunction extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public classPackage: String;
  @serializable
  public className: String;
  @serializable
  public functionName: String;
  @serializable(custom(v => v, v => v))
  public arguments: Map<String, String> ;
  @serializable(custom(v => v, v => v))
  public argumentTypes: Map<String, String> ;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(StepDetailsTestDataFunction, input));
  }
}
