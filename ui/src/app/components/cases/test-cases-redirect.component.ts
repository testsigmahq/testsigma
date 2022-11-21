import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TestCaseFilterService} from "../../services/test-case-filter.service";
import {StepGroupFilterService} from "../../services/step-group-filter.service";
import {UserPreferenceService} from "../../services/user-preference.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-td-redirect',
  template: `
    <router-outlet></router-outlet>`,
  styles: []
})
export class TestCasesRedirectComponent extends BaseComponent implements OnInit {

  constructor(
    private router: Router,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private testCaseFilterService: TestCaseFilterService,
    private stepGroupFilterService: StepGroupFilterService,
    private userPreferenceService: UserPreferenceService) {
    super(authGuard, notificationsService, translate,toastrService);
  }

  ngOnInit() {
    this.userPreferenceService.show().subscribe(userPreference => {
      let selectedFilterId = userPreference?.testCaseFilterId;
      this.route.parent.parent.params.subscribe((params: Params) => {
        const allParams = {...params, ...{versionId: params.versionId}};
        this.pushToParent(this.route, allParams);
        if (this.router.url.endsWith("/cases")) {
          this.testCaseFilterService.findAll(params.versionId).subscribe(res => {
            this.router.navigate(['/td', params.versionId, 'cases', 'filter', this.setFilterId(res.content, selectedFilterId)],{replaceUrl:true});
          });
        } else if (this.router.url.endsWith("/step_groups")) {
          this.stepGroupFilterService.findAll(params.versionId).subscribe(res => {
            this.router.navigate(['/td', params.versionId, 'step_groups', 'filter', this.setFilterId(res.content, selectedFilterId)],{replaceUrl:true});
          });
        }
      })
    })
  }

  setFilterId(filterContent, selectedFilterId) {
    let isFilterId = undefined;
    if(selectedFilterId)
      isFilterId = filterContent.find(filter => selectedFilterId && filter.id == selectedFilterId)?.id;
    if(!isFilterId) {
      isFilterId = filterContent.find(filter => filter.isDefault)?.id;
    }
    return isFilterId;
  }

}
