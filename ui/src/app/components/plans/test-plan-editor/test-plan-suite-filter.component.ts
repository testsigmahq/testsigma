import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";
import {WorkspaceVersionService} from "../../../shared/services/workspace-version.service";

@Component({
  selector: 'app-test-plan-suite-filter',
  template: `
    <button
      #trigger="cdkOverlayOrigin" cdkOverlayOrigin (click)="openTagsDialog()"
      [ngClass]="{'suites-filter-applied': versionFilter}"
      class="btn icon-btn border-rds-2 ml-10 filter-icon-with-reset" [matTooltip]="'btn.common.filter'| translate">
      <i [ngClass]="{'fa-filter-alt': !versionFilter, 'fa-filter': versionFilter}"></i>
    </button>
    <ng-template
      #envTagsDialog="cdkConnectedOverlay"
      cdkConnectedOverlay
      [cdkConnectedOverlayOrigin]="trigger"
      [cdkConnectedOverlayOpen]="isOpen"
      cdkConnectedOverlayPanelClass="filter-dropdown">
      <div class="plan-tags-popup d-flex flex-column flex-wrap ts-form display-relative">
        <div class="min-w-250p fz-16 pb-0 pt-10 pt-18 px-10 px-18 rb-medium d-flex align-items-center">
          <span class="flex-grow-1 mr-15" [textContent]="'btn.common.filter' | translate"></span>
          <i class="pointer fa-close-alt" (click)="close()" [matTooltip]="'btn.common.close' | translate"></i>
        </div>
        <div class="form-group px-18 mt-15 pb-0">
          <app-version-selection
            class="position-relative" [customClass]="'runtime-project-skin'"
            (onVersionSelect)="setCurrentVersion($event)"
            [version]="version"
            [showWithoutDropdown]="true"
          ></app-version-selection>
        </div>

        <div class="d-flex align-items-center justify-content-end px-18 pb-18">
          <button
            [disabled]="!versionFilter"
            (click)="clear()" class="theme-btn-primary px-20 btn-sm" [translate]="'message.common.clear'">
          </button>
          <button
            [disabled]="!selectedVersion"
            (click)="selectedVersion && apply()" class="theme-btn-primary px-20 ml-15 btn-sm" [translate]="'btn.common.filter'">
          </button>
        </div>

      </div>
    </ng-template>
  `,
  styleUrls: ['./test-plan-suite-filter.component.scss']
})
export class TestPlanSuiteFilterComponent{
  @Input('version') version : WorkspaceVersion;
  @Input('versionFilter') versionFilter : WorkspaceVersion | null;
  @Output('onVersionFilter') onVersionFilter : EventEmitter<WorkspaceVersion|null> = new EventEmitter<WorkspaceVersion|null>();

  @ViewChild('envTagsDialog') filterDialog: CdkConnectedOverlay;
  @ViewChild('trigger') trigger;

  public isOpen = false;
  public selectedVersion:WorkspaceVersion;

  constructor(
    private versionService: WorkspaceVersionService
  ) {}

  openTagsDialog(){
    this.isOpen = true;
    setTimeout(() => {
      this.filterDialog.overlayRef._outsidePointerEvents.subscribe(res => {
        if(
          !res['path'][6].classList.contains('cdk-overlay-container') &&
          res['path']?.map(path => path.tagName == 'MAT-OPTION')?.filter(item=> item)?.length == 0
        ) { // TODO: need to Properly handle click out
          this.close();
        }
      });
    }, 200);
  }

  setCurrentVersion(version) {
    if(typeof version == 'number') {
      this.versionService.show(version).subscribe(res=> this.selectedVersion = res);
    } else {
      this.selectedVersion = version;
    }
  }

  clear() {
    this.onVersionFilter.emit(null);
    this.close();
  }

  apply() {
    this.onVersionFilter.emit(this.selectedVersion);
    this.close();
  }

  close() {
    this.filterDialog.overlayRef.detach();
    this.isOpen = false;
  }

}
