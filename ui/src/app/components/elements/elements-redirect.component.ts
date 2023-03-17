import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {ElementFilterService} from "../../services/element-filter.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NavigationService} from "../../services/navigation.service";

@Component({
  selector: 'app-td-redirect',
  template: `
    <router-outlet></router-outlet>`,
  styles: []
})
export class ElementsRedirectComponent extends BaseComponent implements OnInit {

  constructor(
    private router: Router,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    private route: ActivatedRoute,
    private navigation:NavigationService,
    private elementFilterService: ElementFilterService) {
    super(authGuard, notificationsService, translate);
  }

  ngOnInit() {
    this.route.parent.parent.parent.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      if (this.router.url.endsWith("/elements") || this.router.url.endsWith("/elements/create")) {
          this.elementFilterService.findAll(params.versionId).subscribe(res => {
          if(this.router.url.endsWith("/elements/create"))
            this.router.navigate(['/td', params.versionId, 'elements', 'filter', res.content.find(filter => filter.isDefault).id],  {queryParams: {isCreate: "true"}});
          else
            this.router.navigate(['/td', params.versionId, 'elements', 'filter', res.content.find(filter => filter.isDefault).id]);
        });
      }
    })
  }

}
