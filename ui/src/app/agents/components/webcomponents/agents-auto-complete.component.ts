import {Component, EventEmitter, Input, OnInit, Output, Optional, ViewChild} from '@angular/core';
import {AgentService} from "../../services/agent.service";
import {Page} from "../../../shared/models/page";
import {Agent} from "../../models/agent.model";
import {FormControl, FormGroup} from '@angular/forms';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {DryRunFormComponent} from "../../../components/webcomponents/dry-run-form.component";
import {Router} from '@angular/router';
import {PageObject} from "../../../shared/models/page-object";
import { debounceTime } from 'rxjs/internal/operators/debounceTime';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {MatTooltip} from "@angular/material/tooltip";
import {distinctUntilChanged, map} from "rxjs/operators";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";

@Component({
  selector: 'app-agents-auto-complete',
  template: `
    <div
      *ngIf="!agentsEmpty"
      class="w-100">
      <mat-form-field
        class="w-100"
        [formGroup]="agentForm"
        appearance="outline" (click)="groupTrigger.openPanel();focusOnMatSelectSearch()">
        <input type="text" readonly
               [value]="noneValue ? 'None' : value?.name"
               class="autocomplete-placeholder">
        <i class="fa-down-arrow-filled"></i>
        <input
          type="text" matInput
          [formControl]="formControl"
          #groupTrigger="matAutocompleteTrigger"
          [matAutocomplete]="autoComplete" hidden>
        <mat-autocomplete
          #autoComplete="matAutocomplete" disableOptionCentering>
          <mat-option class="p-4" [disabled]="true">
            <mat-progress-spinner
              class="search-spinner" mode="indeterminate" diameter="15" *ngIf="loadingSearch">
            </mat-progress-spinner>
            <input
              (keyup.space)="$event.stopImmediatePropagation()"
              (keydown.space)="$event.stopImmediatePropagation()"
              type="text" [formControl]="searchAutoComplete" autocomplete="off">
          </mat-option>
          <mat-option
            *ngFor="let item of activeItems"
            [value]="item.id"
            (click)="setAgent(item)"
            [textContent]="item.name + (item?.suffix ? item?.suffix : '')">
          </mat-option>
          <mat-option
            *ngFor="let item of disabledItems"
            [value]="item.id"
            [disabled]="true"
            class="disabled text-t-secondary"
            [matTooltip]="item?.suffixNext? '' : !item.isOnline()?
                    ('agents.list_view.unable_to_contact_agent_tool_tip' |
                    translate: {updatedAt: (item.updatedAt | amTimeAgo)}):('agents.unable_to_reach'|translate)">
          <span
            style="height: 4px;line-height: 1"
            [matTooltip]="item?.suffixNext ? ('agents.list_view.version_out_of_sync' |
                    translate: {agentVersion: item?.agentVersion, latestVersion:
                    item?.currentAgentVersion}) : ''">
            <i
              *ngIf="item?.suffixNext"
              class="fa-exclamation-triangle-solid text-dark pr-6"></i>
            <span
              [textContent]="item.name +  (item?.suffixNext ? ' (' + ('agents.list_view.out_of_sync'|translate) + ')' : '')"></span>
          </span>
          </mat-option>
          <mat-option
            *ngIf="agents?.content?.length == 0 && !!searchAutoComplete.value"
            [disabled]="true" [textContent]="'select.search.notfound'|translate"></mat-option>
        </mat-autocomplete>
      </mat-form-field>
      <label class="control-label required" [translate]="labelText"></label>
    </div>
    <div
      *ngIf="agentsEmpty">
      <div class="batch-banner">
        <i class="batch-icon"></i>
        <span
          [translate]="'test_plan_form.machine-empty' | translate: {LabelName: version?.workspace?.isMobileNative ? 'test device':'test machine'}"></span>
        <div (click)="closeDialog()">
          <a class="btn btn-white mx-15 px-20" [translate]="'test_plan_form.install'"></a>
        </div>
        <a
          rel="nofollow"
          href="https://testsigma.com/docs/agent/setup-on-windows-mac-linux/"
          class="text-underline" target="_blank" [translate]="'test_plan_form.agents_info'"></a>
      </div>
    </div>
  `,
  styles: []
})
export class AgentsAutoCompleteComponent implements OnInit {
  @Input('formGroup') agentForm: FormGroup;
  @Input('formCtrl') formControl: FormControl;
  @Input('isAvailableCheck') isAvailableCheck: Boolean;
  @Input('labelText') labelText: string;
  @Input('value') value: Agent;
  @Output('onAgentChange') onAgentChange = new EventEmitter<Agent>();
  @Output('onAgents') onAgents = new EventEmitter<Page<Agent>>();
  @Output('isAgentOnline') isAgentOnline = new EventEmitter<boolean>();
  @Input('version') version: WorkspaceVersion;
  @ViewChild('tooltip') tooltip: MatTooltip;
  public agents: Page<Agent>;
  public agent: Agent;
  public agentsEmpty: boolean;


  public searchAutoComplete = new FormControl();
  public loadingSearch: boolean = false;
  public noneValue: Boolean = false;


  constructor(
    private agentService: AgentService,
    private router: Router,
    private authGuard: AuthenticationGuard,
    @Optional() private inspectionRef?: MatDialog,
    @Optional() private dialogRef?: MatDialogRef<DryRunFormComponent>) {
  }

  ngOnInit(): void {
    this.fetchAgents();
    this.searchAutoComplete.valueChanges.pipe(
      map((event) => {
        if(event?.length > 0)this.loadingSearch = true
        return event;
      }),debounceTime(1000), distinctUntilChanged()).subscribe((term:string) => {
      this.fetchAgents(term);
    })
  }

  ngOnChanges() {
    if(this.value)
      this.setAgent(this.value);
    else if(this.agent)
      this.setAgent(this.agent);
  }


  ngOnDestroy() {
    this.formControl.setErrors(null);
  }

  fetchAgents(term?: string) {
    let searchName = '';
    if (term) {
      searchName = ",title:*" + term + "*";
    }
    let isAgentAvailable = false;
    this.agentService.findAll(searchName,"updatedDate,desc").subscribe(res => {
      if(searchName?.length == 0)
        this.agentsEmpty = res.empty;
      this.agents = res;
      this.agents.content.forEach(agent => {
        if(agent.isOutOfSync() || !agent.isOnline()) {
          agent['isDisabled'] = true;
          agent['suffixNext'] = agent.isOutOfSync()?" (Out of Sync)":'';
          if (!isAgentAvailable) isAgentAvailable = false;
        }
        else if(this.agent == null && this.formControl.value) {
          if(agent.id == this.formControl.value) {
            this.agent = agent;
            this.setAgent(this.agent);
          }
        } else if(this.agent == null){
          this.agent = agent;
          this.setAgent(this.agent)
        }

        if (agent.isOnline() && !agent.isOutOfSync()) isAgentAvailable = true;
      });
      this.onAgents.emit(this.agents);
      if (this.value)
        this.setAgent(this.value);
      else if (this.agents?.content?.length && !this.formControl.value)
        this.setAgent(this.agents.content[0])
      else if(this.formControl.value && !term) {
        this.agentService.findAll("id:" + this.formControl.value).subscribe(res => {
          this.setAgent(res.content[0]);
        });
      }
      this.loadingSearch = false;
      this.isAgentOnline.emit(isAgentAvailable);
    }, error => {
      this.onAgents.emit(this.agents);
      this.loadingSearch = false;
    })
  }

  setAgent(agent: Agent) {
    this.value = null;
    this.formControl.patchValue(agent.id);
    this.onAgentChange.emit(agent);
    this.value = agent;
  }

  closeDialog() {
    this.inspectionRef.closeAll();//TODO need to close Specific modals
    if(this.dialogRef) {
      this.dialogRef.afterClosed().subscribe(() => {
        this.router.navigate(['/agents'])
      })
    } else {
      this.router.navigate(['/agents'])
    }
  }

  navigateToBilling() {
    this.inspectionRef.closeAll();//TODO need to close Specific modals
    if(this.dialogRef) {
      this.dialogRef.afterClosed().subscribe(() => {
        this.router.navigate(['/settings/billing']);
      })
    } else {
      this.router.navigate(['/settings/billing']);
    }
  }
  focusOnMatSelectSearch() {
    let input = document.querySelector('.mat-select-panel-wrap input');
    input = input ? input : document.querySelector('.mat-autocomplete-panel input');
    if (input) {
      input.setAttribute("id", "matSearch");
      document.getElementById("matSearch")['value'] = "";
      document.getElementById("matSearch").focus();
      this.searchAutoComplete.patchValue("");
    }
  }

  setValue(item) {
    this.noneValue = false;
    this.value = item;
    this.onAgentChange.emit(item);
  }

  setNoneValue() {
    this.noneValue = true;
    this.onAgentChange.emit(null);
  }

  get disabledItems(): PageObject[] {
    return this.agents?.content?.filter((item: PageObject) => item['isDisabled']);
  }

  get activeItems(): PageObject[] {
    return this.agents?.content?.filter((item: PageObject) => !item['isDisabled']);
  }

}
