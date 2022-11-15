import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from "../../shared/models/integrations.model";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {IntegrationsService} from "../../shared/services/integrations.service";

@Component({
  selector: 'app-mantis-issue-details',
  templateUrl: './mantis-issue-details.component.html',
  styles: []
})
export class MantisIssueDetailsComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('externalMapping') externalApplicationDetails: EntityExternalMapping;
  @Output('unLink') unLink = new EventEmitter<EntityExternalMapping>();
  public issueDetails: any;
  public issueStatus: any;
  public issueTypeDetails: any;
  public issueUserDetails: any;
  public issueFieldsDetails: any;

  constructor(
    private applicationService: IntegrationsService,
  ) {
  }

  ngOnInit(): void {
    this.fetchDetails();
  }

  fetchDetails() {
    this.applicationService.getMantisIssue(this.externalApplicationDetails.applicationId, this.externalApplicationDetails.externalId)
      .subscribe(data => {
        this.issueDetails = data["issues"][0];
        this.issueTypeDetails = data['issues'][0]['category'];
        this.issueUserDetails = data['issues'][0]['reporter'];
        this.issueStatus = data['issues'][0]['status'];
      }, error => console.log(error));
  }

  unLinkIssue(externalApplicationDetails: EntityExternalMapping) {
    this.unLink.emit(externalApplicationDetails);
  }

  htmlToPlaintext(text) {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

}
