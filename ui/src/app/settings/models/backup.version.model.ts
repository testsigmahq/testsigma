import {deserialize, serializable} from 'serializr';
import {Base} from "../../shared/models/base.model";
import {PageObject} from "../../shared/models/page-object";

export class BackupVersionModel extends Base implements PageObject {
  @serializable
  public isTestCaseEnabled: Boolean = false;
  @serializable
  public isTestStepEnabled: Boolean = false;
  @serializable
  public isRestStepEnabled: Boolean = false;
  @serializable
  public isUploadsEnabled: Boolean = false;
  @serializable
  public isTestCasePriorityEnabled: Boolean = false;
  @serializable
  public isTestCaseTypeEnabled: Boolean = false;
  @serializable
  public isElementEnabled: Boolean = false;
  @serializable
  public isElementScreenNameEnabled: Boolean = false;
  @serializable
  public isTestDataEnabled: Boolean = false;
  @serializable
  public isAttachmentEnabled: Boolean = false;
  @serializable
  public isAgentEnabled: Boolean = false;
  @serializable
  public isRequirementEnabled: Boolean = false;
  @serializable
  public isTestPlanEnabled: Boolean = false;
  @serializable
  public isSuitesEnabled: Boolean = false;
  @serializable
  public isLabelEnabled: Boolean = false;
  @serializable
  public isTestDeviceEnabled: Boolean = false;
  @serializable
  public workspaceVersionId: Number;
  @serializable
  public filterId: Number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(BackupVersionModel, input));
  }
}
