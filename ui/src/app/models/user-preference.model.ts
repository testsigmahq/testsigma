/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {alias, custom, deserialize, serializable, SKIP} from 'serializr';
import {Workspace} from "./workspace.model";
import {WorkspaceVersion} from "./workspace-version.model";
import * as moment from "moment";

export class UserPreference extends Base implements Deserializable {
  @serializable
  public userId: Number;

  @serializable
  public testCaseFilterId: Number;


  @serializable(alias('projectId', custom(v => SKIP, v => {
    if (v != 'undefined')
      return v
  })))
  public projectId: Number;
  @serializable(alias('versionId', custom(v => SKIP, v => {
    if (v != 'undefined')
      return v
  })))
  public versionId: Number;
  @serializable(alias('workspaceId', custom(v => SKIP, v => {
    if (v != 'undefined')
      return v
  })))
  public workspaceId: Number;


  public selectedWorkspace: Workspace;
  public selectedVersion: WorkspaceVersion;
  @serializable(alias('createdDate', custom(() => SKIP, (v) => {
    if (v)
      return moment(v)
  })))
  public createdDate:Date;

  @serializable
  public showedGitHubStar : boolean  = false;

  @serializable
  public clickedSkipForNow : number = 0;

  @serializable(alias('workspaceId', custom(v => v, v => SKIP)))
  get selectedworkspaceId(): String {
    return this.selectedWorkspace ? this.selectedWorkspace.id.toString() : this.workspaceId?.toString();
  }

  @serializable(alias('versionId', custom(v => v, v => SKIP)))
  get selectedVersionId(): String {
    return this.selectedVersion ? this.selectedVersion.id.toString() : this.versionId?.toString();
  }


  deserialize(input: any): this {
    return Object.assign(this, deserialize(UserPreference, input));
  }
}
