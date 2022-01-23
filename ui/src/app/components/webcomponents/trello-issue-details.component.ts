import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Integrations} from '../../shared/models/integrations.model';
import {TestCaseResultExternalMapping} from '../../models/test-case-result-external-mapping.model';
import {IntegrationsService} from '../../shared/services/integrations.service';

@Component({
  selector: 'app-trello-issue-details',
  templateUrl: './trello-issue-details.component.html',
  styles: []
})
export class TrelloIssueDetailsComponent implements OnInit {
  @Input('application') application: Integrations;
  @Input('externalMapping') externalApplicationDetails: TestCaseResultExternalMapping;
  @Output('unLink') unLink = new EventEmitter<TestCaseResultExternalMapping>();
  public issueDetails: any;
  public issueTypeDetails: any;
  public issueUserDetails: any;
  public isButtonClicked = false;

  constructor(
    private applicationService: IntegrationsService,
  ) {
  }

  getBoardLink(boardId){
    return boardId.replace(/"/g, '');
  }
  ngOnInit(): void {
    this.fetchDetails();
  }

  fetchDetails() {
    this.applicationService.getTrelloIssue(this.externalApplicationDetails.workspaceId, this.externalApplicationDetails.externalId)
      .subscribe(data => {
        this.issueDetails = data;
        this.issueTypeDetails = this.issueDetails['component']
        this.issueUserDetails = this.issueDetails['creator_detail'];
      }, error => console.log(error));
  }

  unLinkIssue(externalApplicationDetails: TestCaseResultExternalMapping) {
    this.isButtonClicked = true;
    this.unLink.emit(externalApplicationDetails);
  }

  htmlToPlaintext(text) {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

}
