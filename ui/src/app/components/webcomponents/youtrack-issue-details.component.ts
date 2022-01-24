import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from "../../shared/models/integrations.model";
import {TestCaseResultExternalMapping} from "../../models/test-case-result-external-mapping.model";

@Component({
  selector: 'app-youtrack-issue-details',
  templateUrl: './youtrack-issue-details.component.html',
  styles: []
})
export class YoutrackIssueDetailsComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input("YTissueId") YTissueId:Number;
  @Input('externalMapping') externalApplicationDetails: TestCaseResultExternalMapping;
  @Output('unLink') unLink = new EventEmitter<TestCaseResultExternalMapping>();
  public issueDetails: any;
  public issueStatus: any;
  public issueTypeDetails: any;
  public issueUserDetails: any;
  public title: String;
  public description: String;

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
      this.issueDetails = this.externalApplicationDetails.fields;
      this.title = this.issueDetails['summary'];
      this.description = this.issueDetails['description'];
      this.issueStatus = this.issueDetails['stage'];
      this.issueTypeDetails = this.issueDetails['$type'];
      this.issueUserDetails = this.issueDetails['reporter']['name'] ;
    }
  }

  unLinkIssue(externalApplicationDetails: TestCaseResultExternalMapping) {
    this.unLink.emit(externalApplicationDetails);
  }

  htmlToPlaintext(text) {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

}
