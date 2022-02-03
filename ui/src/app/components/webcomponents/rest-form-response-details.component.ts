import {Component, Input, OnInit} from '@angular/core';
import {RestStepEntity} from "../../models/rest-step-entity.model";
import {RestCompareType} from "../../enums/rest-compare-type.enum";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-rest-form-response-details',
  templateUrl: './rest-form-response-details.component.html',
  styles: []
})
export class RestFormResponseDetailsComponent implements OnInit {
  @Input('restStep') restStep: RestStepEntity;
  @Input('form') form: FormGroup;
  @Input('formSubmitted') formSubmitted: Boolean;
  public changedStr:any;
  public oldCompareType:any;
  public compareType:any;
  public isInitial:boolean=true;
  constructor() {
  }

  ngOnInit(): void {

    this.oldCompareType = this.restStep.responseCompareType;
    this.compareType = this.restStep.responseCompareType;
    this.addFormControls();
    if(this.restStep.responseCompareType == RestCompareType.JSON_PATH ){
      if(this.isInitial){
        this.isInitial = false;
        this.restStep.response = new RestStepEntity().convertToJSONObject(JSON.parse(this.restStep.response));
      }else{
        this.restStep.response = new RestStepEntity().convertToJSONObject(JSON.parse(this.changedStr))
      }
    }else{
      this.form.controls['response'].setValue(   typeof this.restStep.response =='string'  ? this.restStep.response :
        this.restStep.response? JSON.stringify(this.restStep.response):"");
    }
  }

  get compareTypes() {
    return Object.keys(RestCompareType);
  }

  addFormControls() {
    this.form.addControl('status', new FormControl(this.restStep.status, [Validators.required]));
    this.form.addControl('responseCompareType', new FormControl(this.restStep.responseCompareType, []));
    this.form.addControl('response', new FormControl(this.restStep.response, []));
  }

  get canShowBodyJson() :boolean{
    const compareType:any = this.form.get('responseCompareType')?.value;
    return this.changeHeaders(compareType)
  }
  changeValue(value){
    if(value != RestCompareType.JSON_PATH && this.oldCompareType == RestCompareType.JSON_PATH){
      this.changedStr= JSON.stringify(new RestStepEntity().convertJSONObjectFromString(this.form.get("responseBodyJson")?.value));

    } else{
      this.changedStr = typeof this.form.get("response")?.value == 'string' ? this.form.get("response")?.value
        : JSON.stringify(new RestStepEntity().convertJSONObjectFromString(this.form.get("response")?.value));
    }
    this.compareType = value ? value : this.restStep.responseCompareType ;
  }
  changeHeaders(compareType:any) : boolean{
    try{
    if(this.compareType != this.oldCompareType) {
      if(!this.isInitial){

          if (this.compareType != RestCompareType.JSON_PATH && this.restStep.response) {
            let str: string = JSON.stringify(new RestStepEntity().convertJSONObjectFromStr(this.changedStr? JSON.parse(this.changedStr): JSON.parse("{}")));
            this.form.controls['response'].setValue( str)
            this.oldCompareType = this.compareType;
          } else if (compareType == RestCompareType.JSON_PATH && this.form.get('responseBodyJson')) {
            //let response = new RestStepEntity().convertToJSONArray( JSON.parse(this.changedStr ? this.changedStr : '{}'));
            //this.form.get('responseBodyJson')?.setValue(response);
            this.restStep.response = new RestStepEntity().convertToJSONObject(JSON.parse(this.changedStr))
            this.oldCompareType = this.compareType;
          }
      } else {
        this.isInitial = false;
        this.restStep.response = new RestStepEntity().convertToJSONObject(this.restStep.response? JSON.parse(this.restStep.response): "");
      }
    }
    }catch (e){
      console.log(e+'');
    }
    return this.compareType == RestCompareType.JSON_PATH;
  }

  focusOut(event) {
    event.target.blur();
  }
}
