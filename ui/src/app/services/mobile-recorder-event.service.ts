import {EventEmitter, Injectable} from '@angular/core';
import {TestStep} from "../models/test-step.model";

@Injectable({
  providedIn: 'root'
})
export class MobileRecorderEventService {
  public createStepFromRecorder: EventEmitter<TestStep> = new EventEmitter();
  public emitStepRecord(testStep: TestStep) {
    this.createStepFromRecorder.emit(testStep);
  }
  public getStepRecorderEmitter() {
    return this.createStepFromRecorder;
  }
}
