import {Component, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Page} from "../../shared/models/page";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {expand, fade} from "../../shared/animations/animations";
import {CdkConnectedOverlay} from '@angular/cdk/overlay';
import {NaturaltextActionExample} from "../../models/natural-text-action-example.model";


@Component({
  selector: 'app-natural-text-action',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
  animations: [fade, expand],
})

export class ListComponent extends BaseComponent implements OnInit {
  public nlActions: Page<NaturalTextActions>;
  public selectedTemplate: NaturaltextActionExample;
  public filteredBy: string;
  public actions: String[] = ["all"];
  public filterIsOpen = false;
  @ViewChild('filter') overlayDir: CdkConnectedOverlay;
  public fetchingCompleted = false;
  private versionId: number;
  private defaultQuery: string;
  private filterQuery = "";
  private isFiltered: boolean;
  private searchQuery = "";

  constructor(
    private naturalTextActionsService: NaturalTextActionsService,
    private workspaceVersionService: WorkspaceVersionService,
    public route: ActivatedRoute,
    public router: Router) {
    super();
  }

  ngOnInit(): void {
    this.route.parent.params.subscribe((params: Params) => {
      this.versionId = params.versionId;
      this.pushToParent(this.route, params);
      this.filteredBy = this.actions['0'];
      this.workspaceVersionService.show(this.versionId).subscribe((workspaceVersion: WorkspaceVersion) => {
        if(workspaceVersion.workspace.isRest)
          this.router.navigate(['/td']);
        this.defaultQuery = "workspaceType:" + workspaceVersion.workspace.workspaceType;
        this.fetchNLActions();
      });
    });
  };

  public fetchTemplateDetails(template) {
    this.naturalTextActionsService.findTemplateDetails(template.id).subscribe(res => {
      this.selectedTemplate = res
      this.selectedTemplate.template = template;
    });
  }

  filterBy(filter) {
    if (this.filteredBy == filter)
      filter = "all";
    this.filteredBy = filter;
    if (filter == "all") {
      this.filterQuery = "";
    } else if (filter.includes(',')) {
      switch (filter) {
        case 'wait,frame':
          filter = "frame"
          break;
        case 'wait,verify':
          filter = "verify"
          break;
        case 'wait,browser':
          filter = "browser"
          break;
      }
      filter = filter.includes('double tap') ? "double tap" : ( filter.includes('long press') ? "long press" : filter);
      this.filterQuery = ",action:*" + filter + "*" +
        (filter == "verify" ? ",action!verify" : (filter == "frame"? ",action!frame":(filter == "browser"?",action!browser":"")));
    } else {
      this.filterQuery = ",action:" + filter;
    }
    this.fetchNLActions()
  }

  openFilter() {
    this.filterIsOpen = true;
    setTimeout(() => {
      this.overlayDir.overlayRef.backdropClick().subscribe(res => {
        this.overlayDir.overlayRef.detach();
        this.filterIsOpen = false;
      });
    }, 200);
  }

  removeSpaces(action): String {
    return action.replace(/\s/g, "");
  }

  search(term: string) {
    if (term) {
      this.isFiltered = true;
      this.searchQuery = ",naturalText:*" + term + "*";
    } else {
      this.isFiltered = false;
      this.searchQuery = "";
    }
    this.fetchNLActions()
  }

  private fetchNLActions() {
    this.fetchingCompleted = false;
    let filter = this.defaultQuery + this.filterQuery + this.searchQuery;
    this.naturalTextActionsService.findAll(filter, "action,asc").subscribe(res => {
      this.fetchingCompleted = true;
      this.selectedTemplate = null;
      res.content = res.content.filter(template => template.displayName != 'breakLoop' && template.displayName != 'continueLoop')
      this.nlActions = res;
      this.nlActions.content.forEach((template, i) => {
        if (this.actions.indexOf(template.action) == -1)
          this.actions.push(template.action);
        this.fetchingCompleted = true;
      })
    });
  }
}
