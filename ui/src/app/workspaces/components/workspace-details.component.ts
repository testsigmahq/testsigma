import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {WorkspaceService} from "../../services/workspace.service";
import {Workspace} from "../../models/workspace.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from "../../shared/components/base.component";
import {fromEvent} from 'rxjs/internal/observable/fromEvent';
import {debounceTime, distinctUntilChanged, filter} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-application-details',
  templateUrl: './workspace-details.component.html',
  host: {'style': 'flex:1; width:calc(100% - 220px)', 'class': 'h-100'},
  styles: [
  ]
})
export class WorkspaceDetailsComponent extends BaseComponent implements OnInit {
  public workspace= new Workspace();
  public versions: InfiniteScrollableDataSource;
  @ViewChild('versionsSearch', {static: false}) versionsSearch: ElementRef;
  public activeTabIndex: number;
  private applicationCount: number;

  constructor(
    private dialog: MatDialog,
    private router: Router,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private versionService: WorkspaceVersionService,
    private workspaceService: WorkspaceService,
    private route: ActivatedRoute) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.route.parent.params.subscribe(res => {
      this.workspace.id = res.workspaceId;
      this.fetchWorkspace();
      this.attachSearchEvents();
    });
  }

  fetchWorkspace() {
    this.workspaceService.show(this.workspace.id).subscribe(res => {
      this.workspace = res;
      this.fetchVersions("");
    });
  }

  fetchVersions(versionNameQuery?: string) {
    this.versions = new InfiniteScrollableDataSource(this.versionService, "workspaceId:" + this.workspace?.id + versionNameQuery);
  }

  attachSearchEvents() {
    if(this.versionsSearch?.nativeElement)
      fromEvent(this.versionsSearch.nativeElement, 'keyup').pipe(filter(Boolean), debounceTime(500), distinctUntilChanged())
        .subscribe(res => {
          let versionNameQuery = this.versionsSearch.nativeElement.value ?
            ",versionName:*" + this.versionsSearch.nativeElement.value + '*' : '';
          this.fetchVersions(versionNameQuery);
        })
    else
      setTimeout(() => {
        this.attachSearchEvents();
      }, 100);
  }

  destroyApplication(id){
    this.workspaceService.delete(id).subscribe(
      () => {
        this.router.navigate(['/workspaces', this.route.parent.parent.parent.snapshot.params.projectId ,'apps']);
        this.translate.get("message.common.deleted.success", {FieldName: 'Application'})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
      },
      _err => {
        this.translate.get("message.common.deleted.failure", {FieldName: 'Application'})
          .subscribe(res => this.showNotification(NotificationType.Error, res))
      }
    );
  }

}
