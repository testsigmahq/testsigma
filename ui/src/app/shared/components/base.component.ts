/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Component, OnInit, Optional} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ResultConstant} from "../../enums/result-constant.enum";
import {AuthenticationGuard} from "../guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {Validators} from '@angular/forms';
import {ToastrService} from "ngx-toastr";
import {ProgressAnimationType} from "ngx-toastr/toastr/toastr-config";


@Component({
  selector: 'app-base',
  template: ``,
  styles: [],
  providers: [NotificationsService]
})
export class BaseComponent implements OnInit {
  resultConstant = ResultConstant;
  focusSearch: boolean;

  constructor(
    @Optional() public authGuard?: AuthenticationGuard,
    @Optional() public notificationsService?: NotificationsService,
    @Optional() public translate?: TranslateService,
    @Optional() public toastrService?: ToastrService) {
  }

  ngOnInit() {
  }

  pushToParent(route: ActivatedRoute, params: object | undefined) {
    if (route.snapshot.data && route.snapshot.data.legacyURL) {
      let legacyURL = route.snapshot.data.legacyURL;
      for (const key in params) {
        if (params.hasOwnProperty(key)) {
          legacyURL = legacyURL.replace(new RegExp(':' + key), params[key]);
        }
      }
      //TODO: [Pratheepv] Need to remove once we switch new UI. [NEW_UI_CLEANUP]
      if (window.top != window)
        window.top.history.pushState({}, route.snapshot.data.legacyTitle, legacyURL);
    }
  }

  indexOfResult(constant: any) {
    return Object.keys(ResultConstant).indexOf(constant);
  }


  showNotification(type: NotificationType, message) {
    const temp = {
      type: type,
      title: status,
      content: message,
      timeOut: 7000,
      positionClass: 'toast-bottom-left',
      progressBar: true,
      progressAnimation: <ProgressAnimationType>'increasing'
    };
    if (this.toastrService) {
      this.toastrService.show(temp.content, temp.title, temp, temp.type);
    }
  }

  searchSelectDropdown(value, searchKey) {
    this[searchKey] = value;
  }

  focusOnMatSelectSearch() {
    let input = document.querySelector('.mat-select-panel-wrap input');
    input = input ? input : document.querySelector('.mat-autocomplete-panel input');
    if (input) {
      input.setAttribute("id", "matSearch");
      document.getElementById("matSearch")['value'] = "";
      document.getElementById("matSearch").focus();
    }
  }

  setValue(value, input) {
    document.querySelector('input[name="' + input + '"]').setAttribute("value", value);
  }


  requiredIfValidator(predicate) {
    return (formControl => {
      if (!formControl.parent) {
        return null;
      }
      if (predicate()) {
        return Validators.required(formControl);
      }
      return null;
    })
  }

  showAPIError(exception, internalErrorMSG) {
    if (exception['status'] == 422 || exception['status'] == 451)
      this.showNotification(NotificationType.Error, exception['error']['error']);
    else if (exception['status'] == 500)
      this.showNotification(NotificationType.Error, (exception?.error?.code ? this.translate.instant(exception?.error?.code) : internalErrorMSG) || internalErrorMSG);
    else if (exception?.error?.objectErrors?.length > 0)
      this.showNotification(NotificationType.Error, this.translate.instant('message.duplicate_entity'));
    else
      this.showNotification(NotificationType.Error, internalErrorMSG);
  }

  getScrollParent(node: HTMLElement) {
    if (node == null) {
      return null;
    }

    if (node.scrollHeight > node.clientHeight) {
      return node;
    } else {
      return this.getScrollParent(node.parentElement);
    }
  }

  byPassSpecialCharacters(query: string) {
    let returnData = [],
      keys =[];
    query?.split(',')?.forEach(item => {
      let operatorIndex = item.match(/!|~|:|;|>|<|@|\$/).index;
      let searchValue = item.slice(operatorIndex+1);
      let searchKeyWithOperator = item.slice(0,operatorIndex+1);
      if(keys.indexOf(item.slice(0,operatorIndex))>-1) return;
      keys.push(item.slice(0,operatorIndex));
      item = searchKeyWithOperator + encodeURIComponent(searchValue.replace(/'/g, 'ts_single_quote')
        .replace(/!/g, 'ts_negation')
        .replace(/~/g, 'ts_like')
        .replace(/:/g, 'ts_colon')
        .replace(/;/g, 'ts_semicolon')
        .replace(/>/g, 'ts_greater_than')
        .replace(/</g, 'ts_lesser_than'))
        .replace(/@/g, 'ts_at_sign')
        .replace(/\$/g, 'ts_dollar_sign');
      returnData.push(item);
    } );
    return returnData.toString();
  }
}
