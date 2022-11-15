import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from '../../shared/models/integrations.model';
import {EntityExternalMapping} from '../../models/entity-external-mapping.model';
import {IntegrationsService} from '../../shared/services/integrations.service';

@Component({
  selector: 'app-bugzilla-issue-details',
  templateUrl: './bugzilla-issue-details.component.html',
  styles: []
})
export class BugZillaIssueDetailsComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('externalMapping') externalApplicationDetails: EntityExternalMapping;
  @Output('unLink') unLink = new EventEmitter<EntityExternalMapping>();
  public issueDetails: any;
  public issueTypeDetails: any;
  public issueUserDetails: any;
  public isButtonClicked = false;

  constructor(
    private applicationService: IntegrationsService,
  ) {
  }

  ngOnInit(): void {
    this.fetchDetails();
  }

  fetchDetails() {
    this.applicationService.getBugZillaIssue(this.externalApplicationDetails.applicationId, this.externalApplicationDetails.externalId)
      .subscribe(data => {
        this.issueDetails = data['bugs'][0];
        this.issueTypeDetails = this.issueDetails['component']
        this.issueUserDetails = this.issueDetails['creator_detail'];
      }, error => console.log(error));
  }

  unLinkIssue(externalApplicationDetails: EntityExternalMapping) {
    this.isButtonClicked = true;
    this.unLink.emit(externalApplicationDetails);
  }

  htmlToPlaintext(text) {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

}
