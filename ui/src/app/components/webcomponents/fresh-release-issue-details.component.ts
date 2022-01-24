import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from "../../shared/models/integrations.model";
import {TestCaseResultExternalMapping} from "../../models/test-case-result-external-mapping.model";

@Component({
  selector: 'app-fresh-release-issue-details',
  templateUrl: './fresh-release-issue-details.component.html',
  styles: []
})
export class FreshReleaseIssueDetailsComponent implements OnInit {
  @Input('freshReleaseIssueId') freshReleaseIssueId: Number;
  @Input('application') application: Integrations;
  @Input('externalMapping') externalApplicationDetails: TestCaseResultExternalMapping;
  @Output('unLink') unLink = new EventEmitter<TestCaseResultExternalMapping>();
  public issueDetails: any;
  public issueStatus: any;
  public issueTypeDetails: any;
  public issueUserDetails: any;

  constructor() {
  }

  ngOnInit(): void {
    this.fetchDetails()
  }

  ngOnChanges(): void {
    this.fetchDetails();
  }

  fetchDetails() {
    if (this.externalApplicationDetails.fields) {
      this.issueDetails = this.externalApplicationDetails.fields['issue'];
      this.issueStatus = this.externalApplicationDetails.fields['statuses'][0];
      this.issueTypeDetails = this.externalApplicationDetails.fields['issue_types'][0];
      this.issueUserDetails = this.externalApplicationDetails.fields['users'][0];
    }
  }

  unLinkIssue(externalApplicationDetails: TestCaseResultExternalMapping) {
    this.unLink.emit(externalApplicationDetails);
  }

  htmlToPlaintext(text) {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

}
