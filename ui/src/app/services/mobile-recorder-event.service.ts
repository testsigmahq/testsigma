import {EventEmitter, Injectable} from '@angular/core';
import {TestStep} from "../models/test-step.model";
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MobileRecorderEventService {
  public createStepFromRecorder: EventEmitter<TestStep> = new EventEmitter();
  public suggestionContent: Subject<any> = new Subject<any>();
  public editElementPopup: string = 'editElement';
  public stepSummary: string = 'showDetails';
  public stepMoreAction: string = 'showMoreAction';
  public suggestionCustomFunction: string = 'suggestionCustomFunction';
  public suggestionDataProfile: string = 'suggestionDataProfile';
  public suggestionEnvironment: string = 'suggestionEnvironment';
  public suggestionElement: string = 'suggestionElement';
  public currentlyTargetElement;
  public returnData: Subject<any> = new Subject<any>();
  public isLandscapeMode: Boolean;
  public suggestionRuntimeVariable: string = 'suggestionRuntimeVariable';

  public emitStepRecord(testStep: TestStep) {
    this.createStepFromRecorder.emit(testStep);
  }
  public getStepRecorderEmitter() {
    return this.createStepFromRecorder;
  }

  public setEmptyAction() {
    this.suggestionContent.next("")
  }
}
