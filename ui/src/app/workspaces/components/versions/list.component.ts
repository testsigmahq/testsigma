import {Component, OnInit} from '@angular/core';
import {InfiniteScrollableDataSource} from "../../../data-sources/infinite-scrollable-data-source";
import {WorkspaceVersionService} from "../../../shared/services/workspace-version.service";
import {ActivatedRoute, Router} from '@angular/router';
import {WorkspaceVersion} from "../../../models/workspace-version.model";

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  host: {'class':"h-100" ,'style':"flex: 1; width: calc(100% - 220px);"}
})
export class ListComponent implements OnInit {
  public workspaceId: number;
  public versions: InfiniteScrollableDataSource;
  public isDemo:Boolean;

  constructor(
    private route: ActivatedRoute,
    private versionService: WorkspaceVersionService,
    private router: Router) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(res => {
      if (res?.versionId) {
        this.versionService.show(res.versionId).subscribe(res1 => {
          this.isDemo = res1.workspace.isDemo;
        })
      }
    })

    this.route.parent.parent.params.subscribe(res => {
      this.workspaceId = res.workspaceId;
      this.fetchVersions();
      this.redirectToVersion();
    });
  }

  fetchVersions() {
    this.versions = new InfiniteScrollableDataSource(this.versionService, "workspaceId:" + this.workspaceId);
  }

  redirectToVersion() {
    if (this.versions.isFetching)
      setTimeout(() => this.redirectToVersion(), 100);
    else if (this.router.url.endsWith("/versions"))
      this.router.navigate([(<WorkspaceVersion>this.versions['cachedItems'][0]).id], {relativeTo: this.route});
  }

  getIsDemo(){
    return this.isDemo;
  }

}
