import {custom, deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {Integrations} from "../shared/models/integrations.model";

export class TestCaseResultExternalMapping extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public workspaceId: number;
  @serializable
  public testCaseResultId: number;
  @serializable
  public externalId: String;
  @serializable
  public linkToExisting: Boolean;
  @serializable(custom(v => {
    return v;
  }, v => v))
  public fields: Map<String, Object>;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestCaseResultExternalMapping, input));
  }

  public workspace: Integrations;

}
