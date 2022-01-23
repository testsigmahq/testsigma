import {Base} from '../shared/models/base.model';
import {alias, custom, deserialize, object, optional, primitive, serializable, SKIP} from 'serializr';
import {Platform} from '../enums/platform.enum';
import {MobileInspectionStatus} from '../enums/mobile-inspection-status.enum';
import {TestPlanLabType} from '../enums/test-plan-lab-type.enum';
import {PageObject} from '../shared/models/page-object';
import {Capability} from "../shared/models/capability.model";
import {ApplicationPathType} from '../enums/application-path-type.enum';

export class MobileInspection extends Base implements PageObject {
  @serializable(primitive())
  id: number;
  @serializable(primitive())
  platform: Platform;
  @serializable(primitive())
  agentDeviceId: number;
  @serializable(primitive())
  status: MobileInspectionStatus;
  @serializable(primitive())
  labType: TestPlanLabType;
  @serializable(primitive())
  platformDeviceId: number;
  @serializable(primitive())
  appActivity: String;
  @serializable(primitive())
  bundleId: String;
  @serializable(primitive())
  appUploadId: number;
  @serializable(primitive())
  sessionId: string;
  @serializable(primitive())
  applicationPackage: String;
  @serializable(primitive())
  applicationPathType: ApplicationPathType;
  @serializable(alias('STARTED_AT', custom(() => SKIP, (v) => v)))
  public startedAt: Date;
  @serializable(alias('FINISHED_AT', custom(() => SKIP, (v) => v)))
  public finishedAt: Date;
  @serializable(alias('LAST_ACTIVE_AT', custom(() => SKIP, (v) => v)))
  public lastActiveAt: Date;
  @serializable(alias('created_date', custom(() => SKIP, (v) => v)))
  public createdDate: Date;
  @serializable(alias('updated_date', custom(() => SKIP, (v) => v)))
  public updatedDate: Date;

  @serializable(custom(v => {
    let arr = [];
    if(v){
      if(!(v instanceof Array)) {
        v = JSON.parse(v);
      }
      v.forEach(capability => {
        let key = Object.keys(capability);
        if(capability[key[0]]?.length && capability[key[1]]?.length && capability[key[2]]?.length){
          arr.push(capability);
        }
      });
    }
    return arr;
  }, v => {
    let capabilities = [];
    if(typeof v == "string") {
      v = v.replace(/\\"/g, '"');
      v = JSON.parse(v);
      v.forEach(capability => capabilities.push(new Capability().deserialize(capability)));
    } else if (v instanceof  Object)
      v.forEach(capability => capabilities.push(new Capability().deserialize(capability)));
    return capabilities;
  }))
  public capabilities: Capability[];

  deserialize(input: any): this {
    return Object.assign(this, deserialize(MobileInspection, input));
  }
}
