import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from "../../shared/models/integrations.model";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";

@Component({
  selector: 'app-azure-issue-details',
  templateUrl: './azure-issue-details.component.html',
  styles: []
})
export class AzureIssueDetailsComponent implements OnInit {
  @Input('azureIssueId') azureIssueId: Number;
  @Input('application') application: Integrations;
  @Input('externalMapping') externalApplicationDetails: EntityExternalMapping;
  @Output('unLink') unLink = new EventEmitter<EntityExternalMapping>();
  public issueDetails: any;
  public issueStatus: any;
  public issueTypeDetails: any;
  public issueUserDetails: any;
  public title: String;
  public description: String;
  public createdDate: any;
  public updatedDate: any;

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
      this.externalApplicationDetails.fields = this.externalApplicationDetails.fields['value']?this.externalApplicationDetails.fields['value'][0]:null;
      this.issueDetails = this.externalApplicationDetails.fields['fields'];
      this.title = this.issueDetails['System.Title'];
      this.description = this.issueDetails['System.Description'];
      this.issueStatus = this.issueDetails['System.State'];
      this.createdDate = this.issueDetails['System.CreatedDate'];
      this.updatedDate = this.issueDetails['System.ChangedDate'];
      this.issueTypeDetails = this.issueDetails['System.WorkItemType'];
      this.issueUserDetails = this.issueDetails['System.AssignedTo'] ? this.issueDetails['System.AssignedTo']['displayName'] : null;
    }
  }

  unLinkIssue(externalApplicationDetails: EntityExternalMapping) {
    this.unLink.emit(externalApplicationDetails);
  }

  htmlToPlaintext(text) {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

}
