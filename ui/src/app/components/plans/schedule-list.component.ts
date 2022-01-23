import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {FormControl} from '@angular/forms';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-schedule-list',
  templateUrl: './schedule-list.component.html',
  host: {'class': 'page-content-container'},
  styles: []
})
export class ScheduleListComponent extends BaseComponent implements OnInit {

  public versionId: number;

  public viewTypeControl: FormControl;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute
  ) {

    super(authGuard, notificationsService, translate, toastrService);
  }

  get isListView() {
    return this.viewTypeControl.value == 'list_view';
  }

  get isCalendarView() {
    return this.viewTypeControl.value == 'calendar_view';
  }

  ngOnInit(): void {
    this.versionId = this.route.parent.parent.snapshot.params.versionId;
    this.pushToParent(this.route, this.route.parent.parent.snapshot.params);
    this.viewTypeControl = new FormControl('calendar_view', []);
  }
}
