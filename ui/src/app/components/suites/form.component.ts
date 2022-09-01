import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import {TestSuite} from "../../models/test-suite.model";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {debounceTime} from 'rxjs/operators';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TestStep} from "../../models/test-step.model";
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {TestCase} from "../../models/test-case.model";
import {TestSuiteTag} from "../../models/test-suite-tag.model";
import {MatDialog} from '@angular/material/dialog';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {TestSuiteAddCaseFormComponent} from "../webcomponents/test-suite-add-case-form.component";
import {TestCaseService} from 'app/services/test-case.service';
import {TestSuiteService} from "../../services/test-suite.service";
import {ActivatedRoute, Router} from '@angular/router';
import {TestSuiteTagService} from 'app/services/test-suite-tag.service';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {TestCaseTag} from "../../models/test-case-tag.model";
import {ElementTag} from "../../models/element-tag.model";
import {Location} from "@angular/common";
import {Page} from "../../shared/models/page";
import {TestCaseStatus} from "../../enums/test-case-status.enum";
import {Pageable} from "../../shared/models/pageable";
import {ToastrService} from "ngx-toastr";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestPlanService} from "../../services/test-plan.service";
import {TestSuitePrerequisiteChangeComponent} from "../../shared/components/webcomponents/test-suite-prerequisite-change.component";

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
})
export class FormComponent extends BaseComponent implements OnInit {
  public tags: TestSuiteTag[] = [];
  public filteredList: TestSuiteTag[] = [];
  public editTags: TestSuiteTag[] = [];
  public testCases = [new TestCase()];
  public searchAutoComplete = new FormControl();
  public separatorKeysCodes: number[] = [ENTER, COMMA];
  public isDirty: Boolean;
  public testSuiteForm: FormGroup;
  public testSuite = new TestSuite();
  public formSubmitted = false;
  public saving = false;
  public versionId: number;
  public version: WorkspaceVersion;
  public showDescription = false;
  public activeTestCases: TestCase[] = [];
  @ViewChild('searchTag') searchTag: ElementRef;
  private testSuiteId: any;
  public suiteList: Page<TestSuite>;
  public tagsFiltered: boolean = false;
  currentPage = new Pageable();
  private originalPreRequisite: number;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private router: Router,
    private matDialog: MatDialog,
    private workspaceVersionService: WorkspaceVersionService,
    private testCaseService: TestCaseService,
    private testSuiteService: TestSuiteService,
    private testSuiteTagService: TestSuiteTagService,
    private testPlanService: TestPlanService,
    private location: Location) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.versionId = this.route.snapshot.params.versionId;
    this.testSuiteId = this.route.snapshot.params.testSuiteId;
    this.pushToParent(this.route, this.route.snapshot.params);
    this.workspaceVersionService.show(this.versionId).subscribe(res => this.version = res);
    this.fetchTestSuiteTags();
    this.testSuite.workspaceVersionId = this.versionId;
    if (this.testSuiteId) {
      this.fetchSuite()
      this.fetchAssociatedTags();
    } else {
      this.edit();
      this.initFormControls()
    }
    this.fetchSuites()
  }

  fetchTestSuiteTags() {
    this.testSuiteTagService.findAll().subscribe(res => {
        this.tags = res;
        if(this.tags.length == 0) {
          this.tagsFiltered = true;
        }
      }
    );
  }

  fetchAssociatedTags(): void {
    this.testSuiteTagService.find(this.testSuiteId).subscribe(res => {
      this.tags = res;
      this.edit();
    });
  }
  fetchSuite() {
    this.testSuiteService.show(this.testSuiteId).subscribe(suite => {
      this.testSuite = suite;
      this.originalPreRequisite = suite.preRequisite;
      this.currentPage.pageSize = 1000;
      this.testCaseService.findAll("suiteId:"+this.testSuiteId, '', this.currentPage).subscribe(res => {
        this.activeTestCases = res.content;
        this.initFormControls()
      })
    })
  }

  public noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { 'whitespace': true };
  }

  initFormControls() {
    this.testSuiteForm = new FormGroup({
      name: new FormControl(this.testSuite.name, [Validators.required, Validators.minLength(4), Validators.maxLength(250),this.noWhitespaceValidator]),
      description: new FormControl(this.testSuite.description),
      preRequisite: new FormControl(this.testSuite.preRequisite),
      activeTestCases: new FormControl(this.activeTestCases, [Validators.required])
    })
  }

  update() {
    this.formSubmitted = true;
    if (this.testSuiteForm.invalid) return;
        this.saving = true;
    if (this.testSuite.preRequisite != this.originalPreRequisite)
      this.fetchLinkedPlans(this.testSuite.id);
    else
      this.populateAndUpdate();
  }

  public fetchLinkedPlans(id) {
    let testPlans: InfiniteScrollableDataSource;
    testPlans = new InfiniteScrollableDataSource(this.testPlanService, "suiteId:" + id , "name,asc" );
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testPlans.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (!testPlans.isEmpty)
          _this.openPrerequisiteChangeWarning(testPlans);
        else
          _this.populateAndUpdate();
      }
    }
  }

  private openPrerequisiteChangeWarning(executions) {
    let description = this.translate.instant('test_suites.prerequisite_linked_with_plans');
    const dialogRef = this.matDialog.open(TestSuitePrerequisiteChangeComponent, {
      width: '568px',
      height: 'auto',
      data: {
        description: description,
        executions: executions,
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe(result => {
        if (result) {
          this.populateAndUpdate(executions);
        } else {
          this.formSubmitted = false;
          this.saving = false;
        }
      });
  }

  populateAndUpdate(executions?) {
    this.populateTestSuiteRequest();
    this.testSuiteService.update(this.testSuite).subscribe(
      () => {
        let msg = this.testSuite.preRequisite == this.originalPreRequisite || !Boolean(executions)?
          this.translate.instant('message.common.update.success', {FieldName: "Test Suite"}):
          this.translate.instant('test_suite.prerequisite.update.success', {SuiteName: this.testSuite.name,
            executions:(executions.cachedItems.map(execution=>execution.name).join(", ") )+ (executions.cachedItems.length==executions.totalElements?"":", ...")});
        this.showNotification(NotificationType.Success, msg);
        this.router.navigate(['/td', 'suites', this.testSuite.id])
      },
      err => {
        this.formSubmitted = false;
        this.saving = false;
        this.translate.get('message.common.update.failure', {FieldName: "Test Suite"})
          .subscribe(res => this.showAPIError(err, res))
      }
    )
  }

  create() {
    this.formSubmitted = true;
    if (this.testSuiteForm.invalid) return;
    this.saving = true;
    this.populateTestSuiteRequest();
    this.testSuiteService.create(this.testSuite).subscribe(
      (testSuite) => {
        this.translate.get('message.common.created.success', {FieldName: "Test Suite"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', 'suites', testSuite.id])
      },
      err => {
        this.formSubmitted = false;
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: "Test Suite"})
          .subscribe(res => this.showAPIError(err, res,'Test suite'))
      }
    )
  }

  drop(event: CdkDragDrop<TestStep[]>) {
    if (event.previousIndex != event.currentIndex) {
      moveItemInArray(this.activeTestCases, event.previousIndex, event.currentIndex);
    }
  }
  attachEvents() {
    this.searchAutoComplete = new FormControl();
    this.searchAutoComplete.valueChanges.pipe(debounceTime(200)).subscribe((term) => {
      if (term && typeof term == 'string')
        this.filteredList = this.filteredList.filter(tag => tag.name.toUpperCase().indexOf(term.toUpperCase()) > -1);
      else
        this.filteredList = this.tags.filter(tag => {
          return !this.editTags.find(res => res.id == tag.id || res.name.toUpperCase() == tag.name.toUpperCase());
      this.tagsFiltered = true;
        });
    });
  }


  edit() {
    this.editTags = [...this.tags];
      this.filteredList = this.tags.filter(tag => !this.tags.find(res => res.id == tag.id));
      this.attachEvents();
  }

  addSuites() {
    this.matDialog.open(TestSuiteAddCaseFormComponent, {
      width: '85vw',
      height: '90vh',
      data: {
        versionFilter: "workspaceVersionId:" + this.version.id,
        applicationFilter: "workspaceId:" + this.version.workspaceId,
        allTestCasesFilter: "workspaceVersionId:" + this.version.id + ",status:"+TestCaseStatus.READY + ",deleted:false,isStepGroup:false",
        activeTestCases: this.activeTestCases,
      },
      panelClass: ['mat-dialog', 'full-width', 'rds-none']
    }).afterClosed().subscribe(res => {
      if (res) {
        this.activeTestCases = res;
        this.testSuiteForm.controls.activeTestCases.setValue(this.activeTestCases);
      }
    });
  }

  removeCase(testCase: TestCase) {
    const index = this.activeTestCases.indexOf(testCase);
    this.activeTestCases.splice(index, 1);
  }

  addNewTag(name: string): void {
    if ((name || '').trim() && this.isNotAdded((name))) {
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

   isNotAdded(name): boolean {
    return this.searchAutoComplete.value && !this.editTags.find(tag => tag.name == name);
  }

  addExistingTag(tag: TestCaseTag | ElementTag) {
    this.editTags.push(tag);
    this.searchTag.nativeElement.value = '';
    this.searchTag.nativeElement.blur();
    setTimeout(() => this.searchTag.nativeElement.focus(), 10);
    this.isDirty = true;
  }

  removeTag(tag: any) {
    this.editTags.splice(this.tags.indexOf(tag), 1);
    this.searchTag.nativeElement.blur();
    setTimeout(() => this.searchTag.nativeElement.focus(), 10);
    this.isDirty = true;
  }

  private populateTestSuiteRequest() {
    const rawValue = this.testSuiteForm.getRawValue();
    this.testSuite.description = rawValue.description;
    this.testSuite.name = rawValue.name;
    this.testSuite.tags = this.editTags.map(tag => tag.name);
    this.testSuite.testCaseIds = this.activeTestCases.map(testCase => testCase.id);
  }

  goBack() {
    if(this.testSuite.id){
      this.router.navigate(['/td', 'suites', this.testSuite.id])
    } else {
      this.router.navigate(['/td',this.versionId, 'suites'])
    }
  }

  setPreRequisite(testsuite:TestSuite) {
    this.testSuite.preRequisite = testsuite?.id;
  }

  getCurrentItem(items: Page<any>, id: number) {
    let selectedItems = null;
    items.content.filter(item => {
      if (item.id == id) {
        selectedItems = item;
      }
    })
    return selectedItems;
  }

  fetchSuites(term?: any) {
    let query = "workspaceVersionId:" + this.versionId ;
    if(this.testSuiteId){
      query += (",id!" + this.testSuiteId);
    }
    if (term) {
      query += ",name:*" + term + "*";
    }

    this.testSuiteService.findAll(query , undefined).subscribe(res => {
      this.suiteList = res;
      if(this.testSuite.preRequisite && !this.suiteList.content.find(preRequisite => preRequisite.id == this.testSuite.preRequisite)) {
        this.testSuiteService.show(this.testSuite.preRequisite).subscribe(res => {
          this.testSuite.preRequisiteSuite = res;
          this.suiteList.content.push(res)
        })
      }

    })
  }

  get selectedCaseIds () {
    return this.activeTestCases.map(testcase => testcase.id);
  }

  noTagsCreated() {
    return this.tagsFiltered && !this.searchAutoComplete.value && !this.filteredList?.length && !this.editTags?.length;
  }
}
