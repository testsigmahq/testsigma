import {Component, Inject, OnInit} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {ElementFilter} from "../../models/element-filter.model";
import {ElementFilterService} from "../../services/element-filter.service";

@Component({
  selector: 'app-element-filter-form',
  templateUrl: './element-filter-form.component.html'
})

export class ElementFilterFormComponent extends BaseComponent implements OnInit {

  public filter: ElementFilter;

  constructor(
    public dialogRef: MatDialogRef<ElementFilterFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { version: WorkspaceVersion, filter: ElementFilter, query: string },
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public elementFilterService: ElementFilterService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    if (this.data.filter) {
      this.filter = this.data.filter;
    } else {
      this.filter = new ElementFilter();
      this.filter.name = "";
      this.filter.versionId = this.data.version.id;
      this.filter.isDefault = false;
      this.filter.isPublic = true;
    }
    if (this.data.query) {
      this.filter.normalizeCustomQuery(this.data.query);
      console.log(this.filter);
      this.filter.queryHash = this.filter.normalizedQuery;
    }
  }

  save(): void {
    if (this.filter.id)
      this.updateFilter();
    else
      this.createFilter();
  }

  private updateFilter() {
    this.elementFilterService.update(this.filter.id, this.filter).subscribe((res) => {
      this.translate.get('filter.saved.success', {name: this.filter.name}).subscribe((key: string) => {
        this.showNotification(NotificationType.Success, key);
        this.dialogRef.close(res);
      }, error => {
        this.translate.get('message.common.update.failure', {FieldName:'Element View'}).subscribe((key: string) => {
          this.showAPIError(error, key);
        });
      });
    });
  }

  private createFilter() {
    this.elementFilterService.create(this.filter).subscribe((res) => {
      this.translate.get('filter.created.success', {name: this.filter.name}).subscribe((key: string) => {
        this.showNotification(NotificationType.Success, key);
        this.dialogRef.close(res);
      }, error => {
        this.translate.get('message.common.created.failure', {FieldName:'Element View'}).subscribe((key: string) => {
          this.showAPIError(error, key);
        });
      });
    },(error) => {
      if(error.status==422) {
        this.translate.get('message.duplicate_entity.with_name',{EntityName: this.filter.name}).subscribe((message: string) => {
          this.showNotification(NotificationType.Error, message);
          this.dialogRef.close();
        });
      }
    });
  }
}
