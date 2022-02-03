import {deserialize} from 'serializr';
import {PageObject} from "../shared/models/page-object";
import {TestDevice} from "./test-device.model";

export class DryTestDevice extends TestDevice implements PageObject {

  deserialize(input: any): this {
    return Object.assign(this, deserialize(DryTestDevice, input));
  }

}
