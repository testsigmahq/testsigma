/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Injectable} from '@angular/core';
import {HttpHeaders, HttpParams} from "@angular/common/http";
import {Pageable} from "app/shared/models/pageable";

@Injectable()
export class HttpHeadersService {
  public contentTypeApplication = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  constructor() {
  }

  mapToParams(pageable: Pageable): HttpParams {
    const params = new HttpParams();
    if (pageable == undefined) {
      return params;
    }
    const json = pageable.serialize();
    for (const key in json) {
      if (json.hasOwnProperty(key)) {
        params.set(key, json[key]);
      }
    }
    return params;
  }

  serializeParams(filters?: string, sortByFilter?: string, pageable?: Pageable) {
    let params: HttpParams;
    if (filters) {
      params = new HttpParams().append('query', filters);
      if (sortByFilter) {
        params = new HttpParams().append('query', filters).append('sort', sortByFilter);
        if (pageable) {
          params = new HttpParams().append('query', filters).append('sort', sortByFilter).append("page", pageable.pageNumber + '').append("size", pageable.pageSize + '');
        }
      } else if (pageable) {
        params = new HttpParams().append('query', filters).append("page", pageable.pageNumber + '').append("size", pageable.pageSize + '');
      }
    } else if (sortByFilter) {
      params = new HttpParams().append('sort', sortByFilter);
      if (pageable) {
        params = new HttpParams().append('query', filters).append('sort', sortByFilter).append("page", pageable.pageNumber + '').append("size", pageable.pageSize + '');
      }
    } else if (pageable) {
      params = new HttpParams().append("page", pageable.pageNumber + '').append("size", pageable.pageSize + '');
    }
    return params;
  }
}
