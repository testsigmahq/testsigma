import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, FormArray } from '@angular/forms';
import {Capability} from "../../models/capability.model";

@Component({
  selector: 'app-desired-capabilities',
  templateUrl: './desired-capabilities.component.html',
  styles: [
  ]
})
export class DesiredCapabilitiesComponent implements OnInit {
  @Input('formGroup') dryExecutionForm: FormGroup;
  @Input('capabilities') capabilities?: Capability[];
  public showCapabilities: Boolean = false;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit(): void {
  }

  ngOnChanges() {
    if(this.dryExecutionForm.controls['capabilities'])
    this.dryExecutionForm.removeControl('capabilities')
    this.dryExecutionForm.addControl('capabilities', this.formBuilder.array([]));
    this.capabilities?.forEach(capability => {
      this.capabilitiesControls().push(this.addCapabilityFormGroup(capability));
    })
    if(!this.capabilities?.length)
      this.capabilitiesControls().push(this.addCapabilityFormGroup());
  }

  addCapabilityFormGroup(capability?: Capability) {
    return this.formBuilder.group({
      name: capability?.name || '',
      dataType: capability?.type || 'java.lang.String',
      value: capability?.value || ''
    })
  }

  capabilitiesControls(): FormArray {
    return this.dryExecutionForm.get("capabilities") as FormArray;
  }

  remove(i: number) {
    this.capabilitiesControls().removeAt(i);
  }

  andEmptyRowIfMissing() {
    let empty = this.dryExecutionForm.value["capabilities"].find(capability => capability.value == '' && capability.name == '');
    if (!empty)
      this.capabilitiesControls().push(this.addCapabilityFormGroup());
  }

}
