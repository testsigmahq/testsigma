import {Component, OnInit, EventEmitter, Output} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {UserPreferenceService} from "../../services/user-preference.service";

@Component({
  selector: 'app-global-add',
  templateUrl: './global-add.component.html',
  styles: []
})
export class GlobalAddComponent extends BaseComponent implements OnInit {
  public versionId: Number;
  @Output('isClicked') isClicked = new EventEmitter<boolean>();


  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private userPreferenceService: UserPreferenceService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.fetchPreference()
  }

  fetchPreference() {
    this.userPreferenceService.show().subscribe(res => {
      this.versionId = res?.versionId;
    });
  }
  isClickedItems() {
    this.isClicked.emit(false);
  }
}
