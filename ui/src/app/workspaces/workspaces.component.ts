import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../shared/components/base.component";
import {UserPreferenceService} from "../services/user-preference.service";
import {ActivatedRoute, NavigationEnd, Params, Router} from '@angular/router';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {WorkspaceService} from "../services/workspace.service";
import {WorkspaceType} from "../enums/workspace-type.enum";
import {Subscription} from "rxjs";
import {NavigationService} from "../services/navigation.service";

@Component({
  selector: 'app-projects',
  templateUrl: './workspaces.component.html',
  providers: [ScrollingModule]
})
export class WorkspacesComponent extends BaseComponent implements OnInit {

  private routerSub: Subscription;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private userPreferenceService: UserPreferenceService,
    private navigation:NavigationService,
    private workspaceService: WorkspaceService) {
    super();

    this.routerSub = this.router.events.subscribe((val) => {
      if (val instanceof NavigationEnd) {
        if (val.urlAfterRedirects.endsWith("/workspaces"))
          this.redirectToWorkspaces();
      }
    });

  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      if (this.router.url.endsWith("/workspaces")) {

        this.redirectToWorkspaces();

      }
    })
  }


  redirectToDemoApplication() {
    this.workspaceService.findAll("isDemo:true,workspaceType:" + WorkspaceType.WebApplication).subscribe(res => {
      this.navigation.replaceUrl(['/workspaces', res.content[0].id])
    })
  }

  private redirectToWorkspaces() {

    this.userPreferenceService.show().subscribe(res => {
      if (res.workspaceId)
        this.navigation.replaceUrl(['/workspaces', res.workspaceId])
      else
        this.redirectToDemoApplication();
    });
  }
}
