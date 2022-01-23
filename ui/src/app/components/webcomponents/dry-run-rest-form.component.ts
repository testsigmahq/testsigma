import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {WorkspaceVersion} from "../../models/workspace-version.model";

@Component({
  selector: 'app-dry-run-rest-form',
  templateUrl: './dry-run-rest-form.component.html',
  styles: []
})
export class DryRunRestFormComponent implements OnInit {
  @Input('formGroup') restForm: FormGroup;
  @Input('version') version: WorkspaceVersion;
  @Output("closeDialog")closeDryRunDialog = new EventEmitter<any>();

  constructor() {
  }

  get isHybrid() {
    return this.restForm.controls['testPlanLabType'].value == 'Hybrid';
  }

  ngOnInit(): void {
  }

  closeDialogTab(){
    this.closeDryRunDialog.emit();
  }
}
