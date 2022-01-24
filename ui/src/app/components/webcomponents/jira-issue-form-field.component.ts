import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {JiraIssueField} from "../../models/jira-issue-field.model";
import {FormGroup} from '@angular/forms';
import {JiraFieldAllowedValue} from "../../models/jira-field-allowed-value.model";

@Component({
  selector: 'app-jira-issue-form-field',
  templateUrl: './jira-issue-form-field.component.html',
  styles: []
})
export class JiraIssueFormFieldComponent implements OnInit {
  @Input() field: JiraIssueField;
  @Input() form: FormGroup;
  @Output('priority') priorityAction = new EventEmitter<any>();
  public selectedPriority: JiraFieldAllowedValue;
  public isShowPriority: boolean = false;

  ngOnInit() {
    if (this.field && this.field?.allowedValues && this.field?.isPriorityField) {
      this.selectedPriority = this.field?.allowedValues[0];
      this.priorityAction.emit(this.selectedPriority);
    }
  }

  get isValid() {
    if(this.field) {
      return this.form.controls[this.field?.key.toString()]?.valid;
    }
  }

  toggleProject(priority) {
    this.selectedPriority = priority;
    this.isShowPriority = false;
    this.priorityAction.emit(priority);
  }

  toggleDropdown(projectShow: string) {
    this[projectShow] = !this[projectShow];
  }
}
