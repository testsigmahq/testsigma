import {Component, Input, OnInit} from '@angular/core';
import {RestStepEntity} from "../../models/rest-step-entity.model";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-rest-form-store-details',
  templateUrl: './rest-form-store-details.component.html',
  styles: []
})
export class RestFormStoreDetailsComponent implements OnInit {
  @Input('restStep') restStep: RestStepEntity;
  @Input('form') form: FormGroup;
  @Input('formSubmitted') formSubmitted: Boolean;

  constructor() {
  }

  ngOnInit(): void {
    this.addFormControls();
  }

  addFormControls() {
    this.form.addControl('storeMetadata', new FormControl(this.restStep.storeMetadata, []));
  }

}
