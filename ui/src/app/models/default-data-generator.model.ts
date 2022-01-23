import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {custom, deserialize, serializable, SKIP, alias} from 'serializr';

export class DefaultDataGenerator extends Base implements PageObject {
  @serializable(alias('name'))
  public displayName: string;
  @serializable
  public functionName: string;
  @serializable
  public className: string;
  @serializable
  public classDisplayName: string;
  @serializable
  public classPackage: string;
  @serializable
  public description: string;
  @serializable(custom(v => SKIP, v => v))
  public arguments: JSON;
  @serializable
  public fileId: number;

  public methodNames: string[];
  public showDetails: Boolean;
  public selected: Boolean;
  public showInfo = false;

  // @ts-ignore
  get name() : string {
    return this.functionName;
  }

  deserialize(input: any): this {
    return Object.assign(this, deserialize(DefaultDataGenerator, input));
  }
}
