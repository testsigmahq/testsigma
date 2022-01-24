import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../shared/components/base.component";
import {UserPreferenceService} from "../services/user-preference.service";
import {ActivatedRoute, Params, Router} from '@angular/router';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {WorkspaceService} from "../services/workspace.service";
import {WorkspaceType} from "../enums/workspace-type.enum";

@Component({
  selector: 'app-projects',
  templateUrl: './workspaces.component.html',
  providers: [ScrollingModule]
})
export class WorkspacesComponent extends BaseComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private userPreferenceService: UserPreferenceService,
    private workspaceService: WorkspaceService) {
    super();
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      if (this.router.url.endsWith("/workspaces")) {
        this.userPreferenceService.show().subscribe(res => {
          if (res.workspaceId)
            this.router.navigate(['/workspaces', res.workspaceId]);
          else
            this.redirectToDemoApplication();
        });
      }
    })
  }

  redirectToDemoApplication() {
    this.workspaceService.findAll("isDemo:true,workspaceType:" + WorkspaceType.WebApplication).subscribe(res => {
      this.router.navigate(['/workspaces', res.content[0].id]);
    })
  }

}
