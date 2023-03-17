import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceService} from "../../services/workspace.service";
import {Page} from "../../shared/models/page";
import {Workspace} from "../../models/workspace.model";
import {Pageable} from "../../shared/models/pageable";
import {FormControl, FormGroup} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";

@Component({
  selector: 'app-redirect',
  template: `
    <div class="d-flex flex-wrap h-100">
      <div class="d-flex flex-wrap pl-50 pr-40 pt-20 ts-col-100 theme-border-b">
        <div class="d-flex align-items-center ts-col-100">
          <div
            *ngIf="Form && applications?.content"
            class="text-truncate w-20">
            <div class="d-flex position-relative">
              <app-auto-complete
                class="pb-2 d-block"
                [formGroup]="Form"
                [formCtrlName]="Form.controls['name']"
                [value]="getCurrentItem(applications, application?.id)"
                [items]="applications"
                [hasApplicationIcon]=true
                [inline]="true"
                (onSearch)="fetchApplications($event)"
                (onValueChange)="switchApplication($event)"
              ></app-auto-complete>
            </div>
            <span class="text-t-secondary fz-12" [translate]="'left_nav.workspace_settings'"></span>
          </div>
        </div>
        <div class="ts-col-100 position-relative d-flex pt-18">
          <div class="details-container md value-lg ts-col-90">
            <div class="details-items" *ngIf="versions">
              <label class="details-title" [translate]="'application_settings.application_details.version'"></label>
              <div class="details-info">
                <div class="d-flex">
                  <div [textContent]="versions?.content.length"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="d-flex ts-col-100 theme-details-scroll sm-h">
        <div class="secondary-menu">
          <ul id="ul_version_home" class="project-nav item-container">
            <div class="secondary-nav-container">
              <li>
                <a
                  [routerLink]="['details']"
                  [routerLinkActive]="'active'"
                  class="nav-items">
                  <i class="fa-info"></i>
                  <span [translate]="'application_settings.left_nav.details'"></span>
                </a>
              </li>
              <li>
                <a
                  [routerLink]="['/workspaces', application?.id, 'versions']"
                  [routerLinkActive]="'active'"
                  class="nav-items">
                  <i class="fa-versions"></i>
                  <span [translate]="'application_settings.left_nav.versions'"></span>
                </a>
              </li>
              <li>
                <a
                  [routerLink]="['test_case_types']"
                  [routerLinkActive]="'active'"
                  class="nav-items">
                  <i class="fa-test-case-types"></i>
                  <span [translate]="'application_settings.left_nav.test_case_types'"></span>
                </a>
              </li>
              <li>
                <a
                  [routerLink]="['test_case_priorities']"
                  [routerLinkActive]="'active'"
                  class="nav-items">
                  <i class="fa-test-case-priorities"></i>
                  <span [translate]="'application_settings.left_nav.test_case_priorities'"></span>
                </a>
              </li>
            </div>
          </ul>
        </div>
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
  styles: []
})
export class LeftNavComponent implements OnInit {
  public application: Workspace;
  public projectId: number;
  public versions: Page<WorkspaceVersion>;
  public applications: Page<Workspace>;
  public currentApplication: Workspace;
  public Form: FormGroup;

  constructor(public route: ActivatedRoute,
              public router: Router,
              public workspaceService: WorkspaceService,
              public versionService: WorkspaceVersionService,
              public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.projectId = this.route.snapshot.params.projectId;
    this.fetchApplication()
  }

  fetchApplication(switchId?: number) {
    this.workspaceService.show(switchId ? switchId : this.route.snapshot.params.workspaceId).subscribe(res => {
      this.application = res;
      this.currentApplication = this.application;
      if (!switchId)
        this.fetchApplications()
      this.versionService.findAll("workspaceId:" + this.application.id).subscribe(res => {
        this.versions = res;
        if (switchId)
          this.router.navigate(['/workspaces', this.application.id, 'details'])
      })
    }, () => {
      console.log('problem while loading project ::' + this.route.snapshot.params.projectId + ' so switching to demo project');
      this.redirectToDemoApplication();
    });
  }

  redirectToDemoApplication() {
    this.workspaceService.findAll("isDemo:true").subscribe(res => {
      this.router.navigate(['/workspaces', res.content[0].id, 'details']);
    })
  }

  fetchApplications(term?) {
    let searchName = '';
    if (term) {
      searchName = ",name:*" + term + "*";
    }
    if (!term) {
      this.Form = new FormGroup({
        name: new FormControl(this.application.name, [])
      })
    }

    let pageable = new Pageable();
    this.workspaceService.findAll((searchName), undefined, pageable)
      .subscribe(res => this.applications = res);
  }

  switchApplication(project: Workspace) {
    this.fetchApplication(project.id)
  }

  getCurrentItem(items: Page<any>, id: number) {
    let selectedItems = null;
    if(items.content.length>0){
      items.content.filter(item => {
        if (item.id == id) {
          selectedItems = item;
        }
      })
    }
    else{
      selectedItems = this.application;
    }

    return selectedItems;
  }

}
