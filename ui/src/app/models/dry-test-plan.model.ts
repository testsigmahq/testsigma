import {deserialize, list, object, optional, serializable, alias} from 'serializr';
import {PageObject} from "../shared/models/page-object";
import {TestPlan} from "./test-plan.model";
import {DryTestDevice} from "./dry-test-device.model";

export class DryTestPlan extends TestPlan implements PageObject {
  @serializable
  public testCaseId: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(DryTestPlan, input));
  }

}
