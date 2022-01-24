/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */
import {Base} from "../../shared/models/base.model";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {WorkspaceType} from "../../enums/workspace-type.enum";
import {ApplicationPathType} from "../../enums/application-path-type.enum";
import { primitive, serializable} from 'serializr';
import {Platform} from "../../enums/platform.enum";

export class SessionCreationRequest extends Base {
  @serializable(primitive())
  mobileSessionId: number;
  @serializable(primitive())
  executionLabType: TestPlanLabType;
  @serializable(primitive())
  platform: Platform;
  @serializable(primitive())
  workspaceType: WorkspaceType;
  @serializable(primitive())
  uniqueId: String;
  @serializable(primitive())
  applicationPathType: ApplicationPathType;
}
