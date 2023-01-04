import { Injectable } from '@angular/core';
import {Observable, Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RunButtonDisableService {
  private enableStepdata: Subject<any> = new Subject<any>();
  enableStepdata$: Observable<any> = this.enableStepdata.asObservable();

  constructor() { }

  setEnableStepData(updatedData) {
    this.enableStepdata.next(updatedData);
  }


}
