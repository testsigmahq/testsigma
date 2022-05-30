import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import {AdhocRunConfigurationService} from "../../services/adhoc-run-configuration.service";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {AdhocRunConfiguration} from "../../models/adhoc-run-configuration.model";
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {fromEvent} from 'rxjs';
import {MAT_DIALOG_DATA, MatDialog} from '@angular/material/dialog';

import {Workspace} from "../../models/workspace.model";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {TestDevice} from "../../models/test-device.model";

@Component({
  selector: 'app-saved-configuration-list',
  template: `
    <div class="theme-overlay-header">
      <div
        class="ts-col-70 d-flex fz-15 rb-medium"
        [translate]="'save_configuration.title'">
      </div>
      <div class="ml-auto d-flex ts-col-30">
        <div class="ts-col-100 pl-10">
          <div class="d-flex align-items-center ts-col-75">
            <i
              [matTooltip]="'hint.message.common.search' | translate"
              class="fa-search fz-13 mr-5"></i>
            <input
              class="form-control border-0"
              style="display: inline-block; padding: 0!important"
              #searchInput
              [placeholder]="'save_configuration.placeholder' | translate">
          </div>
        </div>
        <button
          class="theme-overlay-close"
          type="button"
          [matTooltip]="'hint.message.common.close' | translate"
          mat-dialog-close>
        </button>
      </div>
    </div>
    <div
      class="theme-overlay-content without-footer pb-15">
      <div
        *ngIf="configurations?.length"
        class="list-header" style="padding-left: 12px">
        <div class="ts-col-40 d-flex">
          <span [translate]="'save_configuration.name'"></span>
        </div>
        <div
          class="ts-col-35"
          [translate]="'save_configuration.environment'"></div>
      </div>
      <cdk-virtual-scroll-viewport
        itemSize="65"
        class="list-container theme-w-o-h-scroll sm-h">
        <div class="list-view md-pm green-highlight"
             *ngFor="let configuration of configurations; let index=index">
          <div class="ts-col-65 d-flex">
            <span
              class="text-break"
              [translate]="configuration.name"></span>
          </div>
          <div
            class="ts-col-35 d-flex">
            <app-test-machine-info-column
              [testDevice]="configuration?.testDevice"></app-test-machine-info-column>
            <i
              [matTooltip]="'hint.message.common.delete' | translate"
              class="ml-auto pointer fa-trash-thin action-icons"
              (click)="delete(configuration.id)"></i>
          </div>
        </div>
        <div
          *ngIf="!configurations?.length && !isFetching"
          class="empty-full-container h-90 pl-10">
          <div class="empty-full-content">
            <div class="empty-run-sm"></div>
            <div
              class="empty-text fz-15"
              [translate]="'message.common.search.not_found'"></div>
          </div>
        </div>
        <app-placeholder-loader *ngIf="isFetching"></app-placeholder-loader>
      </cdk-virtual-scroll-viewport>
    </div>
  `,
  host: {'class': 'theme-overlay-container'},
  styles: []
})
export class DryRunSavedConfigListComponent extends BaseComponent implements OnInit {
  public configurations: AdhocRunConfiguration[];
  public allConfigs: AdhocRunConfiguration[];
  @ViewChild('searchInput') searchInput: ElementRef;
  public searchTerm: string;
  public isFetching: boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private dryRunSavedConfigurationService: AdhocRunConfigurationService,
    private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public options: {
      application: Workspace
    }) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.attachDebounceEvent();
    this.fetchAll();
  }

  fetchAll() {
    this.dryRunSavedConfigurationService.findAll(this.options.application.workspaceType).subscribe(res => {
      this.configurations = res;
      this.allConfigs = res;
      this.enRichExecutionEnvironments();
    })
  }

  enRichExecutionEnvironments() {
    this.configurations.forEach(config => {
      let json = config.serialize();
      json['capabilities'] = json['desiredCapabilities']
      config.testDevice = new TestDevice().deserialize(json);
    })
  }

  delete(id) {
    this.translate.get("save_configuration.delete.confirmation").subscribe((res) => {
      const dialogRef = this.dialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result)
          this.destroy(id);
      });
    });
  }

  destroy(id) {
    this.dryRunSavedConfigurationService.delete(id).subscribe(() => {
        this.translate.get('message.common.deleted.success', {FieldName: 'Favorite Ad-hoc Run Config'}).subscribe((res) => {
          this.showNotification(NotificationType.Success, res);
          this.fetchAll();
        })
      },
      error => {
        this.translate.get('message.common.deleted.failure', {FieldName: 'Favorite Ad-hoc Run Config'}).subscribe((res) => {
          this.showAPIError(error, res);
        })
      })
  }

  attachDebounceEvent() {
    if (this.searchInput && this.searchInput.nativeElement)
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            if (this.searchInput?.nativeElement?.value) {
              this.searchTerm = this.searchInput.nativeElement.value;
              this.configurations = this.configurations.filter(config => config.name.toUpperCase().indexOf(this.searchTerm.toUpperCase()) > -1);
            } else {
              this.searchTerm = undefined;
              this.configurations = this.allConfigs;
            }
          })
        )
        .subscribe();
    else
      setTimeout(() => {
        this.attachDebounceEvent();
      }, 100);
  }
}
