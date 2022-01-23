import {deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";

export class TestCaseCoverageSummary extends Base implements PageObject {
  @serializable
  public automatedCount: number;
  @serializable
  public manualCount: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestCaseCoverageSummary, input));
  }

  get automationPendingCount() {
    return this.automatedCount;
  }
}
