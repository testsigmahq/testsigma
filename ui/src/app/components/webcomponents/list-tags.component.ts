import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {TestCaseTagService} from "../../services/test-case-tag.service";
import {ElementTagService} from "../../services/element-tag.service";
import {TestCaseTag} from "../../models/test-case-tag.model";
import {ElementTag} from "../../models/element-tag.model";
import {FormControl} from '@angular/forms';
import {debounceTime} from 'rxjs/operators';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-list-tags',
  templateUrl: './list-tags.component.html',
  styles: []
})
export class ListTagsComponent extends BaseComponent implements OnInit {
  @Input('editable') editable: boolean;
  @Input('entityId') entityId: number;
  @Input('service') tagService: TestCaseTagService | ElementTagService;

  @ViewChild('searchTag') searchTag: ElementRef;

  public tags: TestCaseTag[] | ElementTag[];
  public tagsList: TestCaseTag[] | ElementTag[];
  public filteredList: TestCaseTag[] | ElementTag[];
  public searchAutoComplete = new FormControl();
  public editing: boolean;
  public editTags: TestCaseTag[] | ElementTag[];
  public separatorKeysCodes: number[] = [ENTER, COMMA];
  public saving: boolean;
  public isDirty: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.fetchAssociatedTags();
  }

  fetchAssociatedTags(): void {
    this.tagService.find(this.entityId).subscribe(res => {
      this.tags = res;
    });
  }

  attachEvents() {
    this.searchAutoComplete = new FormControl();
    this.searchAutoComplete.valueChanges.pipe(debounceTime(200)).subscribe((term) => {
      if (term && typeof term == 'string')
        this.filteredList = this.filteredList.filter(tag => tag.name.toUpperCase().indexOf(term.toUpperCase()) > -1);
      else
        this.filteredList = this.tagsList.filter(tag => {
          return !this.editTags.find(res => res.id == tag.id || res.name.toUpperCase() == tag.name.toUpperCase());
        });
    });
    setTimeout(() => {
      this.searchTag.nativeElement.focus();
    }, 100);
  }


  removeTag(tag: TestCaseTag): void {
    this.editTags.splice(this.tags.indexOf(tag), 1);
    this.searchTag.nativeElement.blur();
    setTimeout(() => this.searchTag.nativeElement.focus(), 10);
    this.isDirty = true;
  }

  addNewTag(name: string): void {
    if ((name || '').trim() && this.isNotAdded(name)) {
      let tag = new TestCaseTag();
      tag.name = name.trim();
      this.editTags.push(tag);
    }
    this.searchTag.nativeElement.value = '';
    this.searchAutoComplete.setValue('');
    this.searchTag.nativeElement.blur();
    setTimeout(() => this.searchTag.nativeElement.focus(), 10);
    this.isDirty = true;
  }

  edit() {
    this.editTags = [...this.tags];
    this.tagService.findAll().subscribe(res => {
      this.tagsList = res;
      this.filteredList = this.tagsList.filter(tag => !this.tags.find(res => res.id == tag.id));
      this.editing = true;
      this.attachEvents();
    });
  }

  addExistingTag(tag: TestCaseTag | ElementTag) {
    this.editTags.push(tag);
    this.searchTag.nativeElement.value = '';
    this.searchTag.nativeElement.blur();
    setTimeout(() => this.searchTag.nativeElement.focus(), 10);
    this.isDirty = true;
  }

  isNotAdded(name: string):boolean{
   return this.searchAutoComplete.value && !this.editTags.find(tag => tag.name == name);
  }

  save() {
    if (!this.isDirty) {
      this.editing = false;
    }
    this.saving = true;
    this.tagService.update(this.entityId, this.editTags.map(tag => tag.name)).subscribe(() => {
      this.translate.get("labels.updated.success_msg").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.tagService.find(this.entityId).subscribe(res => {
          this.tags = res
        });
        this.editing=false;
        this.saving=false;
      });
    });
  }
}
