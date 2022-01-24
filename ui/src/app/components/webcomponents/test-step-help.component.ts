import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {Page} from "../../shared/models/page";
import {TestCase} from "../../models/test-case.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestStepType} from "../../enums/test-step-type.enum";

@Component({
  selector: 'app-test-step-help',
  templateUrl: './test-step-help.component.html',
  styles: []
})
export class TestStepHelpComponent implements OnInit {
  @Input('templates') templates: Page<NaturalTextActions>;
  @Input('testcase') testcase: TestCase;
  @Input('version') version: WorkspaceVersion;
  @Input('stepType') stepType: string;
  @Output('onClose') onClose = new EventEmitter<void>();
  @Output('onSelectTemplate') onSelectTemplate = new EventEmitter<NaturalTextActions>();
  public activeTab: string;

  constructor() {
  }

  ngOnInit(): void {

  }

  ngOnChanges() {
    this.activeTab = !this.isActionText ? 'help' : 'example';
  }

  get isActionText() {
    return this.stepType == TestStepType.ACTION_TEXT;
  }

  helpClose() {
    this.onClose.emit();
  }

  SelectTemplate(template: NaturalTextActions) {
    this.onSelectTemplate.emit(template)
  }

}
