import {deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {TestCaseStatus} from "../enums/test-case-status.enum";

export class ByStatusCount extends Base implements PageObject {
  @serializable
  public status: TestCaseStatus;
  @serializable
  public count: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ByStatusCount, input));
  }

}
