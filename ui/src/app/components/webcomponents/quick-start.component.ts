import {Component, OnInit, ViewChild} from '@angular/core';
import {GetStartedTopicModel} from "../../models/get-started-topic.model";
import {MatDialog} from "@angular/material/dialog";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {GetStartedBaseComponent} from "./get-started-base.component";
import {filter} from "rxjs/operators";
import {NavigationEnd, Router} from "@angular/router";
import {TestCaseService} from "../../services/test-case.service";
import {DryTestPlanService} from "../../services/dry-test-plan.service";
import {TestSuiteService} from "../../services/test-suite.service";
import {TestPlanService} from "../../services/test-plan.service";
import {TestDataService} from "../../services/test-data.service";
import {UserPreferenceService} from "../../services/user-preference.service";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {UserPreference} from "../../models/user-preference.model";
import {CdkConnectedOverlay, CdkOverlayOrigin} from "@angular/cdk/overlay";
import {Location} from "@angular/common";
import {TestPlanResultService} from "../../services/test-plan-result.service";

@Component({
  selector: 'app-quick-start',
  template: `
    <div
      cdkDrag
      cdkDragBoundary=".quick-start-btn-boundary"
      cdkOverlayOrigin #trigger="cdkOverlayOrigin"
      cdkDragLockAxis="x"
      (cdkDragStarted)="onDrag()"
      (cdkDragReleased)="onDrop()"
      (click)="openQuickStartActions()"
      class="quick-start-btn position-absolute right-0">
      <i class="fa-power-flash pr-2"></i>
      <span
        [translate]="'quick_start.btn'"></span>
    </div>
    <ng-template
      #quickStartContainer="cdkConnectedOverlay"
      cdkConnectedOverlay
      [cdkConnectedOverlayOffsetX]="-210"
      [cdkConnectedOverlayOffsetY]="-8"
      [cdkConnectedOverlayOrigin]="trigger"
      [cdkConnectedOverlayOpen]="canShowHint && quickStartSelectedTopic">
      <div class="quick-start-popup">
        <div class="arrow-bottom"></div>
        <div>
          <i
            [matTooltip]="'hint.message.common.close' | translate"
            class="popup-dismiss" (click)="removeShowHintPreference()"></i>
          <div [translate]="quickStartSelectedTopic?.titleKey"></div>
          <div [translate]="quickStartSelectedTopic?.descriptionKey"></div>
        </div>
      </div>
    </ng-template>
    <ng-template
      #quickStartOverlay="cdkConnectedOverlay"
      cdkConnectedOverlay
      [cdkConnectedOverlayWidth]="'358px'"
      [cdkConnectedOverlayOrigin]="trigger"
      [cdkConnectedOverlayOpen]="openQuickStart">
      <div class="quick-start-container">
        <div
          class="quick-start-header">
          <span [translate]="'quick_start.title' | translate : { Percent : completePercent}"></span>
          <i
            [matTooltip]="'hint.message.common.close' | translate"
            class="quick-start-close" (click)="detachQuickStart()"></i>
        </div>
        <div class="quick-start-section-container">
          <div
            class="quick-start-section"
            *ngFor="let topic of filterTopics;let index = index">
            <mat-accordion>
              <mat-expansion-panel
                class="m-0"
                (closed)="panelOpenState = false;"
                (opened)="expandedPanelIndex=index;panelOpenState = true"
                [expanded]="index == expandedPanelIndex"
                hideToggle>
                <mat-expansion-panel-header
                  class="quick-start-section-header">
                  <mat-panel-title class="align-items-center">
                    <div
                      class="pr-10">
                      <div
                        class="circle no-after"
                        [class.finished]="isCompleted(topic)">
                        <i
                          *ngIf="isCompleted(topic)"
                          class="fa-check-solid"></i>
                      </div>
                    </div>
                    <span
                      class="quick-start-title"
                      [translate]="topic.titleKey"></span>
                  </mat-panel-title>
                </mat-expansion-panel-header>
                <div class="elements-table px-30" matExpansionPanelContent>
                  <div
                    class="quick-start-des"
                    [translate]="topic.descriptionKey"></div>
                  <div class="text-nowrap">
                    <button
                      class="btn btn-lg theme-emerald-green-btn border-rds-4"
                      [translate]="'get_started.try_it_now'"
                      (click)="tryIt(topic.navigateToTry, version?.id);detachQuickStart()"></button>
                    <button
                      class="btn btn-lg btn-clear-default border-rds-4"
                      (click)="openArticleByLink(topic.articleLink);detachQuickStart()">
                      <i class="text-purplish-blue fa-article pr-6 fz-15"></i>
                      <span [translate]="'get_started.read_more'" class="text-dark"></span>
                    </button>
                  </div>
                </div>
                <div>
                </div>
              </mat-expansion-panel>
            </mat-accordion>
          </div>
        </div>
      </div>
    </ng-template>
  `,
  styles: []
})
export class QuickStartComponent extends GetStartedBaseComponent implements OnInit {
  public version: WorkspaceVersion;
  public userPreference: UserPreference;
  public expandedPanelIndex: number;
  public panelOpenState: Boolean = false;
  public openQuickStart: boolean;
  public isManuallyClosed: boolean = false;
  public quickStartSelectedTopic: GetStartedTopicModel;
  @ViewChild('quickStartOverlay') quickStartOverlay: CdkConnectedOverlay;
  @ViewChild('quickStartContainer') overlayDir: CdkConnectedOverlay;
  @ViewChild('trigger') trigger: CdkOverlayOrigin;
  isOnDrag: boolean;
  private isClosedOnDrag: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public router: Router,
    private location: Location,
    public testCaseService: TestCaseService,
    public dryTestPlanService: DryTestPlanService,
    public testSuiteService: TestSuiteService,
    public TestPlanService: TestPlanService,
    public testDataService: TestDataService,
    public userPreferenceService: UserPreferenceService,
    private matModal: MatDialog,
    private versionService: WorkspaceVersionService,
    public testPlanResult:TestPlanResultService
  ) {
    super(
      authGuard,
      router,
      testCaseService,
      dryTestPlanService,
      testSuiteService,
      TestPlanService,
      testDataService,
      userPreferenceService,
      testPlanResult);
    router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.getStartedCounts = [];
      super.ngOnChanges();
      if (this.topics) {
        this.setTopicText()
      }
      if(this.isDashboard|| this.isTestCaseDetailsPage) {
        this.openDefault()
      }
    });
  }

  ngOnInit(): void {
    super.ngOnChanges();
    this.fetchVersion();
  }

  openDefault() {
    if(!this.isManuallyClosed && this.filterTopics?.length && this.getStartedCounts && this.getStartedCounts[this.topics[0].countKey] != undefined) {
      let isCiCdCompleted = false;
      let count = 0;
      this.filterTopics?.forEach(item => {
        if(this.isCompleted(item)) {
          if(item.titleKey == 'get_started.ci/cd_integration')
            isCiCdCompleted = true;
          count++;
        }
      });
      this.openQuickStart = this.openDefaultQuickStart && !(count > 5 || (count == 5 && !isCiCdCompleted));
    } else {
      setTimeout(() => this.openDefault(), 300);
    }
  }

  get openDefaultQuickStart() {
    return this.isDashboard || this.isTestCaseDetailsPage;
  }

  get isDashboard(){
    return this.location['_platformLocation']?.location?.href?.includes('dashboard')
  }

  get isTestCaseDetailsPage() {
    return this.location['_platformLocation']?.location?.href?.includes('/steps')
  }

  openQuickStartActions() {
    if (this.isOnDrag && !this.isClosedOnDrag) return;
    this.openQuickStart = true;
    this.isManuallyClosed = false;
    if (!this.quickStartOverlay.open)
      this.trigger.elementRef.nativeElement.click();
  }

  detachQuickStart(isClosedOnDrag?) {
    this.expandedPanelIndex = -1;
    this.quickStartOverlay.open = false;
    this.quickStartOverlay.overlayRef.detach();
    this.openQuickStart = false;
    this.isManuallyClosed = true;
    this.isClosedOnDrag = Boolean(isClosedOnDrag);
  }

  get canShowHint() {
    return !this.openQuickStart && !this.matModal?.openDialogs?.length;
  }

  fetchVersion() {
    this.userPreferenceService.show().subscribe(res => {
      this.userPreference = res;
      if (res.versionId) {
        this.versionService.show(res.versionId).subscribe(res => {
          this.version = res;
          this.setTopicValue(this.version?.workspace)
          this.setTopicText()
        }, err => this.loadDemoVersion());
      } else if (res.projectId) {
        this.versionService.findAll("projectId:" + res.projectId).subscribe(versions => {
          this.version = versions.content[0];
          this.setTopicValue(this.version?.workspace);
          this.setTopicText()
        }, () => {
          this.loadDemoVersion();
        })
      } else {
        this.loadDemoVersion();
      }
    });
  }

  loadDemoVersion() {
    this.versionService.findAll("isDemo:true").subscribe(versions => {
      this.version = versions.content[0];
      this.setTopicValue(this.version?.workspace)
      this.setTopicText()
    })
  }

  setTopicText() {
    if (this.checkAllRequestFinished()) {
      delete this.quickStartSelectedTopic
      this.filterTopics?.forEach((topic: GetStartedTopicModel) => {
        if (!this.isCompleted(topic) && !this.quickStartSelectedTopic) {
          this.quickStartSelectedTopic = topic;
        }
      })
    } else {
      setTimeout(() => this.setTopicText(), 200);
    }
    return true;
  }

  checkAllRequestFinished() {
    let isAllFinished = false;
    if(this.getStartedCounts && Object.keys(this.getStartedCounts).length) {
      isAllFinished = true;
      this.getStartedCounts.filter(item => {
        if(item == undefined){
          isAllFinished = false
        }
      })
    }
    return isAllFinished;
  }


  get filterTopics() {
    return this.topics?.filter(item => !(item.titleKey == 'get_started.local_test_plans' || item.titleKey == 'get_started.cross_browser' || item.titleKey == 'get_started.cross_device' || item.titleKey == 'get_started.overview'))
  }

  get completePercent() {
    let completedCount = 0;
    this.filterTopics?.forEach(item => {
      if(this.isCompleted(item)) {
        completedCount++;
      }
    });
    if(completedCount){
      completedCount = Math.floor((completedCount/ this.filterTopics.length) * 100);
    }
    return completedCount;
  }

  removeShowHintPreference() {
    this.overlayDir.open = false;
    this.overlayDir?.overlayRef?.detach();
  }

  openArticleByLink(articleLink) {
    window.open(articleLink, '_blank', 'nofollow');
  }

  onDrop() {
    setTimeout(
      () => {
        this.isOnDrag = false;
        if (this.isClosedOnDrag && !this.overlayDir.open)
          this.openQuickStartActions();
      }, 100);
  }

  onDrag() {
    this.trigger.elementRef.nativeElement.mouseOutsideComponent
    this.isOnDrag = true;
    if (this.quickStartOverlay.open)
      this.detachQuickStart(true);
  }

}
