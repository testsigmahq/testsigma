import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TestCaseResultExternalMapping} from '../../models/test-case-result-external-mapping.model';
import {Integrations} from "../../shared/models/integrations.model";

@Component({
  selector: 'app-click-up-issue-details',
  templateUrl: './click-up-issue-details.component.html',
  styles: []
})
export class ClickUpIssueDetailsComponent implements OnInit {
  @Input('workspace') application: Integrations;
  @Input('externalMapping') externalApplicationDetails: TestCaseResultExternalMapping;
  @Output('unLink') unLink = new EventEmitter<TestCaseResultExternalMapping>();
  public issueDetails: any;
  public issueStatus: any;
  public issueTypeDetails: any;
  public issueUserDetails: any;
  public issueId: any;

  constructor() {
  }

  ngOnInit(): void {
    this.fetchDetails();
  }

  ngOnChanges(): void {
    this.fetchDetails();
  }

  fetchDetails() {
    if (this.externalApplicationDetails.fields) {
      this.issueDetails = this.externalApplicationDetails.fields;
      this.issueId = this.externalApplicationDetails.externalId;
    }
  }

  unLinkIssue(externalApplicationDetails: TestCaseResultExternalMapping) {
    this.unLink.emit(externalApplicationDetails);
  }

  htmlToPlaintext(text) {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

}
