import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {alias, deserialize, serializable} from "serializr";

export class TestStepForLoop extends Base implements Deserializable {

  @serializable(alias('for_loop_start_index'))
  public startIndex: number;
  @serializable(alias('for_loop_end_index'))
  public endIndex: number;
  @serializable(alias('for_loop_test_data_id'))
  public testDataId: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestStepForLoop, input))
  }
}
