/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {AuthenticationGuard} from "../../guards/authentication.guard";
import {UserPreferenceService} from "../../../services/user-preference.service";
import {WorkspaceVersionService} from "../../services/workspace-version.service";
import {WorkspaceService} from "../../../services/workspace.service";
import {Page} from "../../models/page";
import {Workspace} from "../../../models/workspace.model";
import {UserPreference} from "../../../models/user-preference.model";
import {Router} from '@angular/router';
import {FormControl, FormGroup} from '@angular/forms';
import {MatMenuTrigger} from '@angular/material/menu';
import {MatDialog} from '@angular/material/dialog';
import {OnBoarding} from "../../../enums/onboarding.enum";

@Component({
  selector: 'app-workspace-switcher',
  templateUrl: './workspace-switcher.component.html',
  styles: [`
  .inner-menu{
    box-shadow: rgba(0,0,0,0.1) 0px 0px 2px 2px;
    border-radius: 3px;
    background: white;
    overflow: hidden;
  }

  .item-max-height{
    max-height: 10rem;
    overflow: auto;
  }

  `]
})
export class WorkspaceSwitcherComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  @Input('leftNavPath') leftNavPath?: string;
  @Output('onProjectSwitch') onProjectSwitch = new EventEmitter<void>();
  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;
  @ViewChild('changeStep', {static: false}) public changeStepRef: ElementRef;

  public userPreference: UserPreference;
  public show: Boolean = false;
  public applications: Page<Workspace>;
  public versions: Page<WorkspaceVersion>;
  public projectSwitcherForm: FormGroup;

  constructor(
    private router: Router,
    private dialogRef: MatDialog,
    private userPreferenceService: UserPreferenceService,
    private workspaceService: WorkspaceService,
    private versionService: WorkspaceVersionService,
    public authGuard: AuthenticationGuard) {
  }

  get liveWebVersions() {
    return this.versions?.content?.filter(version => !version.workspace.isDemo && version.workspace.isWeb);
  }

  get liveMobileWebVersions() {
    return this.versions?.content?.filter(version => !version.workspace.isDemo && version.workspace.isMobileWeb);
  }

  get liveAndroidVersions() {
    return this.versions?.content?.filter(version => !version.workspace.isDemo && version.workspace.isAndroidNative);
  }

  get liveiOSVersions() {
    return this.versions?.content?.filter(version => !version.workspace.isDemo && version.workspace.isIosNative);
  }


  get sampleWebApplicationVersion() {
    return this.versions?.content?.find(version =>  version.workspace.isWeb && version.workspace.isDemo);
  }


  get sampleAndroidApplicationVersion() {
    return this.versions?.content?.find(version =>  version.workspace.isAndroidNative && version.workspace.isDemo);
  }

  get sampleiOSApplicationVersion() {
    return this.versions?.content?.find(version =>  version.workspace.isIosNative && version.workspace.isDemo);
  }

  get sampleMobileWebApplicationVersion() {
    return this.versions?.content?.find(version =>  version.workspace.isMobileWeb && version.workspace.isDemo);
  }

  ngOnInit() {
    this.userPreferenceService.show().subscribe(res => {
      this.userPreference = res;
      this.fetchVersions();
      this.fetchApplications();
    });
  }


  go(version: WorkspaceVersion) {
    this.userPreference.selectedVersion = version;
    this.userPreference.selectedWorkspace = version.workspace;
    let url = this.router.url.split('/').splice(1);
    this.userPreferenceService.save(this.userPreference).subscribe(res => {
      this.userPreference = res;
      if (url[0] == 'dashboard') {
        this.goToDashboard();
      } else if (isNaN(parseInt(this.leftNavPath))) {
        let leftNavPath = this.leftNavPath;
        if (leftNavPath == 'runs' || leftNavPath == 'test_case_results' || leftNavPath == 'suite_results' || leftNavPath == 'machine_results')
          leftNavPath = "results";
        this.router.navigate(["/" + url[0], this.userPreference.versionId, leftNavPath]);
      } else {
        this.router.navigate(["/" + url[0], this.userPreference.versionId, url[1]]);
      }
      this.show = false;
      this.sendCloseDialog();
    });
  }

  goToDashboard() {
    let shouldReuseMethod = this.router.routeReuseStrategy.shouldReuseRoute;
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.router.navigate(['/dashboard']);
    setTimeout(() => {
      this.router.routeReuseStrategy.shouldReuseRoute = shouldReuseMethod;
      this.router.onSameUrlNavigation = 'ignore';
    }, 300);
  }

  toggle() {
    this.show = !this.show;
    this.fetchVersions();
  }

  toggleModal() {
    this.trigger.openMenu();
    this.toggle();
  }

  menuHide() {
    this.dialogRef.closeAll();
  }

  enableModalClick(event) {
    event.stopPropagation();
  }

  fetchVersions() {
    this.versionService.findAll().subscribe(res => {
      this.versions = res;
    });
  }

  fetchApplications(){
    this.workspaceService.findAll().subscribe(res => {
      this.applications = res;
    })
  }

  sendCloseDialog() {
    this.onProjectSwitch.emit();
  }

  stopAction($event: MouseEvent) {
    $event.preventDefault();
    $event.stopPropagation();
    $event.stopImmediatePropagation();
    return false;
  }
}
