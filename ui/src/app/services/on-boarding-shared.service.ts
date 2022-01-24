import {EventEmitter, Injectable} from '@angular/core';
import {OnBoarding} from "../enums/onboarding.enum";

@Injectable({
  providedIn: 'root'
})
export class OnBoardingSharedService {

  public onEventComplete: EventEmitter<OnBoarding> = new EventEmitter();

  constructor() {}

  public emitCompleteEvent(preferenceName: OnBoarding) {
    this.onEventComplete.emit(preferenceName);
  }

  public getPreferencesEmitter() {
    return this.onEventComplete;
  }
}
