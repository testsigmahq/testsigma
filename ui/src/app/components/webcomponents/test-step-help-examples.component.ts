import {Component, ElementRef, Input, OnInit, ViewChild, Output, EventEmitter} from '@angular/core';
import {Page} from "../../shared/models/page";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {fromEvent} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {NaturaltextActionExample} from "../../models/natural-text-action-example.model";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {MatSelect} from '@angular/material/select';

@Component({
  selector: 'app-test-step-help-examples',
  templateUrl: './test-step-help-examples.component.html',
  styles: []
})
export class TestStepHelpExamplesComponent implements OnInit {
  @Input('templates') nlActions: Page<NaturalTextActions>;
  @Output('onSelectTemplate') onSelectTemplate = new EventEmitter<NaturalTextActions>();
  public filteredTemplates: NaturalTextActions[];
  public selectedAction: string = 'all';
  public actions: any;
  public expandedPanelIndex: number;
  public panelOpenState: Boolean = false;
  public selectedTemplateDetails:NaturaltextActionExample;
  public isFetching: boolean = false;
  @ViewChild('searchInput') searchInput: ElementRef;
  @ViewChild('selectToggle') selectToggle: MatSelect;

  constructor(
    private naturalTextActionsService: NaturalTextActionsService
  ) {
  }

  ngOnInit(): void {
    this.attachDebounceEvent()
    this.actions = ["wait", "verify", "browser", "frame", "check", "store", "select",
      "scroll", "click", "mouseover", "window", "navigate", "draganddrop", "cookies", "alert", "clear",
      "update", "get", "enter", "keys", "all"];
    if (this.nlActions && this.nlActions.content)
      this.toggleAction(this.selectedAction);
  }

  ngOnchange() {
    if (this.nlActions && this.nlActions.content)
      this.toggleAction(this.selectedAction);
  }

  toggleMatSelect(event): void {
    if(event){
      this.selectToggle.close();
    }
  }

  attachDebounceEvent() {
    if (this.searchInput && this.searchInput.nativeElement)
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            if (this.searchInput.nativeElement.value) {
              this.filter(this.searchInput.nativeElement.value);
            } else {
              this.filteredTemplates = this.nlActions.content;
            }
          })
        )
        .subscribe();
    else
      setTimeout(() => {
        this.attachDebounceEvent();
      }, 100);
  }

  filter(searchText?: string) {
    this.filteredTemplates = [];
    if (searchText && searchText.length) {
      this.nlActions.content.forEach(template => {
        if (template.searchableGrammar.toLowerCase().includes(searchText.toLowerCase()))
          this.filteredTemplates.push(template)
      })
    } else {
      this.filteredTemplates = this.nlActions.content;
    }
  }

  toggleAction(action) {
    if (this.searchInput && this.searchInput.nativeElement)
      this.searchInput.nativeElement.value = '';

    if (action == 'all') {
      this.filteredTemplates = this.nlActions.content;
      return
    }
    this.filteredTemplates = [];
    this.nlActions.content.forEach(template => {
      if (action == template.action)
        this.filteredTemplates.push(template)
    })
  }

  selectTemplate(event,example: NaturalTextActions) {
    this.onSelectTemplate.emit(example)
    event.stopPropagation();
    event.stopImmediatePropagation();
  }

  fetchDetails(id) {
    this.isFetching = true;
    this.selectedTemplateDetails = undefined;
    this.naturalTextActionsService.findTemplateDetails(id).subscribe(res => {
      this.selectedTemplateDetails = res;
      this.isFetching = false;
    })
  }
}
