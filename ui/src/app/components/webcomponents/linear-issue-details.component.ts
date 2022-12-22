import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from '../../shared/models/integrations.model';
import {EntityExternalMapping} from '../../models/entity-external-mapping.model';
import {IntegrationsService} from '../../shared/services/integrations.service';

@Component({
  selector: 'app-linear-issue-details',
  templateUrl: './linear-issue-details.component.html',
  styles: []
})
export class LinearIssueDetailsComponent implements OnInit {
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
    this.applicationService.getLinearIssue(this.externalApplicationDetails.applicationId, this.externalApplicationDetails.externalId)
      .subscribe(data => {
        this.issueDetails = data['data']['issue'];
        this.issueTypeDetails = this.issueDetails['project'];
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
