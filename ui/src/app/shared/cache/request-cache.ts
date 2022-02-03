import { Injectable } from '@angular/core';
import { HttpRequest, HttpResponse } from '@angular/common/http';

const maxAge = 30000;
@Injectable()
export class RequestCache  {
  cache = new Map();

  get(req: HttpRequest<any>): HttpResponse<any> | undefined {
    const url = req.urlWithParams;
    const cached = this.cache.get(url);

    if (!cached) {
      return undefined;
    }

    const isExpired = cached.lastRead < (Date.now() - cached.maxAge || maxAge);
    if(!isExpired) {
      console.debug('Serving from local cache', cached);
      return cached.response;
    }
    return undefined;
  }

  put(req: HttpRequest<any>, response: HttpResponse<any>, maxAge?: number): void {
    const url = req.urlWithParams;
    const entry = { url, response, lastRead: Date.now(), maxAge };
    this.cache.set(url, entry);

    const expired = Date.now() - maxAge;
    this.cache.forEach(expiredEntry => {
      if (expiredEntry.lastRead < expired) {
        this.cache.delete(expiredEntry.url);
      }
    });
  }
}
