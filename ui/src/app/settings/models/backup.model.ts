import {alias, custom, deserialize, serializable, SKIP} from 'serializr';
import {Base} from "../../shared/models/base.model";
import {PageObject} from "../../shared/models/page-object";
import {ExportStatus} from "../enums/export-status.enum";

import * as moment from 'moment';
import {WorkspaceVersion} from "../../models/workspace-version.model";

export class Backup extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable
  public message: String;
  @serializable
  public status: ExportStatus;
  @serializable(alias('createdDate', custom(() => SKIP, (v) => {
    if (v)
      return moment(v)
  })))
  public createdAt: Date;

  @serializable(alias('updatedDate', custom(() => SKIP, (v) => {
    if (v)
      return moment(v)
  })))
  public updatedAt: Date;

  @serializable(custom(v=>SKIP, v=>v))
  public workspaceVersion:WorkspaceVersion;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Backup, input));
  }

  get isCompleted() {
    return this.status == ExportStatus.SUCCESS;
  }

  get isInProgress() {
    return this.status == ExportStatus.IN_PROGRESS;
  }

  get isFailed() {
    return this.status == ExportStatus.FAILURE;
  }
}
