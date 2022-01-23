/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "../shared/models/base.model";
import {ResultConstant} from "../enums/result-constant.enum";
import {serializable} from 'serializr';
import {StatusConstant} from "../enums/status-constant.enum";

export class ResultBase extends Base {

  @serializable
  public startTime: Date;
  @serializable
  public endTime: Date;
  @serializable
  public duration: number;
  @serializable
  public result: ResultConstant;
  @serializable
  public status: StatusConstant;
  @serializable
  public started: boolean;
  @serializable
  public isVisuallyPassed: boolean;

  @serializable
  public totalCount: number;
  @serializable
  public passedCount: number;
  @serializable
  public failedCount: number;
  @serializable
  public abortedCount: number;
  @serializable
  public stoppedCount: number;
  @serializable
  public notExecutedCount: number;
  @serializable
  public queuedCount: number;

  get failedPercentage(): Number {
    return Math.round(((this.failedCount || 0) / (this.totalCount || 1)) * 100);
  }

  get abortedPercentage(): Number {
    return Math.round(((this.abortedCount || 0) / (this.totalCount || 1)) * 100);
  }

  get notExecutedPercentage(): Number {
    return Math.round(((this.notExecutedCount || 0) / (this.totalCount || 1)) * 100);
  }

  get stoppedPercentage(): Number {
    if (this.totalCount == 0 && this.isStopped) {
      return 100;
    }
    return Math.round(((this.stoppedCount || 0) / (this.totalCount || 1)) * 100);
  }

  get passedPercentage(): Number {
    return Math.round(((this.passedCount || 0) / (this.totalCount || 1)) * 100);
  }

  get queuedPercentage(): Number {
    if (this.totalCount == 0 && this.isQueued) {
      return 100;
    }
    return Math.round(((this.queuedCount || 0) / (this.totalCount || 1)) * 100);
  }

  get totalCountValue(): Number {
    return this.totalCount;
  }

  get isStopped(): Boolean {
    return this.result == ResultConstant.STOPPED;
  }

  get isPassed(): Boolean {
    return this.result == ResultConstant.SUCCESS;
  }

  get isFailed(): Boolean {
    return this.result == ResultConstant.FAILURE;
  }

  get isAborted(): Boolean {
    return this.result == ResultConstant.ABORTED;
  }

  get isNotExecuted(): Boolean {
    return this.result == ResultConstant.NOT_EXECUTED;
  }

  get isQueued(): Boolean {
    return (this.result == ResultConstant.QUEUED);
  }

  get isRunning(): Boolean {
    return this.status == StatusConstant.STATUS_IN_PROGRESS;
  }

  get isExecuting(): Boolean {
    return (this.status == StatusConstant.STATUS_IN_PROGRESS) || this.result == ResultConstant.QUEUED;
  }

}
