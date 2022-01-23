import {Component, OnInit} from '@angular/core';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute} from '@angular/router';
import {BaseComponent} from "../../shared/components/base.component";
import {RequirementsService} from "../../services/requirements.service";
import {Requirement} from "../../models/requirement.model";
import {TestCaseService} from "../../services/test-case.service";
import {Page} from "../../shared/models/page";
import {TestCase} from "../../models/test-case.model";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-requirement-details',
  templateUrl: './details.component.html',
  host: {'class': 'page-content-container flex-wrap'},
})
export class DetailsComponent extends BaseComponent implements OnInit {
  public requirement: Requirement;
  public requirementId: number;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private requirementsService: RequirementsService,
    private route: ActivatedRoute,
    private testCaseService: TestCaseService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.requirementId = this.route.parent.snapshot.params.requirementId;
    this.fetchRequirement();
  }

  private fetchRequirement() {
    this.requirementsService.show(this.requirementId).subscribe(res => {
      this.requirement = res;
      this.route.snapshot.params = {...this.route.snapshot.params, ...{versionId: res.workspaceVersionId}};
      this.pushToParent(this.route, this.route.snapshot.params);
    });
  }
}
