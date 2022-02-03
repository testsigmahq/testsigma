import {Component, Input, OnInit} from '@angular/core';
import {ElementElementDetails} from "../../models/element-locator-details.model";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-element-attributes',
  templateUrl: './element-attributes.component.html',
  styles: []
})
export class ElementAttributesComponent extends BaseComponent implements OnInit {

  @Input('elementDetails') elementDetails: ElementElementDetails;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
  }


}
